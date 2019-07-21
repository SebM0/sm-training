package net.smappz.filerename;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.*;

class FileReName {
    static Stream<FileChange> process(Path folder, String source, String replace) throws IOException {
        return Files.list(folder).filter(f -> Files.isRegularFile(f)).map(f -> {
            String[] nameAndExtension = FileToolBox.extractNameAndExtension(f);
            String name = nameAndExtension[0];
            String ext = nameAndExtension[1];
            int index = name.indexOf(source);
            if (index > -1) {
                StringBuilder target = new StringBuilder();
                if (index > 0)
                    target.append(name.substring(0, index));
                target.append(replace);
                if (name.length() > index + source.length())
                    target.append(name.substring(index + source.length()));
                target.append(ext);
                return new FileChange(f, target.toString());
            }
            return null;
        }).filter(Objects::nonNull);
    }
}
