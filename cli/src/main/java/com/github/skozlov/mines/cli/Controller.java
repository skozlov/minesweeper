package com.github.skozlov.mines.cli;

import com.github.skozlov.mines.core.MatrixCoordinate;
import com.github.skozlov.mines.core.command.Command;
import com.github.skozlov.mines.core.command.CommandType;
import com.github.skozlov.mines.model.Model;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Controller {
	private static final Map<String, CommandType> COMMAND_TYPES;
	private static final String COMMAND_TYPE_GROUP = "commandType";
	private static final String ROW_GROUP = "row";
	private static final String COLUMN_GROUP = "column";
	private static final Pattern COMMAND_PATTERN;

	public static final String OPEN_COMMAND = "open";
	public static final String OPEN_INTACT_NEIGHBORS_COMMAND = "around";
	public static final String MARK_AS_MINED_COMMAND = "mark";
	public static final String UNMARK_AS_MINED_COMMAND = "unmark";

	static {
		COMMAND_TYPES = new LinkedHashMap<>(4, 1);
		COMMAND_TYPES.put(OPEN_COMMAND, CommandType.OPEN);
		COMMAND_TYPES.put(OPEN_INTACT_NEIGHBORS_COMMAND, CommandType.OPEN_INTACT_NEIGHBORS);
		COMMAND_TYPES.put(MARK_AS_MINED_COMMAND, CommandType.MARK_AS_MINED);
		COMMAND_TYPES.put(UNMARK_AS_MINED_COMMAND, CommandType.UNMARK_AS_MINED);
		String commandType = String.format(
			"(?<%s>(%s))",
			COMMAND_TYPE_GROUP,
			COMMAND_TYPES.keySet().stream()
				.map(Pattern::quote)
				.collect(Collectors.joining("|"))
		);
		String positiveInt = "[1-9](\\d*){9}";
		String row = String.format("(?<%s>%s)", ROW_GROUP, positiveInt);
		String column = String.format("(?<%s>%s)", COLUMN_GROUP, positiveInt);
		COMMAND_PATTERN = Pattern.compile(String.format("%s\\h+%s\\h+%s", commandType, row, column));
	}

	public Controller(Model model, Reader input, Writer errors) {
		new Thread(() -> {
			try (BufferedReader reader = new BufferedReader(input)){
				try (PrintWriter errorWriter = new PrintWriter(errors)){
					for (;;){
						String line = reader.readLine();
						if (line == null){
							break;
						}
						try {
							model.execute(parseCommand(line));
						} catch (ParsingException e){
							errorWriter.println(e.getMessage());
						}
					}
				}
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}).start();
	}

	private Command parseCommand(String line) throws ParsingException {
		Matcher matcher = COMMAND_PATTERN.matcher(line);
		if (!matcher.matches()){
			throw new ParsingException(String.format("Command `%s` does not match pattern %s", line, COMMAND_PATTERN));
		}
		CommandType commandType = COMMAND_TYPES.get(matcher.group(COMMAND_TYPE_GROUP));
		int rowIndex = parseIndex(matcher.group(ROW_GROUP));
		int columnIndex = parseIndex(matcher.group(COLUMN_GROUP));
		MatrixCoordinate coordinate = new MatrixCoordinate(rowIndex, columnIndex);
		return new Command(commandType, coordinate);
	}

	private static int parseIndex(String source) throws ParsingException {
		try {
			return Integer.parseInt(source) - 1;
		} catch (NumberFormatException e){
			throw new ParsingException(String.format(
				"Index %s is too big, can't be greater than %d",
				source, Integer.MAX_VALUE
			));
		}
	}
}
