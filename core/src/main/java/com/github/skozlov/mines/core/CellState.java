package com.github.skozlov.mines.core;

import java.util.Objects;
import java.util.function.Consumer;

public abstract class CellState {
	private final Cell cell;

	private CellState(Cell cell) {
		this.cell = cell;
	}

	public Cell getCell() {
		return cell;
	}

	public boolean isIntact() {
		return false;
	}

	public boolean isOpen() {
		return false;
	}

	public boolean isMarkedAsMined() {
		return false;
	}

	public abstract void fold(
		Consumer<Intact> intact,
		Consumer<MarkedAsMined> markedAsMined,
		Consumer<WronglyMarkedAsMined> wronglyMarkedAsMined,
		Consumer<Open> open,
		Consumer<Exploded> exploded
	);

	@Override
	public int hashCode() {
		return Objects.hash(cell, getClass());
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj != null && obj.getClass().equals(getClass()) && ((CellState)obj).cell.equals(cell);
	}

	public static final class Intact extends CellState {
		public Intact(Cell cell) {
			super(cell);
		}

		@Override
		public boolean isIntact() {
			return true;
		}

		@Override
		public void fold(
			Consumer<Intact> intact,
			Consumer<MarkedAsMined> markedAsMined,
			Consumer<WronglyMarkedAsMined> wronglyMarkedAsMined,
			Consumer<Open> open,
			Consumer<Exploded> exploded
		) {
			intact.accept(this);
		}
	}

	public static class MarkedAsMined extends CellState {
		private MarkedAsMined(Cell cell) {
			super(cell);
		}

		public static CellState of(Cell cell) {
			return new MarkedAsMined(cell);
		}

		@Override
		public boolean isMarkedAsMined() {
			return true;
		}

		@Override
		public void fold(
			Consumer<Intact> intact,
			Consumer<MarkedAsMined> markedAsMined,
			Consumer<WronglyMarkedAsMined> wronglyMarkedAsMined,
			Consumer<Open> open,
			Consumer<Exploded> exploded
		) {
			markedAsMined.accept(this);
		}
	}

	public static final class WronglyMarkedAsMined extends MarkedAsMined{
		public WronglyMarkedAsMined(Cell cell) {
			super(cell);
		}

		@Override
		public void fold(
			Consumer<Intact> intact,
			Consumer<MarkedAsMined> markedAsMined,
			Consumer<WronglyMarkedAsMined> wronglyMarkedAsMined,
			Consumer<Open> open,
			Consumer<Exploded> exploded
		) {
			wronglyMarkedAsMined.accept(this);
		}
	}

	public static class Open extends CellState {
		private Open(Cell cell) {
			super(cell);
		}

		public static Open of(Cell cell) {
			return new Open(cell);
		}

		@Override
		public boolean isOpen() {
			return true;
		}

		@Override
		public void fold(
			Consumer<Intact> intact,
			Consumer<MarkedAsMined> markedAsMined,
			Consumer<WronglyMarkedAsMined> wronglyMarkedAsMined,
			Consumer<Open> open,
			Consumer<Exploded> exploded
		) {
			open.accept(this);
		}
	}

	public static final class Exploded extends Open {
		public static final Exploded INSTANCE = new Exploded(Cell.Mined.INSTANCE);

		private Exploded(Cell cell) {
			super(cell);
		}

		@Override
		public void fold(
			Consumer<Intact> intact,
			Consumer<MarkedAsMined> markedAsMined,
			Consumer<WronglyMarkedAsMined> wronglyMarkedAsMined,
			Consumer<Open> open,
			Consumer<Exploded> exploded
		) {
			exploded.accept(this);
		}
	}
}
