package com.github.skozlov.mines.model;

import com.github.skozlov.mines.core.Field;
import com.github.skozlov.mines.core.FieldState;
import com.github.skozlov.mines.commons.matrix.MatrixCoordinate;
import com.github.skozlov.mines.commons.matrix.MatrixDimension;
import com.github.skozlov.mines.core.command.Command;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

public final class Model {
	private final MatrixDimension dimension;

	private FieldState field;
	private final Object monitor = new Object();

	private final Collection<ModelListener> listeners = new ConcurrentLinkedQueue<>();

	public Model(Field field){
		this.field = FieldState.allIntact(field);
		dimension = field.getCells().getDimension();
	}

	public MatrixDimension getDimension(){
		return dimension;
	}

	public FieldState getFieldState() {
		return field;
	}

	public void execute(Command command){
		modify(field -> field.execute(command));
	}

	public void open(MatrixCoordinate coordinate) {
		modify(field -> field.open(coordinate));
	}

	public void openIntactNeighbors(MatrixCoordinate coordinate) {
		modify(field -> field.openIntactNeighbors(coordinate));
	}

	public void markAsMined(MatrixCoordinate coordinate) {
		modify(field -> field.markAsMined(coordinate));
	}

	public void unmarkAsMined(MatrixCoordinate coordinate) {
		modify(field -> field.unmarkAsMined(coordinate));
	}

	private void modify(Function<FieldState, FieldState> modification){
		synchronized (monitor){
			if (field.isGameOver()){
				return;
			}
			FieldState newField = modification.apply(field);
			if (newField == field){
				return;
			}
			field = newField;
		}
		for (ModelListener listener : listeners){
			listener.onChange(field);
		}
	}

	public void addListener(ModelListener listener){
		listeners.add(listener);
	}
}
