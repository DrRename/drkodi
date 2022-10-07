package drrename.strategy;

import java.nio.file.Path;
import java.util.ResourceBundle;

public class SimpleReplaceRenamingStrategy extends RenamingStrategyProto {

	private static final String IDENTIFIER = "strategy.simple-replace";

	public SimpleReplaceRenamingStrategy(ResourceBundle resourceBundle) {
        super(resourceBundle);
    }

	@Override
	protected String getInternalId() {
		return IDENTIFIER;
	}

	@Override
	public String getNameNew(final Path file) {

		return file.getFileName().toString().replace(getReplacementStringFrom(), getReplacementStringTo());
	}

	@Override
	public boolean isReplacing() {

		return true;
	}
}
