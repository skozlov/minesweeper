package com.github.skozlov.mines.core;

import com.github.skozlov.mines.commons.Mutable;
import com.github.skozlov.mines.commons.matrix.Matrix;
import com.github.skozlov.mines.commons.matrix.MatrixCoordinate;
import com.github.skozlov.mines.commons.matrix.MatrixDimension;
import com.github.skozlov.mines.core.command.Command;
import com.github.skozlov.mines.core.playerPov.FieldPlayerPov;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class FieldState {
	private final Field field;
	private final FieldPlayerPov playerPov;

	private FieldState(Field field, FieldPlayerPov playerPov) {
		this.field = field;
		this.playerPov = playerPov;
	}

	public static FieldState allIntact(Field field){
		return new FieldState(field, FieldPlayerPov.allIntact(field));
	}

	public Field getField() {
		return field;
	}

	public FieldPlayerPov getPlayerPov() {
		return playerPov;
	}

	public FieldState execute(Command command){
		Function<MatrixCoordinate, FieldState> action = command.getType().fold(
			() -> this::open,
			() -> this::openIntactNeighbors,
			() -> coordinate -> withPlayerPov(playerPov.markAsMined(coordinate)),
			() -> coordinate -> withPlayerPov(playerPov.unmarkAsMined(coordinate))
		);
		return action.apply(command.getCoordinate());
	}

	private FieldState open(MatrixCoordinate coordinate){
		return playerPov.isGameOver() || playerPov.getCells().get(coordinate).isOpen()
			? this
			: open(Collections.singleton(coordinate));
	}

	private FieldState openIntactNeighbors(MatrixCoordinate coordinate){
		if (playerPov.isGameOver() || !playerPov.getCells().get(coordinate).isOpen()){
			return this;
		}
		Collection<MatrixCoordinate> neighbors = coordinate.getNeighbors(field.getCells().getDimension());
		int markedNeighborNumber = (int) neighbors.stream()
			.filter(coord -> playerPov.getCells().get(coord).isMarkedAsMined())
			.count();
		int neighborMineNumber = ((Cell.Free) field.getCells().get(coordinate)).getNeighborMineNumber();
		return markedNeighborNumber == neighborMineNumber
			? open(
				neighbors.stream()
					.filter(coord -> playerPov.getCells().get(coord).isIntact())
					.collect(Collectors.toList())
			)
			: this;
	}

	private FieldState open(Collection<MatrixCoordinate> coordinates){
		Mutable<MatrixCoordinate> explodedCoordinate = new Mutable<>(null);
		Collection<MatrixCoordinate> consideredCoordinates = new HashSet<>();
		Queue<MatrixCoordinate> coordinatesToConsider = new LinkedList<>(coordinates);
		Matrix<Cell> cells = field.getCells();
		MatrixDimension dimension = cells.getDimension();
		while (explodedCoordinate.value == null && !coordinatesToConsider.isEmpty()){
			MatrixCoordinate coordinate = coordinatesToConsider.remove();
			if (consideredCoordinates.contains(coordinate)){
				continue;
			}
			consideredCoordinates.add(coordinate);
			cells.get(coordinate).fold(
				mined -> explodedCoordinate.value = coordinate,
				free -> {
					if (free.getNeighborMineNumber() == 0){
						coordinatesToConsider.addAll(
							coordinate.getNeighbors(dimension).stream()
								.filter(neighbor -> playerPov.getCells().get(neighbor).isIntact())
								.collect(Collectors.toList())
						);
					}
				}
			);
		}
		return withPlayerPov(
			explodedCoordinate.value == null
				? playerPov.openFree(
					consideredCoordinates.stream().collect(Collectors.toMap(
						Function.identity(),
						coordinate -> (Cell.Free)cells.get(coordinate)
					))
				)
				: playerPov.explode(
					dimension.coordinatesToList().stream()
						.filter(coordinate -> field.getCells().get(coordinate).isMined())
						.collect(Collectors.toSet()),
					explodedCoordinate.value
				)
		);
	}

	private FieldState withPlayerPov(FieldPlayerPov playerPov){
		return new FieldState(field, playerPov);
	}

	@Override
	public int hashCode() {
		return Objects.hash(field, playerPov);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this){
			return true;
		}
		if (obj == null || !obj.getClass().equals(getClass())){
			return false;
		}
		FieldState that = (FieldState) obj;
		return this.field.equals(that.field) && this.playerPov.equals(that.playerPov);
	}
}
