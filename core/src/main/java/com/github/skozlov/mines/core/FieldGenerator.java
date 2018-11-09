package com.github.skozlov.mines.core;

import java.util.Set;

public interface FieldGenerator {
	default Field generate(FieldParameters parameters){

	}

	Set<MatrixCoordinate> generateMineCoordinates(FieldParameters parameters);
}
