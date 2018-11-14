package com.github.skozlov.mines.core.command;

import java.util.function.Supplier;

public enum CommandType {
	OPEN, OPEN_INTACT_NEIGHBORS, MARK_AS_MINED, UNMARK_AS_MINED;

	public <T> T fold(
		Supplier<T> open,
		Supplier<T> openIntactNeighbors,
		Supplier<T> markAsMined,
		Supplier<T> unmarkAsMined
	){
		switch (this){
			case OPEN: return open.get();
			case OPEN_INTACT_NEIGHBORS: return openIntactNeighbors.get();
			case MARK_AS_MINED: return markAsMined.get();
			case UNMARK_AS_MINED: return unmarkAsMined.get();
			default: throw new RuntimeException(String.format("Unsupported value %s", this));
		}
	}
}
