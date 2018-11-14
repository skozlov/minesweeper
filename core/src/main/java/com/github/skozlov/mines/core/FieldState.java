package com.github.skozlov.mines.core;

import com.github.skozlov.mines.commons.Mutable;
import com.github.skozlov.mines.commons.matrix.Matrix;
import com.github.skozlov.mines.commons.matrix.MatrixCoordinate;
import com.github.skozlov.mines.commons.matrix.MatrixDimension;
import com.github.skozlov.mines.core.command.Command;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class FieldState {
	private final Matrix<CellState> cells;
	private final int mineNumber;
	private final int markedAsMinedNumber;
	private final int intactNumber;
	private final boolean won;
	private final boolean failed;

	private FieldState(
		Matrix<CellState> cells,
		int markedAsMinedNumber,
		int mineNumber,
		boolean exploded,
		int intactNumber
	) {
		this.cells = cells;
		this.markedAsMinedNumber = markedAsMinedNumber;
		this.mineNumber = mineNumber;
		this.intactNumber = intactNumber;
		failed = exploded;
		won = !exploded && markedAsMinedNumber == mineNumber && intactNumber == 0;
	}

	public static FieldState allIntact(Field field) {
		MatrixDimension dimension = field.getCells().getDimension();
		return new FieldState(
			Matrix.create(
				dimension,
				coordinate -> new CellState.Intact(field.getCells().get(coordinate)),
				CellState.class
			),
			0,
			field.getMineNumber(),
			false,
			dimension.getCellNumber()
		);
	}

	public Matrix<CellState> getCells() {
		return cells;
	}

	public int getMineNumber() {
		return mineNumber;
	}

	public int getMarkedAsMinedNumber() {
		return markedAsMinedNumber;
	}

	public boolean isGameOver() {
		return won || failed;
	}

	public boolean isWon() {
		return won;
	}

	public FieldState execute(Command command){
		Function<MatrixCoordinate, FieldState> action = command.getType().fold(
			() -> this::open,
			() -> this::openIntactNeighbors,
			() -> this::markAsMined,
			() -> this::unmarkAsMined
		);
		return action.apply(command.getCoordinate());
	}

	public FieldState open(MatrixCoordinate coordinate) {
		return (isGameOver() || cells.get(coordinate).isOpen()) ? this : open(Collections.singleton(coordinate));
	}

	public FieldState openIntactNeighbors(MatrixCoordinate coordinate) {
		CellState cell = cells.get(coordinate);
		if (isGameOver() || !cell.isOpen()){
			return this;
		}
		Collection<MatrixCoordinate> neighbors = coordinate.getNeighbors(cells.getDimension());
		return neighbors.stream().filter(coord -> cells.get(coord).isMarkedAsMined()).count()
			== (long)((Cell.Free)cell.getCell()).getNeighborMineNumber()
			? open(neighbors.stream().filter(coord -> cells.get(coord).isIntact()).collect(Collectors.toList()))
			: this;
	}

	private FieldState open(Collection<MatrixCoordinate> coordinates){
		Mutable<MatrixCoordinate> explodedCoordinate = new Mutable<>(null);
		Collection<MatrixCoordinate> consideredCoordinates = new HashSet<>();
		Queue<MatrixCoordinate> coordinatesToConsider = new LinkedList<>(coordinates);
		MatrixDimension dimension = cells.getDimension();
		while (explodedCoordinate.value == null && !coordinatesToConsider.isEmpty()){
			MatrixCoordinate coordinate = coordinatesToConsider.remove();
			if (consideredCoordinates.contains(coordinate)){
				continue;
			}
			consideredCoordinates.add(coordinate);
			Cell cell = cells.get(coordinate).getCell();
			cell.fold(
				mined -> explodedCoordinate.value = coordinate,
				free -> {
					if (free.getNeighborMineNumber() == 0){
						coordinatesToConsider.addAll(
							coordinate.getNeighbors(dimension).stream()
								.filter(neighbor -> cells.get(neighbor).isIntact())
								.collect(Collectors.toList())
						);
					}
				}
			);
		}
		return explodedCoordinate.value != null
			? new FieldState(
				cells.map((cellState, coordinate) -> {
					Cell cell = cellState.getCell();
					if (cellState.isIntact() && cell.isMined()) {
						return coordinate.equals(explodedCoordinate.value)
							? CellState.Exploded.INSTANCE
							: CellState.Open.of(cell);
					} else if (cellState.isMarkedAsMined() && !cell.isMined()){
						return new CellState.WronglyMarkedAsMined(cell);
					}
					return cellState;
				}),
				cells.get(explodedCoordinate.value).isMarkedAsMined()
					? markedAsMinedNumber - 1
					: markedAsMinedNumber, mineNumber,
				true,
				intactNumber
			)
			: new FieldState(
				cells.map((cellState, coordinate) ->
					consideredCoordinates.contains(coordinate)
						? CellState.Open.of(cellState.getCell())
						: cellState
				),
				markedAsMinedNumber
					- (int)consideredCoordinates.stream().filter(c -> cells.get(c).isMarkedAsMined()).count(),
				mineNumber,
				false,
				intactNumber - consideredCoordinates.size()
			);

	}

	public FieldState markAsMined(MatrixCoordinate coordinate) {
		if (isGameOver()){
			return this;
		}
		CellState cell = cells.get(coordinate);
		if (!cell.isIntact()){
			return this;
		}
		return new FieldState(
			cells.set(coordinate, CellState.MarkedAsMined.of(cell.getCell())),
			markedAsMinedNumber + 1,
			mineNumber,
			false,
			intactNumber - 1
		);
	}

	public FieldState unmarkAsMined(MatrixCoordinate coordinate) {
		if (isGameOver()){
			return this;
		}
		CellState cell = cells.get(coordinate);
		if (!cell.isMarkedAsMined()){
			return this;
		}
		return new FieldState(
			cells.set(coordinate, new CellState.Intact(cell.getCell())),
			markedAsMinedNumber - 1,
			mineNumber,
			false,
			intactNumber + 1
		);
	}

	@Override
	public int hashCode() {
		return cells.hashCode();
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
		return this.mineNumber == that.mineNumber
			&& this.markedAsMinedNumber == that.markedAsMinedNumber
			&& this.intactNumber == that.intactNumber
			&& this.won == that.won
			&& this.failed == that.failed
			&& this.cells.equals(that.cells);
	}
}
