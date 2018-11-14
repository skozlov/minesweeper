package com.github.skozlov.mines.commons.matrix;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class Matrix<T> {
	private final T[][] cells;

	private Matrix(T[][] cells) {
		this.cells = cells;
	}

	public static <T> Matrix<T> create(
		MatrixDimension dimension,
		Function<MatrixCoordinate, T> generator,
		Class<T> cellType
	){
		//noinspection unchecked
		T[][] cells = (T[][]) Array.newInstance(cellType, dimension.getRowNumber(), dimension.getColumnNumber());
		dimension.forEachCoordinate(coordinate ->
			cells[coordinate.getRowIndex()][coordinate.getColumnIndex()] = generator.apply(coordinate)
		);
		return new Matrix<>(cells);
	}

	public MatrixDimension getDimension(){
		return new MatrixDimension(cells.length, cells[0].length);
	}

	public T get(MatrixCoordinate coordinate){
		coordinate.checkFor(getDimension());
		return cells[coordinate.getRowIndex()][coordinate.getColumnIndex()];
	}

	public Matrix<T> map(BiFunction<T, MatrixCoordinate, T> f){
		T[][] cells = copyCells();
		getDimension().forEachCoordinate(coordinate -> {
			int rowIndex = coordinate.getRowIndex();
			int columnIndex = coordinate.getColumnIndex();
			cells[rowIndex][columnIndex] = f.apply(this.cells[rowIndex][columnIndex], coordinate);
		});
		return new Matrix<>(cells);
	}

	public Matrix<T> set(MatrixCoordinate coordinate, T value){
		coordinate.checkFor(getDimension());
		T[][] cells = copyCells();
		cells[coordinate.getRowIndex()][coordinate.getColumnIndex()] = value;
		return new Matrix<>(cells);
	}

	private T[][] copyCells() {
		MatrixDimension dimension = getDimension();
		//noinspection unchecked
		T[][] cells = (T[][]) Array.newInstance(
			this.cells[0].getClass().getComponentType(),
			dimension.getRowNumber(),
			dimension.getColumnNumber()
		);
		for (int i = 0; i < cells.length; i++){
			System.arraycopy(this.cells[i], 0, cells[i], 0, this.cells[i].length);
		}
		return cells;
	}

	@Override
	public int hashCode() {
		return Arrays.deepHashCode(cells);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this
			|| obj != null && obj.getClass().equals(getClass()) && Arrays.deepEquals(((Matrix)obj).cells, cells);
	}
}
