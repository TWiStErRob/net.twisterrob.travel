package net.twisterrob.travel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.gradle.api.Action;
import org.gradle.api.Task;
import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.JavaExec;

public class JavaExecExtensions {

	public static void logTo(JavaExec task, Provider<RegularFile> logFile) {
		task.doFirst(action((JavaExec it) -> {
			Path log = logFile.get().getAsFile().toPath();
			try {
				Files.createDirectories(log.getParent());
				it.setStandardOutput(Files.newOutputStream(log));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}));
		task.doLast(action((JavaExec it) -> {
			try {
				task.getStandardOutput().close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}));
	}

	@SuppressWarnings("unchecked")
	private static <T extends Task> Action<? super Task> action(Action<T> action) {
		return (Action<? super Task>)(Action<?>)action;
	}
}
