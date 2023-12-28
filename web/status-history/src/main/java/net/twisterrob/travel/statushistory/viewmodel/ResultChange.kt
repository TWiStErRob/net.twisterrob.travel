package net.twisterrob.travel.statushistory.viewmodel;

import java.util.*;

import net.twisterrob.blt.diff.HtmlDiff;
import net.twisterrob.blt.io.feeds.trackernet.model.LineStatus;
import net.twisterrob.blt.model.Line;

public class ResultChange {
	private Result oldResult;
	private Result newResult;
	private ErrorChange errorChange;
	private Map<Line, StatusChange> statusChanges;
	private Map<Line, String> descChanges;
	public ResultChange(Result oldResult, Result newResult) {
		this.oldResult = oldResult;
		this.newResult = newResult;
		statusChanges = new EnumMap<>(Line.class);
		descChanges = new EnumMap<>(Line.class);
		diff();
	}
	public Result getPrevious() {
		return oldResult;
	}
	public Result getCurrent() {
		return newResult;
	}
	public ErrorChange getError() {
		return errorChange;
	}
	public Map<Line, StatusChange> getStatuses() {
		return statusChanges;
	}
	public Map<Line, String> getDescriptions() {
		return descChanges;
	}

	private void diff() {
		if (oldResult != null && newResult != null) { // after this check fails one of them must be null
			diffError();
			diffContent();
		} else if (newResult != null) {
			errorChange = ErrorChange.NewStatus;
		} else if (oldResult != null) {
			errorChange = ErrorChange.LastStatus;
		} else {
			errorChange = ErrorChange.NoErrors;
		}
	}

	protected void diffContent() {
		if (oldResult.getContent() == null || newResult.getContent() == null) {
			return;
		}
		Map<Line, LineStatus> oldMap = oldResult.getContent().getStatusMap();
		Map<Line, LineStatus> newMap = newResult.getContent().getStatusMap();
		Set<Line> allLines = new HashSet<>();
		allLines.addAll(oldMap.keySet());
		allLines.addAll(newMap.keySet());
		for (Line line : allLines) {
			LineStatus oldStatus = oldMap.get(line);
			LineStatus newStatus = newMap.get(line);
			if (oldStatus == null || newStatus == null) {
				statusChanges.put(line, StatusChange.Unknown);
				continue;
			}
			int statusDiff = oldStatus.getType().compareTo(newStatus.getType());
			if (statusDiff < 0) {
				statusChanges.put(line, StatusChange.Better);
			} else if (statusDiff > 0) {
				statusChanges.put(line, StatusChange.Worse);
			} else /* (statusDiff == 0) */ {
				statusChanges.put(line, StatusChange.Same);
				diffDesc(line, oldStatus, newStatus);
			}
		}
	}

	protected void diffDesc(Line line, LineStatus oldStatus, LineStatus newStatus) {
		String oldDesc = oldStatus.getDescription();
		String newDesc = newStatus.getDescription();
		if (oldDesc != null && newDesc != null) { // after this check fails one of them must be null
			if (oldDesc.equals(newDesc)) {
				String oldBranches = oldStatus.getBranchDescription();
				String newBranches = newStatus.getBranchDescription();
				if (oldBranches.equals(newBranches)) {
					statusChanges.put(line, StatusChange.SameDescriptionSame);
				} else {
					statusChanges.put(line, StatusChange.BranchesChange);
					descChanges.put(line, diffDesc(oldBranches, newBranches));
				}
			} else {
				statusChanges.put(line, StatusChange.SameDescriptionChange);
				descChanges.put(line, diffDesc(oldDesc, newDesc));
			}
		} else if (oldDesc == null && newDesc != null) {
			statusChanges.put(line, StatusChange.SameDescriptionAdd);
			descChanges.put(line, diffDesc("", newDesc));
		} else if (oldDesc != null && newDesc == null) {
			statusChanges.put(line, StatusChange.SameDescriptionDel);
			descChanges.put(line, diffDesc(oldDesc, ""));
		} else if (oldDesc == null && newDesc == null) {
			statusChanges.put(line, StatusChange.SameDescriptionSame);
		}
	}

	private static String diffDesc(String oldDesc, String newDesc) {
		return new HtmlDiff().diff(oldDesc, newDesc);
	}

	protected void diffError() {
		String oldErrorHeader = oldResult.getErrorHeader();
		String newErrorHeader = newResult.getErrorHeader();
		if (oldErrorHeader != null && newErrorHeader != null) { // after this check fails one of them must be null
			errorChange = oldErrorHeader.equals(newErrorHeader)? ErrorChange.Same : ErrorChange.Change;
		} else if (newErrorHeader != null) {
			errorChange = ErrorChange.Failed;
		} else if (oldErrorHeader != null) {
			errorChange = ErrorChange.Fixed;
		} else {
			errorChange = ErrorChange.NoErrors;
		}
	}

	public enum StatusChange {
		Better("better", "status-better"),
		Worse("worse", "status-worse"),
		Same("", "status-same"),
		Unknown("unknown", "status-unknown"),
		SameDescriptionSame("", "status-same-desc-same"),
		SameDescriptionChange("descr.", "status-same-desc-change"),
		SameDescriptionAdd("+ descr.", "status-same-desc-add"),
		SameDescriptionDel("- descr.", "status-same-desc-del"),
		BranchesChange("branches", "status-same-branch-change");
		private final String title;
		private final String cssClass;
		StatusChange(String humanReadable, String cssClass) {
			this.title = humanReadable;
			this.cssClass = cssClass;
		}
		public String getTitle() {
			return title;
		}
		public String getCssClass() {
			return cssClass;
		}
	}

	public enum ErrorChange {
		Same("same error"),
		Change("error changed"),
		Failed("new error"),
		Fixed("error fixed"),
		NoErrors(""),
		NewStatus(""),
		LastStatus("");
		private final String title;
		ErrorChange(String humanReadable) {
			title = humanReadable;
		}
		public String getTitle() {
			return title;
		}
	}
}
