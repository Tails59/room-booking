package ui;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JOptionPane;

public class Output {
	private static DateTimeFormatter dtf;
	
	//// Output to console////
	private static String nowf() {
		if(dtf == null) {
			dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
		}
	
		return LocalTime.now().format(dtf);
	}

	public static void console(String message, Object... args) {
		System.out.printf("[" + nowf() + "] " + message + "\n", args);
	}

	public static void consoleWarn(String message, Object... args) {
		console("[WARN] " + message, args);
	}

	public static void consoleError(String message, Object... args) {
		console("[ERROR] " + message, args);
	}

	//// Output to the user////
	public static void userError(String message) {
		JOptionPane.showMessageDialog(null, message, "Warning",
				JOptionPane.WARNING_MESSAGE);
	}
}
