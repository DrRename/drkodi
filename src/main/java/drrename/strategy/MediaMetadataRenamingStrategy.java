package drrename.strategy;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Slf4j
public class MediaMetadataRenamingStrategy extends RenamingStrategyProto {

    public static final DateTimeFormatter DATE_FORMATTER_WRITE = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    public static final List<DateTimeFormatter> DATE_FORMATTERS_READ = new ArrayList<>();

    private static final String name_identifier = "strategy.metadata.date-to-name.name";

    private static final String help_identifier = "strategy.metadata.date-to-name.help";

    static {
        DATE_FORMATTERS_READ.add(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss"));
        DATE_FORMATTERS_READ.add(DateTimeFormatter.ofPattern("EE MMM dd HH:mm:ss XXX yyyy"));
    }

    public MediaMetadataRenamingStrategy(ResourceBundle resourceBundle, RenamingConfig renamingConfig) {
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
    public String getNameNew(final Path file) {

        try {
            final Metadata metadata = ImageMetadataReader.readMetadata(file.toFile());
            for (final Directory directory : metadata.getDirectories()) {

                for (final Tag tag : directory.getTags()) {

                    int tagType = tag.getTagType();
                    if (306 == tagType) {

                        for (final DateTimeFormatter df : DATE_FORMATTERS_READ) {

                            try {
                                final TemporalAccessor dt = df.parse(tag.getDescription());
                                return DATE_FORMATTER_WRITE.format(dt) + "." + FilenameUtils.getExtension(file.toString());
                            } catch (final DateTimeParseException e) {
                                log.debug(e.toString());
                            }
                        }
                    }
                }
            }
        } catch (final Exception e) {

            log.debug(e.getLocalizedMessage() + " for " + file.getFileName());
        }

        log.debug("Failed to read meta data from file.");

        return file.getFileName().toString();
    }

    @Override
    public boolean isReplacing() {

        return false;
    }
}
