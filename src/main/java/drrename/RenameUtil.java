package drrename;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import drrename.kodi.NfoFileXmlModel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;

@Slf4j
public class RenameUtil {

    public static Path getImagePathFromNfo(Path nfoFile) throws IOException {
        XmlMapper mapper = new XmlMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try {
            NfoFileXmlModel xmlFileContent = mapper.readValue(nfoFile.toFile(), NfoFileXmlModel.class);
            return nfoFile.getParent().resolve(xmlFileContent.getArt().getPoster());
        } catch (JsonParseException e) {
            log.debug("Failed to deserialize {} ({})", nfoFile, e.toString());
        }
        return null;
    }
}
