
package drrename.strategy;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.ResourceBundle;

@Slf4j
public class UnhideStrategy extends RenamingStrategyProto {

	private static final String IDENTIFIER = "strategy.unhide";

	public UnhideStrategy(ResourceBundle resourceBundle) {
        super(resourceBundle);
    }

	@Override
	protected String getInternalId() {
		return IDENTIFIER;
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
