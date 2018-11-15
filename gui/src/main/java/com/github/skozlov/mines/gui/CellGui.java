package com.github.skozlov.mines.gui;

import com.github.skozlov.mines.commons.matrix.MatrixCoordinate;
import com.github.skozlov.mines.core.command.Command;
import com.github.skozlov.mines.core.command.CommandType;
import com.github.skozlov.mines.core.playerPov.CellPlayerPov;
import com.github.skozlov.mines.model.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.github.skozlov.mines.gui.SwingUtils.executeInBackground;
import static java.awt.Color.*;
import static javax.swing.SwingUtilities.*;

public class CellGui extends JButton {
	private CellPlayerPov cell;

	public CellGui(CellPlayerPov cell, MatrixCoordinate coordinate, Model model) {
		this.cell = cell;
		addMouseListener(new MouseAdapter() {
			private boolean leftReleaseAwaiting = false;
			private boolean rightReleaseAwaiting = false;
			private boolean wheelReleaseAwaiting = false;

			@Override
			public void mouseEntered(MouseEvent e) {
				leftReleaseAwaiting = false;
				rightReleaseAwaiting = false;
				wheelReleaseAwaiting = false;
			}

			@Override
			public void mouseExited(MouseEvent e) {
				leftReleaseAwaiting = false;
				rightReleaseAwaiting = false;
				wheelReleaseAwaiting = false;
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (isLeftMouseButton(e)){
					leftReleaseAwaiting = true;
					rightReleaseAwaiting = false;
					wheelReleaseAwaiting = false;
				} else if (isRightMouseButton(e)){
					leftReleaseAwaiting = false;
					rightReleaseAwaiting = true;
					wheelReleaseAwaiting = false;
				} else if (isMiddleMouseButton(e)){
					leftReleaseAwaiting = false;
					rightReleaseAwaiting = false;
					wheelReleaseAwaiting = true;
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (isLeftMouseButton(e)){
					rightReleaseAwaiting = false;
					wheelReleaseAwaiting = false;
					if (leftReleaseAwaiting){
						leftReleaseAwaiting = false;
						executeInBackground(() -> model.execute(new Command(CommandType.OPEN, coordinate)));
					}
				} else if (isRightMouseButton(e)){
					leftReleaseAwaiting = false;
					wheelReleaseAwaiting = false;
					if (rightReleaseAwaiting){
						rightReleaseAwaiting = false;
						if (CellGui.this.cell.isMarkedAsMined()){
							executeInBackground(() ->
								model.execute(new Command(CommandType.UNMARK_AS_MINED, coordinate))
							);
						} else {
							executeInBackground(() ->
								model.execute(new Command(CommandType.MARK_AS_MINED, coordinate))
							);
						}
					}
				} else if (isMiddleMouseButton(e)){
					leftReleaseAwaiting = false;
					rightReleaseAwaiting = false;
					if (wheelReleaseAwaiting){
						wheelReleaseAwaiting = false;
						executeInBackground(() ->
							model.execute(new Command(CommandType.OPEN_INTACT_NEIGHBORS, coordinate))
						);
					}
				}
			}
		});
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int px = getSize().height / 3;
				float pt = px * 0.75f;
				setFont(getFont().deriveFont(pt));
			}
		});
		update(cell);
	}

	public void update(CellPlayerPov cell) {
		this.cell = cell;
		cell.fold(
			intact -> {
				setText("");
				setBackground(null);
			},
			open -> {
				int neighborMineNumber = open.getNeighborMineNumber();
				setText(neighborMineNumber == 0 ? "" : Integer.toString(neighborMineNumber));
				Color color = null;
				if (neighborMineNumber == 1){
					color = BLUE;
				} else if (neighborMineNumber == 2){
					color = GREEN;
				} else if (neighborMineNumber == 3){
					color = RED;
				} else if (neighborMineNumber == 4){
					color = new Color(128, 0, 128);
				} else if (neighborMineNumber == 5){
					color = new Color(128, 0, 0);
				} else if (neighborMineNumber == 6){
					color = new Color(64, 224, 208);
				} else if (neighborMineNumber == 7){
					color = BLACK;
				} else if (neighborMineNumber == 8){
					color = GRAY;
				}
				if (color != null) {
					setForeground(color);
				}
				setFont(FontUtils.strikeOff(getFont()));
				setBackground(WHITE);
			},
			markedAsMined -> {
				setText("⚑");
				setFont(FontUtils.strikeOff(getFont()));
				setForeground(RED);
				setBackground(null);
			},
			exploded -> {
				setText("*");
				setFont(FontUtils.strikeOff(getFont()));
				setForeground(BLACK);
				setBackground(RED);
			},
			mined -> {
				setText("*");
				setForeground(BLACK);
				setFont(FontUtils.strikeOff(getFont()));
				setBackground(WHITE);
			},
			wronglyMarkedAsMined -> {
				setText("⚑");
				setFont(FontUtils.strikeOn(getFont()));
				setForeground(RED);
				setBackground(null);
			}
		);
	}
}
