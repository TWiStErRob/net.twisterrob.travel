package net.twisterrob.travel;

import java.util.List;

import org.gradle.api.file.Directory;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.process.CommandLineArgumentProvider;

public class InputDirectoryArgumentProvider implements CommandLineArgumentProvider {

	private final Provider<Directory> directory;

	public InputDirectoryArgumentProvider(Provider<Directory> directory) {
		this.directory = directory;
	}

	@InputDirectory
	@PathSensitive(PathSensitivity.NONE)
	public Provider<Directory> getDirectory() {
		return directory;
	}

	@Override
	public Iterable<String> asArguments() {
		return List.of(directory.get().getAsFile().getAbsolutePath());
	}
}
