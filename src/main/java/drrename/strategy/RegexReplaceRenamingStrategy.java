package drrename.strategy;

import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.regex.PatternSyntaxException;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class RegexReplaceRenamingStrategy extends RenamingStrategyProto {

	private static final String IDENTIFIER = "strategy.regex-replace";

	public RegexReplaceRenamingStrategy(ResourceBundle resourceBundle) {
        super(resourceBundle);
    }

	@Override
	protected String getInternalId() {
		return IDENTIFIER;
	}

	@Override
	public String getNameNew(final Path file)  {

		try {
			return file.getFileName().toString().replaceAll(getReplacementStringFrom(),getReplacementStringTo());
		} catch(final PatternSyntaxException e) {

				log.debug(e.getLocalizedMessage());
		}
		return file.getFileName().toString();
	}

	@Override
	public boolean isReplacing() {

		return true;
	}
}
