package com.github.skozlov.mines.cli;

import com.github.skozlov.mines.model.Model;

import java.io.Reader;
import java.io.Writer;

public final class CLI {
	public CLI(Model model, Reader input, Writer output, Writer errors) {
		new View(model, output);
		new Controller(model, input, errors);
	}
}
