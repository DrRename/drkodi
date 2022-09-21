package drrename.strategy;

import java.nio.file.Path;

public class ToLowerCaseRenamingStrategy extends RenamingStrategyProto {

	@Override
	public String getIdentifier() {

		return "To Lower Case";
	}

	@Override
	public String getNameNew(final Path file) {

		return file.getFileName().toString().toLowerCase();
	}

	@Override
	public boolean isReplacing() {

		return false;
	}
}
