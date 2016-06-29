package fi.patrikmarin.infoboard.utils;

import java.time.LocalDateTime;

/**
 * Program logger.
 * Available log level can be found in
 * LogLevel.java
 */
public class Logger {
	// Number of warnings and errors logged
	private Integer errorCount = 0;
	private Integer warningCount = 0;
	
	/**
	 * Log the message.
	 * @param level
	 * @param message
	 */
	public static void log(LogLevel level, String message) {
		String spacing = "";
		
		// Handle messages by level
		switch (level) {
			case SUCCESS: 	spacing = " ";
							break;
			default:		spacing = " ";
		}
		
		// Print the message to the console
		System.out.println(LocalDateTime.now().toString() + "\t[" + level.toString() + "]" + spacing + message);
	}
}
