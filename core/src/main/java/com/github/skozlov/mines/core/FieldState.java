package com.github.skozlov.mines.core;

import com.github.skozlov.mines.commons.Mutable;

import java.util.*;
import java.util.stream.Collectors;

public final class FieldState {
	private final CellState[][] cells;
	private final MatrixDimension dimension;
	private final int mineNumber;
	private final int markedAsMinedNumber;
	private final int intactNumber;
	private final boolean won;
	private final boolean failed;

	private FieldState(
		CellState[][] cells,
		int markedAsMinedNumber,
		int mineNumber,
		boolean exploded,
		int intactNumber
	) {
		this.cells = cells;
		dimension = new MatrixDimension(cells.length, cells[0].length);
		this.markedAsMinedNumber = markedAsMinedNumber;
		this.mineNumber = mineNumber;
		this.intactNumber = intactNumber;
		failed = exploded;
		won = !exploded && markedAsMinedNumber == mineNumber && intactNumber == 0;
	}

	public static FieldState allIntact(Field field) {
		MatrixDimension dimension = field.getDimension();
		CellState[][] cells = new CellState[dimension.getRowNumber()][];
		int columnNumber = dimension.getColumnNumber();
		for (int rowIndex = 0; rowIndex < cells.length; rowIndex++){
			cells[rowIndex] = new CellState[columnNumber];
		}
		dimension.forEachCoordinate(coordinate ->
			cells[coordinate.getRowIndex()][coordinate.getColumnIndex()] =
				new CellState.Intact(field.getCell(coordinate))
		);
		return new FieldState(
			cells,
			0,
			field.getMineNumber(),
			false,
			dimension.getCellNumber()
		);
	}

	public MatrixDimension getDimension() {
		return dimension;
	}

	public int getMineNumber() {
		return mineNumber;
	}

	public int getMarkedAsMinedNumber() {
		return markedAsMinedNumber;
	}

	public CellState getCell(MatrixCoordinate coordinate) {
		coordinate.checkFor(dimension);
		return getCellNoChecks(coordinate);
	}

	private CellState getCellNoChecks(MatrixCoordinate coordinate) {
		return cells[coordinate.getRowIndex()][coordinate.getColumnIndex()];
	}

	public boolean isGameOver() {
		return won || failed;
	}

	public boolean isWon() {
		return won;
	}

	public FieldState open(MatrixCoordinate coordinate) {
		return (isGameOver() || getCell(coordinate).isOpen()) ? this : open(Collections.singleton(coordinate));
	}

	public FieldState openIntactNeighbors(MatrixCoordinate coordinate) {
		CellState cell = getCell(coordinate);
		if (isGameOver() || !cell.isOpen()){
			return this;
		}
		Collection<MatrixCoordinate> neighbors = coordinate.getNeighbors(dimension);
		return neighbors.stream().map(this::getCellNoChecks).filter(CellState::isMarkedAsMined).count()
			== (long)((Cell.Free)cell.getCell()).getNeighborMineNumber()
			? open(neighbors.stream().filter(coord -> getCellNoChecks(coord).isIntact()).collect(Collectors.toList()))
			: this;
	}

	private FieldState open(Collection<MatrixCoordinate> coordinates){
		Mutable<MatrixCoordinate> explodedCoordinate = new Mutable<>(null);
		Collection<MatrixCoordinate> consideredCoordinates = new HashSet<>();
		Queue<MatrixCoordinate> coordinatesToConsider = new LinkedList<>(coordinates);
		while (explodedCoordinate.value == null && !coordinatesToConsider.isEmpty()){
			MatrixCoordinate coordinate = coordinatesToConsider.remove();
			if (consideredCoordinates.contains(coordinate)){
				continue;
			}
			consideredCoordinates.add(coordinate);
			Cell cell = getCellNoChecks(coordinate).getCell();
			cell.fold(
				mined -> explodedCoordinate.value = coordinate,
				free -> {
					if (free.getNeighborMineNumber() == 0){
						coordinatesToConsider.addAll(
							coordinate.getNeighbors(dimension).stream()
								.filter(neighbor -> getCellNoChecks(neighbor).isIntact())
								.collect(Collectors.toList())
						);
					}
				}
			);
		}
		CellState[][] cells = copyCells();
		if (explodedCoordinate.value != null){
			dimension.forEachCoordinate(coordinate -> {
				int rowIndex = coordinate.getRowIndex();
				int columnIndex = coordinate.getColumnIndex();
				CellState cellState = cells[rowIndex][columnIndex];
				Cell cell = cellState.getCell();
				if (cellState.isIntact()){
					cells[rowIndex][columnIndex] = cell.isMined()
						? coordinate.equals(explodedCoordinate.value)
							? CellState.Exploded.INSTANCE
							: CellState.Open.of(cell)
						: cellState;
				} else if (cellState.isMarkedAsMined() && !cell.isMined()){
					cells[rowIndex][columnIndex] = new CellState.WronglyMarkedAsMined(cell);
				}
			});
			return new FieldState(
				cells,
				getCellNoChecks(explodedCoordinate.value).isMarkedAsMined()
					? markedAsMinedNumber - 1
					: markedAsMinedNumber, mineNumber,
				true,
				intactNumber
			);
		}
		consideredCoordinates.forEach(coordinate -> {
			int rowIndex = coordinate.getRowIndex();
			int columnIndex = coordinate.getColumnIndex();
			cells[rowIndex][columnIndex] = CellState.Open.of(cells[rowIndex][columnIndex].getCell());
		});
		return new FieldState(
			cells,
			markedAsMinedNumber
				- (int)consideredCoordinates.stream().filter(c -> getCellNoChecks(c).isMarkedAsMined()).count(),
			mineNumber,
			false,
			intactNumber - consideredCoordinates.size()
		);
	}

	public FieldState markAsMined(MatrixCoordinate coordinate) {
		if (isGameOver()){
			return this;
		}
		CellState cell = getCell(coordinate);
		if (!cell.isIntact()){
			return this;
		}
		CellState[][] cells = copyCells();
		cells[coordinate.getRowIndex()][coordinate.getColumnIndex()] = CellState.MarkedAsMined.of(cell.getCell());
		return new FieldState(
			cells,
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
		CellState cell = getCell(coordinate);
		if (!cell.isMarkedAsMined()){
			return this;
		}
		CellState[][] cells = copyCells();
		cells[coordinate.getRowIndex()][coordinate.getColumnIndex()] = new CellState.Intact(cell.getCell());
		return new FieldState(
			cells,
			markedAsMinedNumber - 1,
			mineNumber,
			false,
			intactNumber + 1
		);
	}

	private CellState[][] copyCells() {
		CellState[][] cells = new CellState[this.cells.length][];
		for (int i = 0; i < cells.length; i++){
			cells[i] = Arrays.copyOf(this.cells[i], this.cells[i].length);
		}
		return cells;
	}

	@Override
	public int hashCode() {
		return Arrays.deepHashCode(cells);
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
			&& Arrays.deepEquals(this.cells, that.cells);
	}
}
