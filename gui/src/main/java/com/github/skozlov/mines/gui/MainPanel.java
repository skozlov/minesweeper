package com.github.skozlov.mines.gui;

import com.github.skozlov.mines.model.Model;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {
	public MainPanel(Model model) {
		super(new BorderLayout());

		JPanel mineCounterPanel = new JPanel();
		mineCounterPanel.add(new MineCounter(model));

		add(mineCounterPanel, BorderLayout.NORTH);
		add(new FieldPanel(model), BorderLayout.CENTER);
	}
}
