package com.github.skozlov.mines.model;

import com.github.skozlov.mines.commons.matrix.MatrixDimension;
import com.github.skozlov.mines.core.Field;
import com.github.skozlov.mines.core.command.Command;
import com.github.skozlov.mines.core.FieldState;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class Model {
	private final MatrixDimension dimension;

	private volatile FieldState field;
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
		synchronized (monitor){
			if (field.getPlayerPov().isGameOver()){
				return;
			}
			FieldState newField = field.execute(command);
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
