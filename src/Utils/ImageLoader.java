package Utils;

import DataStructures.ArrayList.ArrayUnorderedList;
import DataStructures.Iterator;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * ImageLoader provides a utility to load and cache images from the file system.
 * It uses a list to store loaded images to avoid reading the same file multiple times.
 */
public class ImageLoader {

    /**
     * Helper class to store the association between a file path and its loaded image.
     */
    private static class TextureEntry {

        String path;
        BufferedImage image;

        /**
         * Creates a new TextureEntry with the specified path and image.
         *
         * @param path  the file path of the image
         * @param image the loaded BufferedImage
         */
        public TextureEntry(String path, BufferedImage image) {
            this.path = path;
            this.image = image;
        }
    }

    /**
     * A list to cache loaded textures.
     */
    private static final ArrayUnorderedList<TextureEntry> textureCache = new ArrayUnorderedList<>();

    /**
     * Retrieves an image from the specified path.
     * 
     * It first checks the cache to see if the image has already been loaded.
     * If not found, it attempts to load the image from the file system and adds it to the cache.
     *
     * @param path the file path of the image to load
     * @return the loaded BufferedImage, or null if the file does not exist or an error occurs
     */
    public static BufferedImage getImage(String path) {
        // Check if the image is already in the cache
        Iterator<TextureEntry> it = textureCache.iterator();
        while (it.hasNext()) {
            TextureEntry entry = it.next();
            if (entry.path.equals(path)) {
                return entry.image;
            }
        }

        // If not in cache, try to load from disk
        try {
            File file = new File(path);
            if (file.exists()) {
                BufferedImage image = ImageIO.read(file);
                
                // Add the newly loaded image to the cache
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