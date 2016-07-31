package net.twisterrob.blt.gapp.viewmodel;

import java.util.Date;

import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeed;

public class Result {
	private final String errorHeader;
	private final String fullError;
	private final LineStatusFeed content;
	private final Date when;
	public Result(Date when, String error) {
		this.when = when;
		this.content = null;
		this.errorHeader = error.indexOf('\n') != -1? error.substring(0, error.indexOf('\n')) : error;
		this.fullError = error;
	}
	public Result(Date when, LineStatusFeed content) {
		this.when = when;
		this.content = content;
		this.errorHeader = null;
		this.fullError = null;
	}
	public LineStatusFeed getContent() {
		return content;
	}
	public String getErrorHeader() {
		return errorHeader;
	}
	public String getFullError() {
		return fullError;
	}
	public Date getWhen() {
		return when;
	}
}
