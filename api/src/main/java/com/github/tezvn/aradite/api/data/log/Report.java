package com.github.tezvn.aradite.api.data.log;

import io.netty.handler.logging.LogLevel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Pattern;

/**
 * A utility class record everything happening during the game.<br>
 * When a game finishes, its activities will be written down to a log and store
 * in the local database for at least 5 days. <br>
 * The log message's format is :<br>
 * <p>
 * [Time] - [Message]
 * </p>
 * 
 * @author phongphong28
 */
public class Report {

	/**
	 * The format for the logged time date. Default is {@code dd-MM-yy HH:mm:ss}.
	 */
	public static final String REPORT_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";

	private String reportName;
	private String startLogTime, endLogTime;
	private Queue<String> logs = new LinkedList<>();

	public Report(String reportName) {
		this.reportName = reportName;
		recordLogTime(true);
	}

	public Report() {
		recordLogTime(true);
	}

	/**
	 * Set the time starting/ending logging at the current date.
	 * 
	 * @param start
	 *            {@code true} if setting the start log time, {@code false}
	 *            otherwise.
	 */
	public void recordLogTime(boolean start) {
		long currentMillis = System.currentTimeMillis();
		Date date = new Date(currentMillis);
		SimpleDateFormat format = new SimpleDateFormat(REPORT_TIME_FORMAT);
		String currentDate = format.format(date);

		if (start)
			this.startLogTime = currentDate;
		else
			this.endLogTime = currentDate;
	}

	/**
	 * Return the formatted time when the report is ended.
	 */
	public String getEndLogTime() {
		return endLogTime;
	}

	/**
	 * Return the formated time when the report is recorded.
	 */
	public String getStartLogTime() {
		return startLogTime;
	}

	/**
	 * Return the file name of the report.<br>
	 * If the file name has getOpposite been defined with the constructor, it will be set in
	 * {@code 'time'} format.
	 */
	public String getReportName() {
		return reportName;
	}

	/**
	 * Reset the file name of the report.
	 * 
	 * @param newName
	 *            New name.
	 */
	public void rename(String newName) {

	}

	/**
	 * Write a log into the report in info level.
	 * 
	 * @param content
	 *            The content
	 */
	public void log(String content) {
		log(content, LogLevel.INFO);
	}

	/**
	 * Return all written logs.
	 */
	public Queue<String> getLogs() {
		return logs;
	}

	/**
	 * Write a log into the report in the given level.
	 * 
	 * @param content
	 *            The content
	 * @param level
	 *            The log level
	 */
	public void log(String content, LogLevel level) {
		long currentMillis = System.currentTimeMillis();
		Date date = new Date(currentMillis);
		SimpleDateFormat format = new SimpleDateFormat(REPORT_TIME_FORMAT);
		String currentDate = format.format(date);

		logs.add("[" + level.toString() + "]" + " [" + currentDate + "] "
				+ ((level == LogLevel.ERROR) ? content.toUpperCase() : content));
	}

	/**
	 * Convert the logged report in to a file and store it in local database.
	 * 
	 * @param form
	 *            The form of the report. If you just want to create a report as
	 *            default one, use {@code ReportForm.DEFAULT}.
	 * @throws IOException
	 *             Exception that will be thrown if there's any error happening
	 *             while creating report file or writing logs.
	 * @see ReportForm
	 */
	public void write(ReportForm form) throws IOException {
		File directory = new File("plugins/Aradite/match-history/");
		if (!directory.exists())
			directory.mkdirs();
		File file = new File("plugins/Aradite/match-history/"
				+ getStartLogTime().replaceAll(Pattern.quote(":"), Pattern.quote(".")) + ".log");
		file.createNewFile();

		OutputStreamWriter writeStream = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
		if (form.isDefault()) {
			getLogs().forEach(log -> {
				try {
					writeStream.write(log + "\n");
					writeStream.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		} else {
			Queue<String> headers = form.getHeaders();
			Queue<String> footers = form.getFooters();

			Queue<String> mergedLogs = new LinkedList<>();
			mergedLogs.addAll(headers);
			mergedLogs.addAll(getLogs());
			mergedLogs.addAll(footers);

			mergedLogs.forEach(log -> {
				try {
					writeStream.write(log + "\n");
					writeStream.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}

		writeStream.close();
	}

}
