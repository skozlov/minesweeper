package com.github.skozlov.mines.core;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class RandomFieldGenerator implements FieldGenerator {
	@Override
	public Set<MatrixCoordinate> generateMineCoordinates(FieldParameters parameters) {
		List<MatrixCoordinate> coordinates = parameters.getDimension().coordinatesToList();
		Collections.shuffle(coordinates, ThreadLocalRandom.current());
		return new HashSet<>(coordinates.subList(0, parameters.getMineNumber()));
	}
}
