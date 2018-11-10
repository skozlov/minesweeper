package com.github.skozlov.mines.core;

import java.util.Objects;

public final class FieldParameters {
	private final MatrixDimension dimension;
	private final int mineNumber;

	public FieldParameters(MatrixDimension dimension, int mineNumber) {
		if (mineNumber < 0){
			throw new IllegalArgumentException("No mines");
		}
		int cellNumber = dimension.getCellNumber();
		if (mineNumber >= cellNumber){
			throw new IllegalArgumentException(String.format(
				"Field of %d cannot contain %d mines",
				cellNumber, mineNumber
			));
		}
		this.dimension = dimension;
		this.mineNumber = mineNumber;
	}

	public MatrixDimension getDimension() {
		return dimension;
	}

	public int getMineNumber() {
		return mineNumber;
	}

	@Override
	public int hashCode() {
		return Objects.hash(dimension, mineNumber);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this){
			return true;
		}
		if (obj == null || !obj.getClass().equals(getClass())){
			return false;
		}
		FieldParameters that = (FieldParameters) obj;
		return this.mineNumber == that.mineNumber && this.dimension.equals(that.dimension);
	}
}
