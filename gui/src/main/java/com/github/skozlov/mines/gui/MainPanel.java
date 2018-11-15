package com.github.skozlov.mines.gui;

import com.github.skozlov.mines.core.FieldState;
import com.github.skozlov.mines.model.Model;

import javax.swing.*;
import java.awt.*;

import static com.github.skozlov.mines.gui.SwingUtils.executeInBackground;
import static javax.swing.SwingUtilities.invokeLater;

public class MainPanel extends JPanel {
	public MainPanel(Model model) {
		super(new BorderLayout());

		JPanel mineCounterPanel = new JPanel();
		mineCounterPanel.add(new MineCounter(model));

		add(mineCounterPanel, BorderLayout.NORTH);
		add(new FieldPanel(model), BorderLayout.CENTER);

		model.addListener(this::onState);
		executeInBackground(() -> onState(model.getFieldState()));
	}

	private void onState(FieldState field){
		invokeLater(() -> {
			if (field.getPlayerPov().isWon()){
				JOptionPane.showMessageDialog(MainPanel.this, "You won");
			}
		});
	}
}
