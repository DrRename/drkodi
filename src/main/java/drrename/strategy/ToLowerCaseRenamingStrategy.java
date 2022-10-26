package drrename.strategy;

import java.util.ResourceBundle;

public class ToLowerCaseRenamingStrategy extends RenamingStrategyProto {

	private static final String name_identifier = "strategy.to-lower-case.name";

	private static final String help_identifier = "strategy.to-lower-case.help";

	public ToLowerCaseRenamingStrategy(ResourceBundle resourceBundle, RenamingConfig renamingConfig) {
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

	@Override
	public String applyStrategyOnString(String fileNameString) {
		return fileNameString.toLowerCase();
	}

	@Override
	public boolean isReplacing() {
		return false;
	}
}
