package drrename.strategy;

import org.apache.commons.text.WordUtils;

import java.util.ResourceBundle;

public class CapitalizeFirstStrategy extends RenamingStrategyProto {

    private static final String name_identifier = "strategy.capitalize-first-letter.name";

    private static final String help_text_identifier = "strategy.capitalize-first-letter.help";

    public CapitalizeFirstStrategy(ResourceBundle resourceBundle, RenamingConfig renamingConfig) {
        super(resourceBundle, renamingConfig);
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
        return String.format(getResourceBundle().getString(getNameId()));
    }

    @Override
    public String applyStrategyOnString(String fileNameString) {
        return WordUtils.capitalize(fileNameString, ' ', '_', '-');
    }

    @Override
    public boolean isReplacing() {
        return false;
    }
}
