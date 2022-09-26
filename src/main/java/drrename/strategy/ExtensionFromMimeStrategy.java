package drrename.strategy;

import jodd.net.MimeTypes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;

import java.nio.file.Path;
import java.util.Arrays;

@Slf4j
public class ExtensionFromMimeStrategy extends RenamingStrategyProto {

	@Override
	public String getIdentifier() {

		return "File Extension From MIME Type";
	}

	@Override
	public String getNameNew(final Path file) {

//		if("heic".equalsIgnoreCase(FilenameUtils.getExtension(file.getFileName().toString()))){
//			// this does not seem to work properly, igore such files
//			return file.getFileName().toString();
//		}
		try {
			Tika tika = new Tika();
			String mimeType = tika.detect(file);
			if("image/heic".equalsIgnoreCase(mimeType)){
				return FilenameUtils.getBaseName(file.getFileName().toString()) + ".heic";
			}
			String[] type = MimeTypes.findExtensionsByMimeTypes(mimeType, false);
			if(type != null && type.length > 0) {
				if (Arrays.stream(type).anyMatch(s -> "jpg".equals(s))) {
					return FilenameUtils.getBaseName(file.getFileName().toString()) + ".jpg";
				}
				if (Arrays.stream(type).anyMatch(s -> "mov".equals(s))) {
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
