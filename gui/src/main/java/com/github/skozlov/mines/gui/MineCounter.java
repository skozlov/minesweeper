package com.github.skozlov.mines.gui;

import com.github.skozlov.mines.model.Model;

import javax.swing.*;

import static javax.swing.SwingUtilities.invokeLater;

public class MineCounter extends JLabel {
	public MineCounter(Model model) {
		super(Integer.toString(model.getMineNumber()));
		setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLoweredBevelBorder(),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)
		));
		model.addListener(field ->
			invokeLater(() ->
				setText(Integer.toString(model.getMineNumber() - field.getMarkedAsMinedNumber()))
			)
		);
	}
}
