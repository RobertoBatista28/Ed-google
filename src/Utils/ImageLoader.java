package Utils;

import DataStructures.ArrayList.ArrayUnorderedList;
import DataStructures.Iterator;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageLoader {

    private static class TextureEntry {

        String path;
        BufferedImage image;

        public TextureEntry(String path, BufferedImage image) {
            this.path = path;
            this.image = image;
        }
    }

    private static final ArrayUnorderedList<TextureEntry> textureCache = new ArrayUnorderedList<>();

    public static BufferedImage getImage(String path) {
        Iterator<TextureEntry> it = textureCache.iterator();
        while (it.hasNext()) {
            TextureEntry entry = it.next();
            if (entry.path.equals(path)) {
                return entry.image;
            }
        }

        try {
            File file = new File(path);
            if (file.exists()) {
                BufferedImage image = ImageIO.read(file);
                textureCache.add(new TextureEntry(path, image));
                return image;
            } else {
                System.err.println("[ImageLoader] Ficheiro não encontrado: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("[ImageLoader] Erro ao ler imagem: " + e.getMessage());
        }

        return null;
    }
}
