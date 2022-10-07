package drrename.strategy;

import org.apache.commons.text.WordUtils;

import java.nio.file.Path;
import java.util.ResourceBundle;

public class CapitalizeFirstStrategy extends RenamingStrategyProto {

    private static final String IDENTIFIER = "strategy.capitalize-first-letter";

    public CapitalizeFirstStrategy(ResourceBundle resourceBundle) {
        super(resourceBundle);
    }

    @Override
    protected String getInternalId() {
        return IDENTIFIER;
    }

    @Override
    public String getNameNew(Path file)  {
        return WordUtils.capitalize(file.getFileName().toString(), ' ', '_','-');
    }

    @Override
    public boolean isReplacing() {
        return false;
    }
}
