package com.github.skozlov.mines.core;

import com.github.skozlov.mines.commons.matrix.MatrixCoordinate;

import java.util.Set;

public interface FieldGenerator {
	default Field generate(FieldParameters parameters){
		return new Field(parameters.getDimension(), generateMineCoordinates(parameters));
	}

	Set<MatrixCoordinate> generateMineCoordinates(FieldParameters parameters);
}
