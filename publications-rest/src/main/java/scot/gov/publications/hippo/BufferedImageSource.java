package scot.gov.publications.hippo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class BufferedImageSource {

    public BufferedImage get(InputStream inputStream) throws IOException {
        return ImageIO.read(inputStream);
    }
}
