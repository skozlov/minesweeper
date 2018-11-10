package com.github.skozlov.mines.core;

import java.util.function.Consumer;

public class CellState {
	public boolean isMarkedAsMined() {

	}

	public void fold(
		Consumer<Intact> intact,
		Consumer<MarkedAsMined> markedAsMined,
		Consumer<WronglyMarkedAsMined> wronglyMarkedAsMined,
		Consumer<Open> open,
		Consumer<Exploded> exploded
	){
		//todo
	}

	public Cell getCell() {

	}

	public boolean isOpen() {

	}

	public boolean isIntact() {

	}

	@Override
	public int hashCode() {

	}

	@Override
	public boolean equals(Object obj) {

	}

	public static class Intact extends CellState {
		public Intact(Cell cell) {
			//todo
		}
	}

	public static class MarkedAsMined extends CellState {
		public static CellState of(Cell cell) {

		}
	}

	public static class WronglyMarkedAsMined extends MarkedAsMined{
		public WronglyMarkedAsMined(Cell cell) {
			//todo
		}
	}

	public static class Open extends CellState {
		public static CellState of(Cell cell) {

		}
	}

	public static class Exploded extends Open {
		public static final CellState INSTANCE = ;
	}
}
