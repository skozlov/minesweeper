package com.github.skozlov.mines.core;

import java.util.Arrays;
import java.util.stream.Stream;

public final class Field {
	private final Cell[][] cells;
	private final MatrixDimension dimension;
	private final int mineNumber;

	public Field(Cell[][] cells){
		if (cells.length == 0){
			throw new IllegalArgumentException("Empty array");
		}
		int columnNumber = cells[0].length;
		if (columnNumber == 0){
			throw new IllegalArgumentException("Empty first row");
		}
		dimension = new MatrixDimension(cells.length, columnNumber);
		int mineNumber = 0;
		this.cells = new Cell[cells.length][];
		for (int rowIndex = 0; rowIndex < cells.length; rowIndex++){
			Cell[] row = cells[rowIndex];
			if (row.length != columnNumber){
				throw new IllegalArgumentException(String.format(
					"At least 2 rows of different size: %d and %d",
					columnNumber, row.length
				));
			}
			this.cells[rowIndex] = Arrays.copyOf(row, row.length);
			mineNumber += Stream.of(row).filter(Cell::isMined).count();
		}
		if (mineNumber == dimension.getCellNumber()){
			throw new IllegalArgumentException("All cells are mined");
		}
		this.mineNumber = mineNumber;
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
