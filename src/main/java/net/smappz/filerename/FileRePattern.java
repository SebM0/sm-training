package net.smappz.filerename;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.*;

class FileRePattern {
    static Stream<FileChange> process(Path folder, String pattern) throws IOException {
        return Files.list(folder)
                .filter(f -> Files.isRegularFile(f))
                .map(f -> {
                    String fname = f.getFileName().toString();
                    String name = fname;
                    String ext = "";
                    int index = fname.lastIndexOf(".");
                    if (index > -1) {
                        name = fname.substring(0, index);
                        ext = fname.substring(index);
                    }
                    StringBuilder num = new StringBuilder();
                    for (char c : name.toCharArray()) {
                        if (Character.isDigit(c)) {
                            num.append(c);
                        }
                    }
                    if (num.length() > 0) {
                        try {
                            int value = Integer.parseInt(num.toString());
                            return new FileChange(f, pattern + ext, value);
                        } catch (NumberFormatException e) {
                            return null;
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull);
    }
}
