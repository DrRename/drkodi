package drrename.strategy;

import java.nio.file.Path;
import java.util.ResourceBundle;

public class SimpleReplaceRenamingStrategy extends RenamingStrategyProto {

	private static final String name_identifier = "strategy.simple-replace.name";

	private static final String help_identifier = "strategy.simple-replace.help";

	public SimpleReplaceRenamingStrategy(ResourceBundle resourceBundle) {
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
		return file.getFileName().toString().replace(getReplacementStringFrom(), getReplacementStringTo());
	}

	@Override
	public boolean isReplacing() {
		return true;
	}
}
