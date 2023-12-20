package net.twisterrob.travel;

import java.util.List;

import org.gradle.api.file.Directory;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.process.CommandLineArgumentProvider;

public class OutputDirectoryArgumentProvider implements CommandLineArgumentProvider {

	private final Provider<Directory> directory;

	public OutputDirectoryArgumentProvider(Provider<Directory> directory) {
		this.directory = directory;
	}

	@OutputDirectory
	public Provider<Directory> getDirectory() {
		return directory;
	}

	@Override
	public Iterable<String> asArguments() {
		return List.of(directory.get().getAsFile().getAbsolutePath());
	}
}
