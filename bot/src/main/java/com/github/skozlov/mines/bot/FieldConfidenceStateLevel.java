package com.github.skozlov.mines.bot;

import com.github.skozlov.mines.commons.matrix.Matrix;
import com.github.skozlov.mines.core.playerPov.FieldPlayerPov;

public interface FieldConfidenceStateLevel {
	Matrix<CellConfidenceState> getCells();

	FieldConfidenceStateLevel update(FieldConfidenceStateLevel previousLevel);

	FieldConfidenceStateLevel update(FieldPlayerPov field);
}
