package com.github.skozlov.mines.core.playerPov;

import com.github.skozlov.mines.core.Cell;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class CellPlayerPov {
	private CellPlayerPov(){}

	public abstract boolean isOpen();

	public abstract boolean isMarkedAsMined();

	public abstract boolean isIntact();

	public abstract void fold(
		Consumer<Intact> intact,
		Consumer<Open> open,
		Consumer<MarkedAsMined> markedAsMined,
		Consumer<Exploded> exploded,
		Consumer<Mined> mined,
		Consumer<WronglyMarkedAsMined> wronglyMarkedAsMined
	);

	public abstract <T> T fold(
		Function<Intact, T> intact,
		Function<Open, T> open,
		Function<MarkedAsMined, T> markedAsMined,
		Function<Exploded, T> exploded,
		Function<Mined, T> mined,
		Function<WronglyMarkedAsMined, T> wronglyMarkedAsMined
	);

	public static final class Intact extends CellPlayerPov{
		private Intact(){}

		@Override
		public boolean isOpen() {
			return false;
		}

		@Override
		public boolean isMarkedAsMined() {
			return false;
		}

		@Override
		public boolean isIntact() {
			return true;
		}

		@Override
		public void fold(
			Consumer<Intact> intact,
			Consumer<Open> open,
			Consumer<MarkedAsMined> markedAsMined,
			Consumer<Exploded> exploded,
			Consumer<Mined> mined,
			Consumer<WronglyMarkedAsMined> wronglyMarkedAsMined
		) {
			intact.accept(this);
		}

		@Override
		public <T> T fold(
			Function<Intact, T> intact,
			Function<Open, T> open,
			Function<MarkedAsMined, T> markedAsMined,
			Function<Exploded, T> exploded,
			Function<Mined, T> mined,
			Function<WronglyMarkedAsMined, T> wronglyMarkedAsMined
		) {
			return intact.apply(this);
		}

		public static final Intact INSTANCE = new Intact();
	}

	public static final class Open extends CellPlayerPov{
		private final int neighborMineNumber;

		public Open(Cell.Free cell){
			this.neighborMineNumber = cell.getNeighborMineNumber();
		}

		public int getNeighborMineNumber() {
			return neighborMineNumber;
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
					&& ((Open)obj).neighborMineNumber == neighborMineNumber;
		}

		@Override
		public boolean isOpen() {
			return true;
		}

		@Override
		public boolean isMarkedAsMined() {
			return false;
		}

		@Override
		public boolean isIntact() {
			return false;
		}

		@Override
		public void fold(
			Consumer<Intact> intact,
			Consumer<Open> open,
			Consumer<MarkedAsMined> markedAsMined,
			Consumer<Exploded> exploded,
			Consumer<Mined> mined,
			Consumer<WronglyMarkedAsMined> wronglyMarkedAsMined
		) {
			open.accept(this);
		}

		@Override
		public <T> T fold(
			Function<Intact, T> intact,
			Function<Open, T> open,
			Function<MarkedAsMined, T> markedAsMined,
			Function<Exploded, T> exploded,
			Function<Mined, T> mined,
			Function<WronglyMarkedAsMined, T> wronglyMarkedAsMined
		) {
			return open.apply(this);
		}
	}

	public static final class MarkedAsMined extends CellPlayerPov{
		private MarkedAsMined(){}

		@Override
		public boolean isOpen() {
			return false;
		}

		@Override
		public boolean isMarkedAsMined() {
			return true;
		}

		@Override
		public boolean isIntact() {
			return false;
		}

		@Override
		public void fold(
			Consumer<Intact> intact,
			Consumer<Open> open,
			Consumer<MarkedAsMined> markedAsMined,
			Consumer<Exploded> exploded,
			Consumer<Mined> mined,
			Consumer<WronglyMarkedAsMined> wronglyMarkedAsMined
		) {
			markedAsMined.accept(this);
		}

		@Override
		public <T> T fold(
			Function<Intact, T> intact,
			Function<Open, T> open,
			Function<MarkedAsMined, T> markedAsMined,
			Function<Exploded, T> exploded,
			Function<Mined, T> mined,
			Function<WronglyMarkedAsMined, T> wronglyMarkedAsMined
		) {
			return markedAsMined.apply(this);
		}

		public static final MarkedAsMined INSTANCE = new MarkedAsMined();
	}

	public static final class Exploded extends CellPlayerPov{
		private Exploded(){}

		@Override
		public boolean isOpen() {
			return true;
		}

		@Override
		public boolean isMarkedAsMined() {
			return false;
		}

		@Override
		public boolean isIntact() {
			return false;
		}

		@Override
		public void fold(
			Consumer<Intact> intact,
			Consumer<Open> open,
			Consumer<MarkedAsMined> markedAsMined,
			Consumer<Exploded> exploded,
			Consumer<Mined> mined,
			Consumer<WronglyMarkedAsMined> wronglyMarkedAsMined
		) {
			exploded.accept(this);
		}

		@Override
		public <T> T fold(
			Function<Intact, T> intact,
			Function<Open, T> open,
			Function<MarkedAsMined, T> markedAsMined,
			Function<Exploded, T> exploded,
			Function<Mined, T> mined,
			Function<WronglyMarkedAsMined, T> wronglyMarkedAsMined
		) {
			return exploded.apply(this);
		}

		public static final Exploded INSTANCE = new Exploded();
	}

	public static final class Mined extends CellPlayerPov{
		private Mined(){}

		@Override
		public boolean isOpen() {
			return false;
		}

		@Override
		public boolean isMarkedAsMined() {
			return false;
		}

		@Override
		public boolean isIntact() {
			return false;
		}

		@Override
		public void fold(
			Consumer<Intact> intact,
			Consumer<Open> open,
			Consumer<MarkedAsMined> markedAsMined,
			Consumer<Exploded> exploded,
			Consumer<Mined> mined,
			Consumer<WronglyMarkedAsMined> wronglyMarkedAsMined
		) {
			mined.accept(this);
		}

		@Override
		public <T> T fold(
			Function<Intact, T> intact,
			Function<Open, T> open,
			Function<MarkedAsMined, T> markedAsMined,
			Function<Exploded, T> exploded,
			Function<Mined, T> mined,
			Function<WronglyMarkedAsMined, T> wronglyMarkedAsMined
		) {
			return mined.apply(this);
		}

		public static final Mined INSTANCE = new Mined();
	}

	public static final class WronglyMarkedAsMined extends CellPlayerPov{
		private WronglyMarkedAsMined(){}

		@Override
		public boolean isOpen() {
			return false;
		}

		@Override
		public boolean isMarkedAsMined() {
			return true;
		}

		@Override
		public boolean isIntact() {
			return false;
		}

		@Override
		public void fold(
			Consumer<Intact> intact,
			Consumer<Open> open,
			Consumer<MarkedAsMined> markedAsMined,
			Consumer<Exploded> exploded,
			Consumer<Mined> mined,
			Consumer<WronglyMarkedAsMined> wronglyMarkedAsMined
		) {
			wronglyMarkedAsMined.accept(this);
		}

		@Override
		public <T> T fold(
			Function<Intact, T> intact,
			Function<Open, T> open,
			Function<MarkedAsMined, T> markedAsMined,
			Function<Exploded, T> exploded,
			Function<Mined, T> mined,
			Function<WronglyMarkedAsMined, T> wronglyMarkedAsMined
		) {
			return wronglyMarkedAsMined.apply(this);
		}

		public static final WronglyMarkedAsMined INSTANCE = new WronglyMarkedAsMined();
	}
}
