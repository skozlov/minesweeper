package com.github.skozlov.mines.gui;

import com.github.skozlov.mines.core.FieldState;
import com.github.skozlov.mines.model.Model;

import javax.swing.*;

import static com.github.skozlov.mines.gui.SwingUtils.executeInBackground;
import static javax.swing.SwingUtilities.invokeLater;

public class MineCounter extends JLabel {
	public MineCounter(Model model) {
		super();
		setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLoweredBevelBorder(),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)
		));
		model.addListener(this::update);
		executeInBackground(() -> update(model.getFieldState()));
	}

	private void update(FieldState field){
		invokeLater(() -> setText(Integer.toString(field.getPlayerPov().getMineNumberLeft())));
	}
}
