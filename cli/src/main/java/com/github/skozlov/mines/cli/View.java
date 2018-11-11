package com.github.skozlov.mines.cli;

import com.github.skozlov.mines.core.CellState;
import com.github.skozlov.mines.core.FieldState;
import com.github.skozlov.mines.core.MatrixDimension;
import com.github.skozlov.mines.model.Model;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static java.lang.System.lineSeparator;

public final class View {
	private FieldState field;
	private final Executor executor = Executors.newSingleThreadExecutor();
	private final PrintWriter writer;

	public View(Model model, Writer output){
		writer = new PrintWriter(output, true);
		model.addListener(this::print);
		print(model.getFieldState());
	}

	private void print(FieldState field){
		executor.execute(() -> {
			if (field == this.field){
				return;
			}
			this.field = field;
			printField();
			if (field.isWon()){
				printWon();
			}
		});
	}

	private void printField(){
		writer.println(fieldToString() + lineSeparator());
	}

	private String fieldToString() {
		MatrixDimension dimension = field.getDimension();
		StringBuilder buffer = new StringBuilder(
			dimension.getCellNumber() + lineSeparator().length() * dimension.getRowNumber()
		);
		dimension.forEachCoordinate(coordinate -> {
			buffer.append(cellToChar(field.getCell(coordinate)));
			if (coordinate.getColumnIndex() == dimension.getMaxColumnIndex()){
				buffer.append(lineSeparator());
			}
		});
		return buffer.toString();
	}

	private char cellToChar(CellState cell) {
		return cell.fold(
			intact -> '_',
			markedAsMined -> '#',
			wronglyMarkedAsMined -> '$',
			open -> cell.getCell().fold(
				mined -> '*',
				free -> Integer.toString(free.getNeighborMineNumber()).charAt(0)
			),
			exploded -> '@'
		);
	}

	private void printWon(){
		writer.println("You won");
	}
}
