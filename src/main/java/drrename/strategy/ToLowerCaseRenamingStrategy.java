package drrename.strategy;

import java.nio.file.Path;
import java.util.ResourceBundle;

public class ToLowerCaseRenamingStrategy extends RenamingStrategyProto {

	private static final String IDENTIFIER = "strategy.to-lower-case";

	public ToLowerCaseRenamingStrategy(ResourceBundle resourceBundle) {
        super(resourceBundle);
    }

	@Override
	protected String getInternalId() {
		return IDENTIFIER;
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
