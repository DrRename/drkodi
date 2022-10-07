package drrename.strategy;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

public class MediaMetadataRenamingStrategy extends RenamingStrategyProto {

	private static final Logger logger = LoggerFactory.getLogger(MediaMetadataRenamingStrategy.class);
	public static final DateTimeFormatter DATE_FORMATTER_WRITE = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
	public static final List<DateTimeFormatter> DATE_FORMATTERS_READ = new ArrayList<>();
	private static final String IDENTIFIER = "strategy.metadata.date-to-name";

	static {
		DATE_FORMATTERS_READ.add(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss"));
		DATE_FORMATTERS_READ.add(DateTimeFormatter.ofPattern("EE MMM dd HH:mm:ss XXX yyyy"));
	}

    public MediaMetadataRenamingStrategy(ResourceBundle resourceBundle) {
        super(resourceBundle);
    }

	@Override
	protected String getInternalId() {
		return IDENTIFIER;
	}

	@Override
	public String getNameNew(final Path file) {

		try {
			final Metadata metadata = ImageMetadataReader.readMetadata(file.toFile());
			for(final Directory directory : metadata.getDirectories()) {
				// System.out.println("---------------------");
				for(final Tag tag : directory.getTags()) {
					// System.out.println(tag);
					int tagType = tag.getTagType();
					if (306 == tagType) {
						// System.err.println(tag);
						for (final DateTimeFormatter df : DATE_FORMATTERS_READ) {
							// System.err.println(ZonedDateTime.now().format(df));
							try {
								final TemporalAccessor dt = df.parse(tag.getDescription());
								final String result = DATE_FORMATTER_WRITE.format(dt) + "." + FilenameUtils.getExtension(file.toString());
								return result;
							} catch (final DateTimeParseException e) {
								if (logger.isDebugEnabled())
									logger.debug(e.toString());
								final int i = 0;
							}
						}
					}
					// System.out.println("---------------------");
					final int i = 0;
				}
			}
		} catch(final Exception e) {
			if(logger.isDebugEnabled())
				logger.debug(e.getLocalizedMessage() + " for " + file.getFileName());
		}
		if(logger.isDebugEnabled()){
			logger.debug("Failed to read meta data from file.");
		}
		return file.getFileName().toString();
	}

	@Override
	public boolean isReplacing() {

		return false;
	}
}
