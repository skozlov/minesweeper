package com.github.skozlov.mines.core;

import java.util.List;
import java.util.Optional;

public class FieldState {
	public static FieldState allIntact(Field field) {

	}

	public int getMarkedAsMinedNumber() {

	}

	public int getMineNumber() {

	}

	public int getRowNumber() {

	}

	public int getColumnNumber() {

	}

	public CellState getCell(MatrixCoordinate coordinate) {

	}

	public List<MatrixCoordinate> getCoordinates() {

	}

	public FieldState open(MatrixCoordinate coordinate) {

	}

	public boolean isGameOver() {

	}

	public FieldState openIntactNeighbors(MatrixCoordinate coordinate) {

	}

	public FieldState markAsMined(MatrixCoordinate coordinate) {

	}

	public FieldState unmarkAsMined(MatrixCoordinate coordinate) {

	}
}
