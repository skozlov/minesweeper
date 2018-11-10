package com.github.skozlov.mines.gui;

import javax.swing.*;

public class SwingUtils {
	public static void executeInBackground(Runnable task){
		new SwingWorker<Object, Object>() {
			@Override
			protected Object doInBackground() {
				task.run();
				return null;
			}
		}.execute();
	}
}
