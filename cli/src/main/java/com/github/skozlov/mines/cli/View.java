package com.github.skozlov.mines.cli;

import com.github.skozlov.mines.commons.matrix.Matrix;
import com.github.skozlov.mines.commons.matrix.MatrixDimension;
import com.github.skozlov.mines.core.playerPov.CellPlayerPov;
import com.github.skozlov.mines.core.FieldState;
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
	public static final String MINES_LEFT_SUFFIX = " mines left";

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
			if (field.getPlayerPov().isWon()){
				printWon();
			}
		});
	}

	private void printField(){
		writer.println(fieldToString());
	}

	private String fieldToString() {
		Matrix<CellPlayerPov> cells = field.getPlayerPov().getCells();
		MatrixDimension dimension = cells.getDimension();
		String minesLeft =
			Integer.toString(field.getPlayerPov().getMineNumberLeft()) + MINES_LEFT_SUFFIX + lineSeparator();
		StringBuilder buffer = new StringBuilder(
			 minesLeft.length() + dimension.getCellNumber() + lineSeparator().length() * dimension.getRowNumber()
		).append(minesLeft);
		dimension.forEachCoordinate(coordinate -> {
			buffer.append(cellToChar(cells.get(coordinate)));
			if (coordinate.getColumnIndex() == dimension.getMaxColumnIndex()){
				buffer.append(lineSeparator());
			}
		});
		return buffer.toString();
	}

	private char cellToChar(CellPlayerPov cell) {
		return cell.fold(
			intact -> INTACT_CELL,
			open -> Integer.toString(open.getNeighborMineNumber()).charAt(0),
			markedAsMined -> MARKED_CELL,
			exploded -> EXPLODED_CELL,
			mined -> MINED_CELL,
			wronglyMarkedAsMined -> WRONGLY_MARKED_CELL
		);
	}

	private void printWon(){
		writer.println("You won");
	}
}
