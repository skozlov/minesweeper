package com.github.skozlov.mines.gui;

import com.github.skozlov.mines.commons.matrix.Matrix;
import com.github.skozlov.mines.commons.matrix.MatrixCoordinate;
import com.github.skozlov.mines.commons.matrix.MatrixDimension;
import com.github.skozlov.mines.core.playerPov.CellPlayerPov;
import com.github.skozlov.mines.core.FieldState;
import com.github.skozlov.mines.model.Model;

import javax.swing.*;
import java.awt.*;

import static com.github.skozlov.mines.gui.SwingUtils.executeInBackground;
import static javax.swing.SwingUtilities.invokeLater;

public class FieldPanel extends JPanel {
	private CellGui[][] cells;
	private final JPanel gridPanel;
	private final Model model;

	public FieldPanel(Model model) {
		super(new GridBagLayout());

		this.model = model;
		MatrixDimension dimension = model.getDimension();
		int rowNumber = dimension.getRowNumber();
		int columnNumber = dimension.getColumnNumber();

		gridPanel = new JPanel(new GridLayout(rowNumber, columnNumber)){
			@Override
			public Dimension getPreferredSize() {
				Dimension parentSize = getParent().getSize();
				Dimension outerSize = (parentSize.width == 0 || parentSize.height == 0)
					? super.getPreferredSize()
					: parentSize;
				int cellSidePix = Math.min(
					outerSize.width / columnNumber,
					outerSize.height / rowNumber
				);
				return new Dimension(cellSidePix * columnNumber, cellSidePix * rowNumber);
			}
		};

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		add(gridPanel, gridBagConstraints);

		model.addListener(this::update);
		executeInBackground(() -> update(model.getFieldState()));
	}

	private void update(FieldState field){
		invokeLater(() -> {
			Matrix<CellPlayerPov> cells = field.getPlayerPov().getCells();
			MatrixDimension dimension = cells.getDimension();
			if (this.cells == null){
				int rowNumber = dimension.getRowNumber();
				int columnNumber = dimension.getColumnNumber();
				this.cells = new CellGui[rowNumber][];
				for (int rowIndex = 0; rowIndex < rowNumber; rowIndex++){
					CellGui[] row = new CellGui[columnNumber];
					this.cells[rowIndex] = row;
					for (int columnIndex = 0; columnIndex < columnNumber; columnIndex++){
						MatrixCoordinate coordinate = new MatrixCoordinate(rowIndex, columnIndex);
						CellGui cell = new CellGui(cells.get(coordinate), coordinate, model);
						row[columnIndex] = cell;
						gridPanel.add(cell);
					}
				}
			} else {
				dimension.forEachCoordinate(coordinate ->
					this.cells[coordinate.getRowIndex()][coordinate.getColumnIndex()].update(cells.get(coordinate))
				);
			}
		});
	}
}
