package drrename.strategy;

import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.ResourceBundle;

public class CleanupStrategy extends RenamingStrategyProto {

	private final static String IDENTIFIER = "strategy.cleanup";

	private final ExtensionFromMimeStrategy extensionFromMimeStrategy;

	public CleanupStrategy(ResourceBundle resourceBundle) {
		super(resourceBundle);
		extensionFromMimeStrategy = new ExtensionFromMimeStrategy(resourceBundle);
	}

	@Override
	protected String getInternalId() {
		return IDENTIFIER;
	}

	@Override
	public String getNameNew(final Path file) {

		return extensionFromMimeStrategy.getNameNew(file).replaceAll("(?i)\\(case conflict\\)", "");

	}

	@Override
	public boolean isReplacing() {

		return false;
	}
}
