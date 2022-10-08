package drrename.strategy;

import org.apache.commons.text.WordUtils;

import java.nio.file.Path;
import java.util.ResourceBundle;

public class CapitalizeFirstStrategy extends RenamingStrategyProto {

    private static final String name_identifier = "strategy.capitalize-first-letter.name";

    private static final String help_text_identifier = "strategy.capitalize-first-letter.help";

    public CapitalizeFirstStrategy(ResourceBundle resourceBundle) {
        super(resourceBundle);
    }

    @Override
    protected String getNameId() {
        return name_identifier;
    }

    @Override
    protected String getHelpTextId() {
        return help_text_identifier;
    }

    @Override
    public String getHelpText() {
        return String.format(String.format(getResourceBundle().getString(getNameId())));
    }

    @Override
    public String getNameNew(Path file) {
        return WordUtils.capitalize(file.getFileName().toString(), ' ', '_', '-');
    }

    @Override
    public boolean isReplacing() {
        return false;
    }
}
