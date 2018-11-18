package com.github.skozlov.mines.core.playerPov;

import com.github.skozlov.mines.commons.Mutable;
import com.github.skozlov.mines.commons.matrix.Matrix;
import com.github.skozlov.mines.commons.matrix.MatrixCoordinate;
import com.github.skozlov.mines.commons.matrix.MatrixDimension;
import com.github.skozlov.mines.core.Cell;
import com.github.skozlov.mines.core.Field;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class FieldPlayerPov {
	private final Matrix<CellPlayerPov> cells;
	private final int mineNumber;
	private final int markedAsMinedNumber;
	private final int intactNumber;
	private final boolean exploded;

	private FieldPlayerPov(
		Matrix<CellPlayerPov> cells,
		int mineNumber,
		int markedAsMinedNumber,
		int intactNumber,
		boolean exploded
	) {
		this.cells = cells;
		this.mineNumber = mineNumber;
		this.markedAsMinedNumber = markedAsMinedNumber;
		this.intactNumber = intactNumber;
		this.exploded = exploded;
	}

	public static FieldPlayerPov allIntact(Field field) {
		MatrixDimension dimension = field.getCells().getDimension();
		return new FieldPlayerPov(
			Matrix.create(dimension, c -> CellPlayerPov.Intact.INSTANCE, CellPlayerPov.class),
			field.getMineNumber(),
			0,
			dimension.getCellNumber(),
			false
		);
	}

	public Matrix<CellPlayerPov> getCells() {
		return cells;
	}

	public int getMineNumberLeft(){
		return mineNumber - markedAsMinedNumber;
	}

	public boolean isWon() {
		return !exploded && intactNumber == 0 && getMineNumberLeft() == 0;
	}

	public boolean isGameOver(){
		return exploded || isWon();
	}

	public FieldPlayerPov openFree(Map<MatrixCoordinate, Cell.Free> cells){
		if (isGameOver() || cells.isEmpty()){
			return this;
		}
		return set(
			cells.entrySet().stream().collect(Collectors.toMap(
				Map.Entry::getKey,
				entry -> new CellPlayerPov.Open(entry.getValue())
			))
		);
	}

	public FieldPlayerPov explode(Set<MatrixCoordinate> mined, MatrixCoordinate exploded){
		if (isGameOver()){
			return this;
		}
		if (!mined.contains(exploded)){
			throw new IllegalArgumentException(String.format(
				"Cell %s cannot be exploded because it is not mined",
				exploded
			));
		}
		Set<MatrixCoordinate> marked = cells.collectCoordinates((c, cell) -> cell.isMarkedAsMined());
		Map<MatrixCoordinate, CellPlayerPov> wronglyMarked = marked.stream()
			.filter(coordinate -> !mined.contains(coordinate))
			.collect(Collectors.toMap(
				Function.identity(),
				c -> CellPlayerPov.WronglyMarkedAsMined.INSTANCE
			));
		Map<MatrixCoordinate, CellPlayerPov> newMined = mined.stream()
			.filter(coordinate -> !marked.contains(coordinate))
			.collect(Collectors.toMap(
				Function.identity(),
				coordinate -> coordinate.equals(exploded)
					? CellPlayerPov.Exploded.INSTANCE
					: CellPlayerPov.Mined.INSTANCE
			));
		Map<MatrixCoordinate, CellPlayerPov> all = new HashMap<>(
			wronglyMarked.size() + newMined.size(),
			1
		);
		all.putAll(wronglyMarked);
		all.putAll(newMined);
		return set(all);
	}

	public FieldPlayerPov markAsMined(MatrixCoordinate coordinate) {
		return set(Collections.singletonMap(coordinate, CellPlayerPov.MarkedAsMined.INSTANCE));
	}

	public FieldPlayerPov unmarkAsMined(MatrixCoordinate coordinate) {
		return set(Collections.singletonMap(coordinate, CellPlayerPov.Intact.INSTANCE));
	}

	@SuppressWarnings("WeakerAccess")
	public FieldPlayerPov set(Map<MatrixCoordinate, CellPlayerPov> cells){
		if (isGameOver() || cells.isEmpty()){
			return this;
		}
		Mutable<Integer> markedAsMinedNumber = new Mutable<>(this.markedAsMinedNumber);
		Mutable<Integer> intactNumber = new Mutable<>(this.intactNumber);
		Mutable<MatrixCoordinate> explodedCoordinate = new Mutable<>(null);
		cells.forEach((coordinate, newCell) -> {
			CellPlayerPov oldCell = this.cells.get(coordinate);
			newCell.fold(
				intact -> {
					if (oldCell.isOpen()){
						throw new IllegalStateException(String.format("Open cell %s cannot become intact", coordinate));
					}
				},
				open -> {
					if (oldCell.isOpen() && !oldCell.equals(newCell)){
						throw new IllegalStateException(String.format("Open cell %s cannot be modified", coordinate));
					}
				},
				markedAsMined -> {
					if (oldCell.isOpen()){
						throw new IllegalStateException(String.format("Open cell %s cannot be marked", coordinate));
					}
				},
				exploded -> {
					if (explodedCoordinate.value != null){
						throw new IllegalStateException(String.format(
							"Multiple cells (%s and %s) cannot be exploded",
							explodedCoordinate.value, coordinate
						));
					}
					if (oldCell.isOpen()){
						throw new IllegalStateException(String.format("Open cell %s cannot be exploded", coordinate));
					}
					explodedCoordinate.value = coordinate;
				},
				mined -> {
					if (oldCell.isOpen()){
						throw new IllegalStateException(String.format("Open cell %s cannot be mined", coordinate));
					}
				},
				wronglyMarkedAsMined -> {
					if (!oldCell.equals(CellPlayerPov.MarkedAsMined.INSTANCE)){
						throw new IllegalStateException(String.format(
							"Not marked cell %s cannot be wrongly marked",
							coordinate
						));
					}
				}
			);
			if (!oldCell.isMarkedAsMined() && newCell.isMarkedAsMined()){
				markedAsMinedNumber.value++;
			} else if (oldCell.isMarkedAsMined() && !newCell.isMarkedAsMined()){
				markedAsMinedNumber.value--;
			}
			if (!oldCell.isIntact() && newCell.isIntact()){
				intactNumber.value++;
			} else if (oldCell.isIntact() && !newCell.isIntact()){
				intactNumber.value--;
			}
		});
		return new FieldPlayerPov(
			this.cells.map((cell, coordinate) -> cells.getOrDefault(coordinate, cell)),
			mineNumber,
			markedAsMinedNumber.value,
			intactNumber.value,
			explodedCoordinate.value != null
		);
	}
}
