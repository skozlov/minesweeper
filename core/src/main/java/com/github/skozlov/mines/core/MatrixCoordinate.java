package com.github.skozlov.mines.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.max;
import static java.lang.Math.min;

public final class MatrixCoordinate {
	private final int rowIndex;
	private final int columnIndex;

	public MatrixCoordinate(int rowIndex, int columnIndex) {
		if (rowIndex < 0){
			throw new IllegalArgumentException(String.format("Negative row index %d", rowIndex));
		}
		if (columnIndex < 0){
			throw new IllegalArgumentException(String.format("Negative column index %d", columnIndex));
		}
		this.rowIndex = rowIndex;
		this.columnIndex = columnIndex;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public List<MatrixCoordinate> getNeighbors(MatrixDimension matrixDimension) {
		checkFor(matrixDimension);
		int minRowIndex = max(0, rowIndex - 1);
		int minColumnIndex = max(0, columnIndex - 1);
		int maxRowIndex = min(matrixDimension.getRowNumber() - 1, rowIndex + 1);
		int maxColumnIndex = min(matrixDimension.getColumnNumber() - 1, columnIndex + 1);
		List<MatrixCoordinate> neighbors = new ArrayList<>(
			(maxRowIndex - minRowIndex + 1) * (maxColumnIndex - minColumnIndex + 1) - 1
		);
		for (int rowIndex = minRowIndex; rowIndex <= maxRowIndex; rowIndex++){
			for (int columnIndex = minColumnIndex; columnIndex <= maxColumnIndex; columnIndex++){
				if (!(rowIndex == this.rowIndex && columnIndex == this.columnIndex)){
					neighbors.add(new MatrixCoordinate(rowIndex, columnIndex));
				}
			}
		}
		return neighbors;
	}

	public void checkFor(MatrixDimension dimension){
		int rowIndex = getRowIndex();
		int rowNumber = dimension.getRowNumber();
		if (rowIndex >= rowNumber){
			throw new IllegalArgumentException(String.format("Illegal row index %d for %d rows", rowIndex, rowNumber));
		}
		int columnIndex = getColumnIndex();
		int columnNumber = dimension.getColumnNumber();
		if (columnIndex >= columnNumber){
			throw new IllegalArgumentException(String.format(
				"Illegal column index %d for %d columns",
				columnIndex, columnNumber
			));
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(rowIndex, columnIndex);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this){
			return true;
		}
		if (obj == null || !obj.getClass().equals(getClass())){
			return false;
		}
		MatrixCoordinate that = (MatrixCoordinate) obj;
		return this.rowIndex == that.rowIndex && this.columnIndex == that.columnIndex;
	}
}
