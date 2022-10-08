package drrename.strategy;

import org.apache.commons.text.CaseUtils;

import java.nio.file.Path;
import java.util.ResourceBundle;

public class SpaceToCamelCaseRenamingStrategy extends RenamingStrategyProto {

	private static final String name_identifier = "strategy.space-to-camel-case.name";

	private static final String help_identifier = "strategy.space-to-camel-case.help";

	public SpaceToCamelCaseRenamingStrategy(ResourceBundle resourceBundle) {
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
	public String getNameNew(final Path file)  {


		if(!file.getFileName().toString().matches("\\S+")){
			String newName = CaseUtils.toCamelCase(file.getFileName().toString(), false, ' ');
			return newName;
		}
		return file.getFileName().toString();
	}

	@Override
	public boolean isReplacing() {

		return false;
	}
}
