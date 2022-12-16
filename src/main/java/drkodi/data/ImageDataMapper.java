package drkodi.data;

import org.springframework.stereotype.Component;

@Component
public class ImageDataMapper {

    ImageData map(byte[] imageData){
        return ImageData.from(imageData);
    }
}
