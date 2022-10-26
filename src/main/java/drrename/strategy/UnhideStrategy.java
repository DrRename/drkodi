
package drrename.strategy;

import lombok.extern.slf4j.Slf4j;

import java.util.ResourceBundle;

@Slf4j
public class UnhideStrategy extends RenamingStrategyProto {

	private static final String name_identifier = "strategy.unhide.name";

	private static final String help_identifier = "strategy.unhide.help";

	public UnhideStrategy(ResourceBundle resourceBundle, RenamingConfig renamingConfig) {
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
		if(fileNameString.matches(".+~\\d+~")){
			return fileNameString.substring(0, fileNameString.indexOf("~"));
		}
		if(fileNameString.endsWith("~")){
			return fileNameString.substring(0, fileNameString.length() - 1);
		}
		return fileNameString;
	}

	@Override
	public boolean isReplacing() {
		return false;
	}
}
