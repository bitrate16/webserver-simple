package bitrate16.webserver.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	public static String DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";

	public static void log(String message) {
		String tagLine = "[" + getDate(DATE_FORMAT) + "] ";
		String[] lines = message.split("\n");
		for (int i = 0; i < lines.length; i++) {
			System.out.print(tagLine);
			System.out.println(lines[i]);
		}
	}

	public static void err(String message) {
		String tagLine = "[" + getDate(DATE_FORMAT) + "] ";
		String[] lines = message.split("\n");
		for (int i = 0; i < lines.length; i++) {
			System.err.print(tagLine);
			System.err.println(lines[i]);
		}
	}

	public static void log(String tag, String message) {
		String tagLine = "[" + getDate(DATE_FORMAT) + "][" + tag + "] ";
		String[] lines = message.split("\n");
		for (int i = 0; i < lines.length; i++) {
			System.out.print(tagLine);
			System.out.println(lines[i]);
		}
	}

	public static void err(String tag, String message) {
		String tagLine = "[" + getDate(DATE_FORMAT) + "][" + tag + "] ";
		String[] lines = message.split("\n");
		for (int i = 0; i < lines.length; i++) {
			System.err.print(tagLine);
			System.err.println(lines[i]);
		}
	}

	public static String getDate(String format) {
		return new SimpleDateFormat(format).format(new Date(System.currentTimeMillis())).toString();
	}

	public static String getDateFormatted() {
		return new SimpleDateFormat(DATE_FORMAT).format(new Date(System.currentTimeMillis())).toString();
	}
}
