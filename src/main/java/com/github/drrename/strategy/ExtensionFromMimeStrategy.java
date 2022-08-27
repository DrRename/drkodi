package com.github.drrename.strategy;

import jodd.net.MimeTypes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeType;
import org.bouncycastle.mime.BasicMimeParser;
import org.bouncycastle.mime.MimeParser;
import org.bouncycastle.mime.MimeParserContext;
import org.springframework.util.MimeTypeUtils;

import java.io.IOException;
import java.nio.file.Files;
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
			if(type.length > 1 && Arrays.stream(type).anyMatch(s -> "jpg".equals(s))){
				return FilenameUtils.getBaseName(file.getFileName().toString()) + ".jpg";
			}
			if(type.length > 1 && Arrays.stream(type).anyMatch(s -> "mov".equals(s))){
				return FilenameUtils.getBaseName(file.getFileName().toString()) + ".mov";
			}
			else {
				return FilenameUtils.getBaseName(file.getFileName().toString()) + "." + type[0];
			}


		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
			return file.getFileName().toString();
		}
	}

	@Override
	public boolean isReplacing() {

		return false;
	}
}
