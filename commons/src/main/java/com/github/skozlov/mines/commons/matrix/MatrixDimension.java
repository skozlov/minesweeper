package com.github.skozlov.mines.commons.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class MatrixDimension {
	private final int rowNumber;
	private final int columnNumber;
	private final List<MatrixCoordinate> coordinates;

	public MatrixDimension(int rowNumber, int columnNumber) {
		if (rowNumber <= 0){
			throw new IllegalArgumentException(String.format("Illegal row number %d", rowNumber));
		}
		if (columnNumber <= 0){
			throw new IllegalArgumentException(String.format("Illegal column number %d", columnNumber));
		}
		this.rowNumber = rowNumber;
		this.columnNumber = columnNumber;
		coordinates = new ArrayList<>(rowNumber * columnNumber);
		for (int rowIndex = 0; rowIndex < rowNumber; rowIndex++){
			for (int columnIndex = 0; columnIndex < columnNumber; columnIndex++){
				coordinates.add(new MatrixCoordinate(rowIndex, columnIndex));
			}
		}
	}

	public int getRowNumber() {
		return rowNumber;
	}

	public int getColumnNumber() {
		return columnNumber;
	}

	public int getCellNumber() {
		return rowNumber * columnNumber;
	}

	public void forEachCoordinate(Consumer<MatrixCoordinate> action){
		coordinates.forEach(action);
	}

	public List<MatrixCoordinate> coordinatesToList() {
		return new ArrayList<>(coordinates);
	}

	@Override
	public int hashCode() {
		return Objects.hash(rowNumber, columnNumber);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this){
			return true;
		}
		if (obj == null || !obj.getClass().equals(getClass())){
			return false;
		}
		MatrixDimension that = (MatrixDimension) obj;
		return this.rowNumber == that.rowNumber && this.columnNumber == that.columnNumber;
	}

	public int getMaxColumnIndex() {
		return columnNumber - 1;
	}
}
