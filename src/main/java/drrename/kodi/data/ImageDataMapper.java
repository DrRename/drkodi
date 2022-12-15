package drrename.kodi.data;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
public class ImageDataMapper {

    ImageData map(byte[] imageData){
        return ImageData.from(imageData);
    }
}
