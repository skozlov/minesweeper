package com.github.skozlov.mines.bot;

import com.github.skozlov.mines.core.Cell;

import java.util.function.Consumer;

public abstract class CellConfidenceState {
	private CellConfidenceState() {
	}

	public abstract boolean isUnknown();

	public abstract boolean isMined();

	public abstract boolean isFree();

	public abstract boolean isOpen();

	public abstract void fold(
		Consumer<Unknown> unknown,
		Consumer<Mined> mined,
		Consumer<Free> free,
		Consumer<Open> open
	);

	public static final class Unknown extends CellConfidenceState {
		private Unknown() {
		}

		@Override
		public boolean isUnknown() {
			return true;
		}

		@Override
		public boolean isMined() {
			return false;
		}

		@Override
		public boolean isFree() {
			return false;
		}

		@Override
		public boolean isOpen() {
			return false;
		}

		@Override
		public void fold(
			Consumer<Unknown> unknown,
			Consumer<Mined> mined,
			Consumer<Free> free,
			Consumer<Open> open
		) {
			unknown.accept(this);
		}

		public static final Unknown INSTANCE = new Unknown();
	}

	public static final class Mined extends CellConfidenceState {
		private Mined() {
		}

		@Override
		public boolean isUnknown() {
			return false;
		}

		@Override
		public boolean isMined() {
			return true;
		}

		@Override
		public boolean isFree() {
			return false;
		}

		@Override
		public boolean isOpen() {
			return false;
		}

		@Override
		public void fold(
			Consumer<Unknown> unknown,
			Consumer<Mined> mined,
			Consumer<Free> free,
			Consumer<Open> open
		) {
			mined.accept(this);
		}

		public static final Mined INSTANCE = new Mined();
	}

	public static final class Free extends CellConfidenceState {
		private Free() {
		}

		@Override
		public boolean isUnknown() {
			return false;
		}

		@Override
		public boolean isMined() {
			return false;
		}

		@Override
		public boolean isFree() {
			return true;
		}

		@Override
		public boolean isOpen() {
			return false;
		}

		@Override
		public void fold(
			Consumer<Unknown> unknown,
			Consumer<Mined> mined,
			Consumer<Free> free,
			Consumer<Open> open
		) {
			free.accept(this);
		}

		public static final Free INSTANCE = new Free();
	}

	public static final class Open extends CellConfidenceState {
		private final int neighborMineNumber;

		public Open(Cell.Free cell) {
			neighborMineNumber = cell.getNeighborMineNumber();
		}

		public int getNeighborMineNumber() {
			return neighborMineNumber;
		}

		@Override
		public void fold(
			Consumer<Unknown> unknown,
			Consumer<Mined> mined,
			Consumer<Free> free,
			Consumer<Open> open
		) {
			open.accept(this);
		}

		@Override
		public int hashCode() {
			return neighborMineNumber;
		}

		@Override
		public boolean equals(Object obj) {
			return obj == this
				|| obj != null
				&& obj.getClass().equals(getClass())
				&& ((Open) obj).neighborMineNumber == neighborMineNumber;
		}

		@Override
		public boolean isUnknown() {
			return false;
		}

		@Override
		public boolean isMined() {
			return false;
		}

		@Override
		public boolean isFree() {
			return true;
		}

		@Override
		public boolean isOpen() {
			return true;
		}
	}
}
