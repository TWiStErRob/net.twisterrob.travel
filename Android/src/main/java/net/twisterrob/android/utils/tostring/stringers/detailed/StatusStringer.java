package net.twisterrob.android.utils.tostring.stringers.detailed;

import javax.annotation.Nonnull;

import com.google.android.gms.common.api.Status;

import net.twisterrob.java.utils.tostring.*;

public class StatusStringer extends Stringer<Status> {
	@Override public void toString(@Nonnull ToStringAppender append, Status status) {
		append.rawProperty("code", status.getStatusCode());
		append.rawProperty("message", status.getStatusMessage());
		append.booleanProperty(status.isSuccess(), "success");
		append.booleanProperty(status.isCanceled(), "cancelled");
		append.booleanProperty(status.isInterrupted(), "interrupted");
		append.booleanProperty(status.hasResolution(), "has resolution", "no resolution");
		if (status.hasResolution()) {
			append.complexProperty("resolution", status.getResolution());
		}
		if (status != status.getStatus()) {
			append.complexProperty("status", status.getStatus());
		}
	}
}
