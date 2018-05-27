package net.twisterrob.test.hamcrest;

import javax.annotation.*;

import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.*;

public class Matchers {

	public static <T extends Throwable> Matcher<T> hasCause(final @Nullable Throwable cause) {
		return hasCause(sameInstance(cause));
	}
	@SuppressWarnings("deprecation")
	public static <T extends Throwable> Matcher<T> hasCause(final @Nonnull Matcher<?> matcher) {
		return org.junit.internal.matchers.ThrowableCauseMatcher.hasCause(matcher);
	}

	public static <T extends Throwable> Matcher<Throwable> containsCause(final @Nonnull Matcher<Throwable> matcher) {
		return HasCause.hasCause(matcher);
	}
	public static <T extends Throwable> Matcher<? super Throwable> containsCause(final @Nullable Throwable cause) {
		return HasCause.hasCause(cause);
	}

	public static <T extends Throwable> Matcher<T> hasMessage(final @Nullable String message) {
		return hasMessage(equalTo(message));
	}
	public static <T extends Throwable> Matcher<T> hasMessage(final @Nonnull Matcher<String> matcher) {
		return org.junit.internal.matchers.ThrowableMessageMatcher.hasMessage(matcher);
	}
}
