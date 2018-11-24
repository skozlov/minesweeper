package com.github.skozlov.mines.core;

import com.github.skozlov.mines.commons.matrix.Matrix;
import com.github.skozlov.mines.commons.matrix.MatrixCoordinate;
import com.github.skozlov.mines.commons.matrix.MatrixDimension;

import java.util.Set;

public final class Field {
	private final FieldParameters parameters;
	private final Matrix<Cell> cells;

	public Field(MatrixDimension dimension, Set<MatrixCoordinate> mineCoordinates){
		parameters = new FieldParameters(dimension, mineCoordinates.size());
		int cellNumber = dimension.getCellNumber();
		if (parameters.getMineNumber() >= cellNumber){
			throw new IllegalArgumentException(String.format(
				"%d cells, %d are mined",
				cellNumber, parameters.getMineNumber())
			);
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

	public Matrix<Cell> getCells() {
		return cells;
	}

	public FieldParameters getParameters() {
		return parameters;
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
		return this.parameters.equals(that.parameters) && this.cells.equals(that.cells);
	}
}
