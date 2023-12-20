package net.twisterrob.travel;

import java.util.List;

import org.gradle.api.file.Directory;
import org.gradle.api.provider.Provider;
import org.gradle.process.CommandLineArgumentProvider;

public abstract class DirectoryArgumentProvider implements CommandLineArgumentProvider {

	private final Provider<Directory> directory;

	public DirectoryArgumentProvider(Provider<Directory> directory) {
		this.directory = directory;
	}

	public Provider<Directory> getDirectory() {
		return directory;
	}

	@Override
	public Iterable<String> asArguments() {
		return List.of(directory.get().getAsFile().getAbsolutePath());
	}
}
