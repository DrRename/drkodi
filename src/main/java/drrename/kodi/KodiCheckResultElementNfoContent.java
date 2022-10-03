package drrename.kodi;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Getter
public class KodiCheckResultElementNfoContent extends KodiCheckResultElement<KodiCheckResultElementNfoContent.NfoFileContentType> {

    static class NfoFileXmlModel {

    }

    public enum NfoFileContentType {NO_FILE}

    public KodiCheckResultElementNfoContent(String movieName, NfoFileContentType object) {
        super(KodiCheckResult.Type.NFO_CONTENT, movieName, object);
    }

    static KodiCheckResultElementNfoContent parse(Path path) throws IOException {
        String movieName = path.getFileName().toString();
        KodiCheckResultElementNfoContent.NfoFileContentType john = parse2(path);
        return new KodiCheckResultElementNfoContent(movieName, john);
    }

     static KodiCheckResultElementNfoContent.NfoFileContentType parse2(Path path) throws IOException {
         XmlMapper mapper = new XmlMapper();
         mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
         try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
             for (Path child : ds) {
                 String extension = FilenameUtils.getExtension(child.getFileName().toString());
                 if (Files.isRegularFile(child) && "nfo".equalsIgnoreCase(extension)) {
                     // we look only at the first file found
                     try {
                         NfoFileXmlModel xmlFileContent = mapper.readValue(child.toFile(), NfoFileXmlModel.class);
                         log.debug("XML content: {}", xmlFileContent);
                     }catch(JsonParseException e){
                         log.debug("Not an XML file, check only one line for URL");
                         String content = Files.readString(child);
                         log.debug("File content: {}", content);
                     }
                 }
             }
         }

        return NfoFileContentType.NO_FILE;

    }
}
