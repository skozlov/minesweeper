package com.github.skozlov.mines.core;

import java.util.function.Consumer;

public class Cell {
	public boolean isMined() {

	}

	public void fold(Consumer<Mined> mined, Consumer<Free> free) {
		//todo
	}

	public static class Mined extends Cell {
	}

	public static class Free extends Cell {
		public int getNeighborMineNumber() {

		}
	}
}
