package drrename.strategy;

import java.util.ResourceBundle;

public class SimpleReplaceRenamingStrategy extends RenamingStrategyProto {

	private static final String name_identifier = "strategy.simple-replace.name";

	private static final String help_identifier = "strategy.simple-replace.help";

	public SimpleReplaceRenamingStrategy(ResourceBundle resourceBundle, RenamingConfig renamingConfig) {
        super(resourceBundle, renamingConfig);
    }

	@Override
	protected String getNameId() {
		return name_identifier;
	}

	@Override
	protected String getHelpTextId() {
		return help_identifier;
	}

	public String applyStrategyOnString(String fileNameString) {
		return fileNameString.replace(getReplacementStringFrom(), getReplacementStringTo());
	}

	@Override
	public boolean isReplacing() {
		return true;
	}
}
