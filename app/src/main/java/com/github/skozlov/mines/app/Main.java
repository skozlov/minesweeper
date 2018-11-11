package com.github.skozlov.mines.app;

import com.github.skozlov.mines.cli.CLI;
import com.github.skozlov.mines.core.Field;
import com.github.skozlov.mines.core.RandomFieldGenerator;
import com.github.skozlov.mines.gui.Window;
import com.github.skozlov.mines.model.Model;
import org.apache.commons.cli.ParseException;

import javax.swing.*;
import java.awt.*;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import static javax.swing.SwingUtilities.invokeLater;

public class Main {
	private Main(){}

	public static void main(String[] args) throws ParseException {
		Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
			e.printStackTrace();
			System.exit(-1);
		});
		Args arguments = new Args(args);
		if (arguments.isHelpRequested()){
			arguments.printHelp();
			return;
		}
		Field field = new RandomFieldGenerator().generate(arguments.getFieldParameters());
		Model model = new Model(field);
		new CLI(
			model,
			new InputStreamReader(System.in),
			new OutputStreamWriter(System.out),
			new OutputStreamWriter(System.err)
		);
		invokeLater(() -> {
			JFrame window = new Window(model);
			window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			window.setLocationRelativeTo(null);
			window.setExtendedState(window.getExtendedState() | Frame.MAXIMIZED_BOTH);
			window.setVisible(true);
		});
	}
}
