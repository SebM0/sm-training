package net.smappz.filerename;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.*;

public class FileToolBox {

    public static void main(String[] args) throws IOException {
        String pathName = "D:\\DEV\\PyTests\\PyMonster\\platform_tutorial\\images\\fire";
        Path path = Paths.get(pathName);
        //Stream<FileChange> changes = FileReNumber.process(path, "Explosion %02d");
        Stream<FileChange> changes = FileReName.process(path, "fire_1b_40_", "Fire ");
        changes.forEach(c -> {
            System.out.println(c.toString());
            c.rename();
        });
    }

    static String[] extractNameAndExtension(Path path) {
        String fname = path.getFileName().toString();
        String name = fname;
        String ext = "";
        int index = fname.lastIndexOf(".");
        if (index > -1) {
            name = fname.substring(0, index);
            ext = fname.substring(index);
        }
        return new String[]{name, ext};
    }
}
