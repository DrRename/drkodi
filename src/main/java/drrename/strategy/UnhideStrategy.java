
package drrename.strategy;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.ResourceBundle;

@Slf4j
public class UnhideStrategy extends RenamingStrategyProto {

	private static final String name_identifier = "strategy.unhide.name";

	private static final String help_identifier = "strategy.unhide.help";

	public UnhideStrategy(ResourceBundle resourceBundle) {
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

		if(file.getFileName().toString().matches(".+\\~\\d+\\~")){
			return file.getFileName().toString().substring(0, file.getFileName().toString().indexOf("~") - 1);
		}
		if(file.getFileName().toString().endsWith("~")){
			return file.getFileName().toString().substring(0, file.getFileName().toString().length() - 1);
		}
		return file.getFileName().toString();
	}

	@Override
	public boolean isReplacing() {

		return false;
	}
}
