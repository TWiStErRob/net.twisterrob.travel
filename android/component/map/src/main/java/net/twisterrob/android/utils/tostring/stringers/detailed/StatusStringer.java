package net.twisterrob.android.utils.tostring.stringers.detailed;

import android.annotation.SuppressLint;

import com.google.android.gms.common.api.Status;

import androidx.annotation.NonNull;

import net.twisterrob.java.utils.tostring.Stringer;
import net.twisterrob.java.utils.tostring.ToStringAppender;

public class StatusStringer extends Stringer<Status> {
	@SuppressLint("VisibleForTests")
	@Override public void toString(@NonNull ToStringAppender append, Status status) {
		append.rawProperty("code", status.getStatusCode());
		append.rawProperty("message", status.getStatusMessage());
		append.booleanProperty(status.isSuccess(), "success");
		append.booleanProperty(status.isCanceled(), "cancelled");
		append.booleanProperty(status.isInterrupted(), "interrupted");
		append.booleanProperty(status.hasResolution(), "has resolution", "no resolution");
		if (status.hasResolution()) {
			append.complexProperty("resolution", status.getResolution());
		}
	}
}
