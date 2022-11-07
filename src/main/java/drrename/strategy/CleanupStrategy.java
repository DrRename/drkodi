package drrename.strategy;

import java.nio.file.Path;
import java.util.ResourceBundle;

public class CleanupStrategy extends RenamingStrategyProto {

    private final static String name_identifier = "strategy.cleanup.name";

    private final static String help_text_identifier = "strategy.cleanup.help";

    private final ExtensionFromMimeStrategy extensionFromMimeStrategy;

    public CleanupStrategy(ResourceBundle resourceBundle, RenamingConfig renamingConfig) {
        super(resourceBundle, renamingConfig);
        extensionFromMimeStrategy = new ExtensionFromMimeStrategy(resourceBundle, renamingConfig);
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
    public String getNameNew(Path file) {
        return extensionFromMimeStrategy.getNameNew(file).replaceAll("(?i)\\(case conflict\\)", "");
    }

    @Override
    public boolean isReplacing() {
        return false;
    }
}
