package net.smappz.filerename;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.*;

public class FileRename {

    public static void main(String[] args) throws IOException {
        String pathName = "D:\\DEV\\PyTests\\PyMonster\\platform_tutorial\\images\\explosion";
        Path path = Paths.get(pathName);
        Stream<FileChange> changes = FileReNumber.process(path, "Explosion %02d");
        changes.forEach(c -> {
            System.out.println(c.toString());
            c.rename();
        });
    }
}
