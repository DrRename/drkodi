package drrename.strategy;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.regex.PatternSyntaxException;

@Slf4j
public class RegexReplaceRenamingStrategy extends RenamingStrategyProto {

    private static final String name_identifier = "strategy.regex-replace.name";

    private static final String help_identifier = "strategy.regex-replace.help";

    public RegexReplaceRenamingStrategy(ResourceBundle resourceBundle) {
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

        try {
            return file.getFileName().toString().replaceAll(getReplacementStringFrom(), getReplacementStringTo());
        } catch (final PatternSyntaxException e) {

            log.debug(e.getLocalizedMessage());
        }
        return file.getFileName().toString();
    }

    @Override
    public boolean isReplacing() {
        return true;
    }
}
