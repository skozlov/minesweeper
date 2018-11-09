package com.github.skozlov.mines.gui;

import com.github.skozlov.mines.model.Model;

import javax.swing.*;

public class Window extends JFrame {
	public Window(Model model){
		super("Minesweeper");
		setContentPane(new MainPanel(model));
		pack();
	}
}
