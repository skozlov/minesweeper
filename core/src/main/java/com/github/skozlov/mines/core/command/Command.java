package com.github.skozlov.mines.core.command;

import com.github.skozlov.mines.core.MatrixCoordinate;

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
}
