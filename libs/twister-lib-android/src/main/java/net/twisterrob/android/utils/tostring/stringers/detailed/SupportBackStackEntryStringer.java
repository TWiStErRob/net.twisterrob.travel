package net.twisterrob.android.utils.tostring.stringers.detailed;

import javax.annotation.Nonnull;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentManager.BackStackEntry;

import net.twisterrob.android.utils.tools.AndroidTools;
import net.twisterrob.java.utils.tostring.*;

public class SupportBackStackEntryStringer extends Stringer<BackStackEntry> {
	private final Resources resources;
	public SupportBackStackEntryStringer(Context context) {
		this.resources = context.getResources();
	}

	@Override public void toString(@Nonnull ToStringAppender append, BackStackEntry entry) {
		append.identity(entry.getId(), entry.getName());
		append.formattedProperty("shortTitle",
				Integer.toHexString(entry.getBreadCrumbShortTitleRes()),
				"%s/%s",
				resourceIdToString(entry.getBreadCrumbShortTitleRes()),
				entry.getBreadCrumbShortTitle()
		);
		append.formattedProperty("title",
				Integer.toHexString(entry.getBreadCrumbTitleRes()),
				"%s/%s",
				resourceIdToString(entry.getBreadCrumbTitleRes()),
				entry.getBreadCrumbTitle()
		);
	}

	private String resourceIdToString(@StringRes int res) {
		if (res != AndroidTools.INVALID_RESOURCE_ID) {
			try {
				return resources.getResourceName(res);
			} catch (Resources.NotFoundException ex) {
				// ignore, returns null
			}
		}
		return null;
	}
}
