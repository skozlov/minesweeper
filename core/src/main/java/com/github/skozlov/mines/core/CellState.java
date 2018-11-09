package com.github.skozlov.mines.core;

import java.util.function.Consumer;

public class CellState {
	public boolean isMarkedAsMined() {

	}

	public void fold(
		Consumer<Intact> intact,
		Consumer<MarkedAsMined> markedAsMined,
		Consumer<Open> open,
		Consumer<Exploded> exploded
	){
		//todo
	}

	public Cell getCell() {

	}

	public static class Intact extends CellState {
	}

	public static class MarkedAsMined extends CellState {
	}

	public static class Open extends CellState {
	}

	public static class Exploded extends Open {
	}
}
