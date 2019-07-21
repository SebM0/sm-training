package net.smappz.filerename;

import java.awt.image.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import javax.imageio.*;

public class ImageReColor {
    public static void main(String[] args) throws IOException {
        // Color swap
        Map<Integer, Integer> swaps = new HashMap<>();
        // Hair
        swaps.put(rgb(254, 160, 0), rgb(0, 160, 0));
        swaps.put(rgb(255, 204, 0), rgb(0, 204, 0));
        swaps.put(rgb(255, 255, 0), rgb(0, 255, 0));
        // Band
        swaps.put(rgb(60, 0, 0), rgb(0, 0, 60));
        swaps.put(rgb(102, 0, 0), rgb(0, 0, 102));
        swaps.put(rgb(126, 0, 0), rgb(0, 0, 126));
        // Skirt
        swaps.put(rgb(206, 153, 100), rgb(184, 107, 235));
        swaps.put(rgb(220, 183, 146), rgb(204, 117, 245));
        swaps.put(rgb(227, 197, 167), rgb(204, 117, 245));
        swaps.put(rgb(238, 218, 198), rgb(214, 127, 255));
        swaps.put(rgb(238, 217, 197), rgb(214, 127, 255));
        swaps.put(rgb(237, 217, 193), rgb(214, 127, 255));

        String pathName = "D:\\DEV\\PyTests\\PyMonster\\platform_tutorial\\images\\adventure_girl_2";
        Path folder = Paths.get(pathName);
        Files.list(folder).filter(f -> Files.isRegularFile(f)).forEach(f -> {
            try {
                BufferedImage img = ImageIO.read(f.toFile());
                reColorImage(img, swaps);

                ImageIO.write(img, "png", f.toFile());
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        });
    }

    private static void reColorImage(BufferedImage img, Map<Integer,Integer> swaps) {
        int width = img.getWidth();
        int height = img.getHeight();

        // scan
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int color = img.getRGB(x, y);
                if (swaps.containsKey(color))
                    img.setRGB(x, y, swaps.get(color));
            }
        }
    }

    private static int rgb(int r, int g, int b) {
        return (0xFF << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF));
    }
}
