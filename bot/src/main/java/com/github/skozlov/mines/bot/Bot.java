package com.github.skozlov.mines.bot;

import com.github.skozlov.mines.commons.matrix.MatrixCoordinate;
import com.github.skozlov.mines.core.FieldState;
import com.github.skozlov.mines.core.command.Command;
import com.github.skozlov.mines.core.command.CommandType;
import com.github.skozlov.mines.core.playerPov.FieldPlayerPov;
import com.github.skozlov.mines.model.Model;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class Bot {
	private final Model model;
	private final Executor executor = Executors.newSingleThreadExecutor();
	private FieldConfidenceState confidenceState;

	public Bot(Model model) {
		this.model = model;
		model.addListener(this::onState);
		onState(model.getFieldState());
	}

	private void onState(FieldState field) {
		onState(field.getPlayerPov());
	}

	private void onState(FieldPlayerPov field) {
		executor.execute(() -> {
			if (field.isGameOver()) {
				return;
			}
			confidenceState = confidenceState == null ? new FieldConfidenceState(field) : confidenceState.update(field);
			for (MatrixCoordinate coordinate : field.getCells().getDimension().coordinatesToList()){
				if (
					confidenceState.getCells().get(coordinate).isMined()
						&& !field.getCells().get(coordinate).isMarkedAsMined()
				) {
					model.execute(new Command(CommandType.MARK_AS_MINED, coordinate));
					break;
				} else if (
					confidenceState.getCells().get(coordinate).isFree()
						&& !field.getCells().get(coordinate).isOpen()
				) {
					model.execute(new Command(CommandType.OPEN, coordinate));
					break;
				}
			}
		});
	}
}
