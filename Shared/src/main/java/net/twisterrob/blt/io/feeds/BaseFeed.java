package net.twisterrob.blt.io.feeds;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class BaseFeed {
	@OverridingMethodsMustInvokeSuper
	protected void postProcess() {
		// optional @Override
	}
}
