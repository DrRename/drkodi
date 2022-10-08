package drrename.strategy;

import java.nio.file.Path;
import java.util.ResourceBundle;

public class ToLowerCaseRenamingStrategy extends RenamingStrategyProto {

	private static final String name_identifier = "strategy.to-lower-case.name";

	private static final String help_identifier = "strategy.to-lower-case.help";

	public ToLowerCaseRenamingStrategy(ResourceBundle resourceBundle) {
        super(resourceBundle);
    }

	@Override
	protected String getNameId() {
		return name_identifier;
	}

	@Override
	protected String getHelpTextId() {
		return help_identifier;
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
