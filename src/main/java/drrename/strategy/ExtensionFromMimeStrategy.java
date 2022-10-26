package drrename.strategy;

import jodd.net.MimeTypes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.ResourceBundle;

@Slf4j
public class ExtensionFromMimeStrategy extends RenamingStrategyProto {

    private static final String name_identifier = "strategy.mime.file-extension.name";

    private static final String help_identifier = "strategy.mime.file-extension.help";

    public ExtensionFromMimeStrategy(ResourceBundle resourceBundle) {
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

        if(Files.isDirectory(file)){
            return file.getFileName().toString();
        }

        try {
            Tika tika = new Tika();
            String mimeType = tika.detect(file);
            if ("image/heic".equalsIgnoreCase(mimeType)) {
                return FilenameUtils.getBaseName(file.getFileName().toString()) + ".heic";
            }
            String[] type = MimeTypes.findExtensionsByMimeTypes(mimeType, false);
            if (type != null && type.length > 0) {
                if (Arrays.asList(type).contains("jpg")) {
                    return FilenameUtils.getBaseName(file.getFileName().toString()) + ".jpg";
                }
                if (Arrays.asList(type).contains("mov")) {
                    return FilenameUtils.getBaseName(file.getFileName().toString()) + ".mov";
                } else {
                    return FilenameUtils.getBaseName(file.getFileName().toString()) + "." + type[0];
                }
            }
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return file.getFileName().toString();
    }

    @Override
    public boolean isReplacing() {
        return false;
    }
}
