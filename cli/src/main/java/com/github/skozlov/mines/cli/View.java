package com.github.skozlov.mines.cli;

import com.github.skozlov.mines.core.CellState;
import com.github.skozlov.mines.core.FieldState;
import com.github.skozlov.mines.commons.matrix.MatrixDimension;
import com.github.skozlov.mines.model.Model;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static java.lang.System.lineSeparator;

public final class View {
	public static final char INTACT_CELL = '_';
	public static final char MARKED_CELL = '#';
	public static final char WRONGLY_MARKED_CELL = '$';
	public static final char MINED_CELL = '*';
	public static final char EXPLODED_CELL = '@';

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
		writer.println(fieldToString());
	}

	private String fieldToString() {
		MatrixDimension dimension = field.getCells().getDimension();
		StringBuilder buffer = new StringBuilder(
			dimension.getCellNumber() + lineSeparator().length() * dimension.getRowNumber()
		);
		dimension.forEachCoordinate(coordinate -> {
			buffer.append(cellToChar(field.getCells().get(coordinate)));
			if (coordinate.getColumnIndex() == dimension.getMaxColumnIndex()){
				buffer.append(lineSeparator());
			}
		});
		return buffer.toString();
	}

	private char cellToChar(CellState cell) {
		return cell.fold(
			intact -> INTACT_CELL,
			markedAsMined -> MARKED_CELL,
			wronglyMarkedAsMined -> WRONGLY_MARKED_CELL,
			open -> cell.getCell().fold(
				mined -> MINED_CELL,
				free -> Integer.toString(free.getNeighborMineNumber()).charAt(0)
			),
			exploded -> EXPLODED_CELL
		);
	}

	private void printWon(){
		writer.println("You won");
	}
}
