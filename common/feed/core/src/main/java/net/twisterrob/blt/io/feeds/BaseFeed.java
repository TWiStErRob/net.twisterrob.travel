package net.twisterrob.blt.io.feeds;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class BaseFeed<T extends BaseFeed<?>> {
	@OverridingMethodsMustInvokeSuper
	protected void postProcess() {
		// optional override
	}

	/**
	 * @param other the other feed to merge with
	 * @return a new instance of the same class as {@code this} and {@code other}
	 * @throws UnsupportedOperationException if merging is not possible
	 */
	public T merge(T other) throws UnsupportedOperationException {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not support merging.");
	}
}
