package net.smappz.filerename;

import java.io.*;
import java.nio.file.Path;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

class FileReNumber {

    static Stream<FileChange> process(Path folder, String pattern) throws IOException {
        Stream<FileChange> changes = FileRePattern.process(folder, pattern);
        AtomicInteger ref = new AtomicInteger(0);
        return changes.sorted(FileChange::compareTo).map(c -> c.setNumber(ref.getAndIncrement()));
    }
}
