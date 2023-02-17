package com.github.tezvn.aradite.impl.data.log;

import java.util.LinkedList;
import java.util.Queue;

/**
 * A class decides the form for the report, which includes header, footer, ....
 * 
 * @author phongphong28
 */
public class ReportForm {

	/**
	 * The default form of a report.
	 */
	public static final ReportForm DEFAULT = new ReportForm();

	private final Queue<String> headers = new LinkedList<>();
	private final Queue<String> footers = new LinkedList<>();

	/**
	 * Add a new header line
	 * @param headerLine New line
	 */
	public void addHeader(String headerLine) {
		this.headers.add(headerLine);
	}
	
	/**
	 * Add a new footer line
	 * @param footerLine New line
	 */
	public void addFooter(String footerLine) {
		this.headers.add(footerLine);
	}
	
	/**
	 * Return all header lines.
	 */
	public Queue<String> getHeaders() {
		return headers;
	}

	/**
	 * Return all footer lines.
	 */
	public Queue<String> getFooters() {
		return footers;
	}

	/**
	 * Check if the form is a default form (no headers and footers).
	 * 
	 * @return {@code true} if default, {@code false} otherwise.
	 */
	public boolean isDefault() {
		return getHeaders().isEmpty() && getFooters().isEmpty();
	}

}
