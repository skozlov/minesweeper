package com.github.skozlov.mines.core;

import java.util.Arrays;
import java.util.Set;

public final class Field {
	private final Cell[][] cells;
	private final MatrixDimension dimension;
	private final int mineNumber;

	public Field(MatrixDimension dimension, Set<MatrixCoordinate> mineCoordinates){
		this.dimension = dimension;
		mineNumber = mineCoordinates.size();
		int cellNumber = dimension.getCellNumber();
		if (mineNumber >= cellNumber){
			throw new IllegalArgumentException(String.format("%d cells, %d are mined", cellNumber, mineNumber));
		}
		int columnNumber = dimension.getColumnNumber();
		cells = new Cell[dimension.getRowNumber()][];
		for (int rowIndex = 0; rowIndex < cells.length; rowIndex++){
			cells[rowIndex] = new Cell[columnNumber];
		}
		mineCoordinates.forEach(coordinate -> {
			coordinate.checkFor(dimension);
			cells[coordinate.getRowIndex()][coordinate.getColumnIndex()] = Cell.Mined.INSTANCE;
		});
		dimension.forEachCoordinate(coordinate -> {
			int rowIndex = coordinate.getRowIndex();
			int columnIndex = coordinate.getColumnIndex();
			if (cells[rowIndex][columnIndex] == null){
				cells[rowIndex][columnIndex] = new Cell.Free(
					(int) coordinate.getNeighbors(dimension).stream()
						.filter(c -> {
							Cell cell = cells[c.getRowIndex()][c.getColumnIndex()];
							return cell != null && cell.isMined();
						})
						.count()
				);
			}
		});
	}

	public MatrixDimension getDimension() {
		return dimension;
	}

	public int getMineNumber() {
		return mineNumber;
	}

	public Cell getCell(MatrixCoordinate coordinate) {
		coordinate.checkFor(dimension);
		return cells[coordinate.getRowIndex()][coordinate.getColumnIndex()];
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
		Field that = (Field) obj;
		return this.mineNumber == that.mineNumber && this.cells == that.cells;
	}
}
