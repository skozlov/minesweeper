package com.github.skozlov.mines.model;

import com.github.skozlov.mines.core.FieldState;

public interface ModelListener {
	void onChange(FieldState fieldState);
}
