package com.github.skozlov.mines.core;

import java.util.List;

public class MatrixCoordinate {
	public MatrixCoordinate(int rowIndex, int columnIndex) {
		//todo
	}

	@Override
	public int hashCode() {

	}

	@Override
	public boolean equals(Object obj) {

	}

	public int getRowIndex() {

	}

	public int getColumnIndex() {

	}

	public List<MatrixCoordinate> getNeighbors(MatrixDimension matrixDimension) {

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
}
