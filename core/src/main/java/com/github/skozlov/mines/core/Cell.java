package com.github.skozlov.mines.core;

import java.util.function.Consumer;

public class Cell {
	public boolean isMined() {

	}

	public void fold(Consumer<Mined> mined, Consumer<Free> free) {
		//todo
	}

	@Override
	public int hashCode() {

	}

	@Override
	public boolean equals(Object obj) {

	}

	public static class Mined extends Cell {
		public static final Mined INSTANCE = ;
	}

	public static class Free extends Cell {
		public int getNeighborMineNumber() {

		}
	}
}
