package com.github.skozlov.mines.app;

import com.github.skozlov.mines.core.FieldParameters;
import com.github.skozlov.mines.core.MatrixDimension;
import org.apache.commons.cli.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.Math.min;
import static java.lang.System.lineSeparator;

public class Args {
	private static final String HELP_KEY = "h";

	private static final String ROWS_NUMBER_KEY = "r";
	private static final String COLUMNS_NUMBER_KEY = "c";
	private static final String MINES_NUMBER_KEY = "m";

	private static final String PRESET_KEY = "p";
	private static final String BEGINNER_PRESET_NAME = "beginner";
	private static final String DEFAULT_PRESET_NAME = BEGINNER_PRESET_NAME;
	private static final Map<String, FieldParameters> PRESETS = new LinkedHashMap<String, FieldParameters>(){{
		put(BEGINNER_PRESET_NAME, new FieldParameters(new MatrixDimension(9, 9), 10));
		put("intermediate", new FieldParameters(new MatrixDimension(16, 16), 40));
		put("expert", new FieldParameters(new MatrixDimension(16, 30), 99));
	}};

	private static final CommandLineParser PARSER = new DefaultParser();

	private static final Options OPTIONS = new Options()
		.addOption(
			Option.builder(HELP_KEY)
				.longOpt("help")
				.desc("Print help.")
				.build()
		)
		.addOption(
			Option.builder(ROWS_NUMBER_KEY)
				.longOpt("rows")
				.hasArg()
				.argName("rows")
				.desc(String.join(
					lineSeparator(),
					"Number of rows.",
					"Type: integer.",
					"Min: 1.",
					String.format("Max: %d.", Integer.MAX_VALUE),
					String.format("Default: specified by preset (see -%s).", PRESET_KEY)
				))
				.build()
		)
		.addOption(
			Option.builder(COLUMNS_NUMBER_KEY)
				.longOpt("columns")
				.hasArg()
				.argName("columns")
				.desc(String.join(
					lineSeparator(),
					"Number of columns.",
					"Type: integer.",
					"Min: 1.",
					String.format("Max: %d.", Integer.MAX_VALUE),
					String.format("Default: specified by preset (see -%s).", PRESET_KEY)
				))
				.build()
		)
		.addOption(
			Option.builder(MINES_NUMBER_KEY)
				.longOpt("mines")
				.hasArg()
				.argName("mines")
				.desc(String.join(
					lineSeparator(),
					"Number of mines.",
					"Type: integer.",
					"Min: 1.",
					"Max: rows * columns.",
					String.format(
						"Default: specified by preset (see -%s) if possible, (rows * columns) otherwise.",
						PRESET_KEY
					)
				))
				.build()
		)
		.addOption(
			Option.builder(PRESET_KEY)
				.longOpt("preset")
				.hasArg()
				.argName("preset")
				.desc(String.join(
					lineSeparator(),
					"Named preset for field parameters.",
					String.format(
						"The parameters may be overridden by options -%s, -%s and/or -%s.",
						ROWS_NUMBER_KEY, COLUMNS_NUMBER_KEY, MINES_NUMBER_KEY
					),
					"Supported presets:",
					String.join(
						lineSeparator(),
						PRESETS.entrySet().stream()
							.map(entry -> {
								String name = entry.getKey();
								FieldParameters parameters = entry.getValue();
								return String.format(
									"%s: %dx%d cells with %d mines.",
									name,
									parameters.getDimension().getRowNumber(),
									parameters.getDimension().getColumnNumber(),
									parameters.getMineNumber()
								);
							})
							.collect(Collectors.toList())
					),
					String.format("Default: %s.", DEFAULT_PRESET_NAME)
				))
				.build()
		);

	private static final HelpFormatter HELP_FORMATTER = new HelpFormatter();

	private final boolean helpRequested;
	private final FieldParameters fieldParameters;

	public Args(String[] args) throws ParseException {
		CommandLine options = PARSER.parse(OPTIONS, args);
		helpRequested = options.hasOption(HELP_KEY);
		String presetName = options.getOptionValue(PRESET_KEY, DEFAULT_PRESET_NAME);
		FieldParameters preset = PRESETS.get(presetName);
		if (preset == null){
			throw new IllegalStateException(String.format("Unknown preset: %s", presetName));
		}
		int rowNumber = Optional.ofNullable(options.getOptionValue(ROWS_NUMBER_KEY))
			.map(Integer::parseInt)
			.orElseGet(() -> preset.getDimension().getRowNumber());
		int columnNumber = Optional.ofNullable(options.getOptionValue(COLUMNS_NUMBER_KEY))
			.map(Integer::parseInt)
			.orElseGet(() -> preset.getDimension().getColumnNumber());
		int mineNumber = Optional.ofNullable(options.getOptionValue(MINES_NUMBER_KEY))
			.map(Integer::parseInt)
			.orElseGet(() -> min(preset.getMineNumber(), rowNumber * columnNumber));
		fieldParameters = new FieldParameters(new MatrixDimension(rowNumber, columnNumber), mineNumber);
	}

	public boolean isHelpRequested() {
		return helpRequested;
	}

	public void printHelp(){
		HELP_FORMATTER.printHelp(
			String.format("java -jar mines.jar [-options]%swhere options include:", lineSeparator()),
			OPTIONS
		);
	}

	public FieldParameters getFieldParameters() {
		return fieldParameters;
	}
}
