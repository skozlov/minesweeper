package com.github.skozlov.mines.gui;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.Map;

public class FontUtils {
	public static Font strikeOn(Font font) {
		//noinspection unchecked
		Map<TextAttribute, Object> attributes = (Map<TextAttribute, Object>) font.getAttributes();
		attributes.put(TextAttribute.STRIKETHROUGH, true);
		return new Font(attributes);
	}

	public static Font strikeOff(Font font) {
		//noinspection unchecked
		Map<TextAttribute, Object> attributes = (Map<TextAttribute, Object>) font.getAttributes();
		attributes.put(TextAttribute.STRIKETHROUGH, false);
		return new Font(attributes);
	}
}
