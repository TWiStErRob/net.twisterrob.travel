package net.twisterrob.blt.io.feeds;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class BaseFeed<T extends BaseFeed<?>> {
	@OverridingMethodsMustInvokeSuper
	protected void postProcess() {
		// optional override
	}

	/**
	 * @param other
	 * @return a new instance
	 */
	public T merge(T other) {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not support merging.");
	}
}
