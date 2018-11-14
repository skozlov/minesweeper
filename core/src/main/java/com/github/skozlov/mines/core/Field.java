package com.github.skozlov.mines.core;

import com.github.skozlov.mines.commons.matrix.Matrix;
import com.github.skozlov.mines.commons.matrix.MatrixCoordinate;
import com.github.skozlov.mines.commons.matrix.MatrixDimension;

import java.util.Set;

public final class Field {
	private final int mineNumber;
	private final Matrix<Cell> cells;

	public Field(MatrixDimension dimension, Set<MatrixCoordinate> mineCoordinates){
		mineNumber = mineCoordinates.size();
		int cellNumber = dimension.getCellNumber();
		if (mineNumber >= cellNumber){
			throw new IllegalArgumentException(String.format("%d cells, %d are mined", cellNumber, mineNumber));
		}
		mineCoordinates.forEach(coordinate -> coordinate.checkFor(dimension));
		cells = Matrix.create(
			dimension,
			coordinate -> mineCoordinates.contains(coordinate)
				? Cell.Mined.INSTANCE
				: new Cell.Free(
					(int) coordinate.getNeighbors(dimension).stream()
						.filter(mineCoordinates::contains)
						.count()
			),
			Cell.class
		);
	}

	public int getMineNumber() {
		return mineNumber;
	}

	public Matrix<Cell> getCells() {
		return cells;
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
		Field that = (Field) obj;
		return this.mineNumber == that.mineNumber && this.cells.equals(that.cells);
	}
}
