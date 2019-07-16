package net.smappz.filerename;

import java.awt.image.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.*;

public class ImageCrop {
    public static void main(String[] args) throws IOException {
        String pathName = "D:\\DEV\\PyTests\\PyMonster\\platform_tutorial\\images\\zombie_2";
        Path folder = Paths.get(pathName);
        Files.list(folder).filter(f -> Files.isRegularFile(f)).forEach(f -> {
            try {
                BufferedImage img = ImageIO.read(f.toFile());
                img = cropImage(img);
                //String fname = f.getFileName().toString();
                //String name = fname;
                //String ext = "";
                //int index = fname.lastIndexOf(".");
                //if (index > -1) {
                //    name = fname.substring(0, index);
                //    ext = fname.substring(index);
                //}
                //Path targetFile = folder.resolve(name + "_crop" + ext);
                ImageIO.write(img, "png", f.toFile());
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        });
    }

    private static BufferedImage cropImage(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();

        // scan left
        int left = -1;
        for (int x=0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int color = img.getRGB(x, y);
                if (color != 0) {
                    left = x;
                    break;
                }
            }
            if (left > -1)
                break;
        }
        // scan right
        int right = -1;
        for (int x = width-1; x >= 0; x--) {
            for (int y = 0; y < height; y++) {
                int color = img.getRGB(x, y);
                if (color != 0) {
                    right = x;
                    break;
                }
            }
            if (right > -1)
                break;
        }
        // scan top
        int top = -1;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int color = img.getRGB(x, y);
                if (color != 0) {
                    top = y;
                    break;
                }
            }
            if (top > -1)
                break;
        }
        // scan bottom
        int bottom = -1;
        for (int y = height-1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                int color = img.getRGB(x, y);
                if (color != 0) {
                    bottom = y;
                    break;
                }
            }
            if (bottom > -1)
                break;
        }
        return img.getSubimage(left, top, right-left, bottom-top);
    }
}
