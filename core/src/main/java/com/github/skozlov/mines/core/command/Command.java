package com.github.skozlov.mines.core.command;

import com.github.skozlov.mines.core.MatrixCoordinate;

import java.util.Objects;

public final class Command {
	private final CommandType type;
	private final MatrixCoordinate coordinate;

	public Command(CommandType type, MatrixCoordinate coordinate) {
		this.type = type;
		this.coordinate = coordinate;
	}

	public CommandType getType() {
		return type;
	}

	public MatrixCoordinate getCoordinate() {
		return coordinate;
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, coordinate);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this){
			return true;
		}
		if (obj == null || !obj.getClass().equals(getClass())){
			return false;
		}
		Command that = (Command) obj;
		return this.type.equals(that.type) && this.coordinate.equals(that.coordinate);
	}
}
