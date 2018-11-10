package com.github.skozlov.mines.core;

import java.util.function.Consumer;

public abstract class Cell {
	private Cell(){}

	public boolean isMined() {
		return false;
	}

	public abstract void fold(Consumer<Mined> mined, Consumer<Free> free);

	public static final class Mined extends Cell {
		public static final Mined INSTANCE = new Mined();

		private Mined(){
			super();
		}

		@Override
		public boolean isMined() {
			return true;
		}

		@Override
		public void fold(Consumer<Mined> mined, Consumer<Free> free) {
			mined.accept(this);
		}
	}

	public static final class Free extends Cell {
		private final int neighborMineNumber;

		public Free(int neighborMineNumber) {
			super();
			if (neighborMineNumber < 0 || neighborMineNumber > 8){
				throw new IllegalArgumentException(String.format(
					"Incorrect neighbor mine number %d",
					neighborMineNumber
				));
			}
			this.neighborMineNumber = neighborMineNumber;
		}

		public int getNeighborMineNumber() {
			return neighborMineNumber;
		}

		@Override
		public void fold(Consumer<Mined> mined, Consumer<Free> free) {
			free.accept(this);
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
					&& ((Free)obj).neighborMineNumber == neighborMineNumber;
		}
	}
}
