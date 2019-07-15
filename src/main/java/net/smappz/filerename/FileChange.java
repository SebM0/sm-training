package net.smappz.filerename;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

class FileChange implements Comparable<FileChange> {
    private final Path m_source;
    private final String m_targetName;
    private int m_number;
    private boolean m_formatted;

    FileChange(Path source, String targetName) {
        m_source = source;
        m_targetName = targetName;
        m_number = 0;
        m_formatted = true;
    }

    FileChange(Path source, String pattern, int number) {
        m_source = source;
        m_targetName = pattern;
        m_number = number;
        m_formatted = false;
    }

    FileChange setNumber(int number) {
        m_number = number;
        return this;
    }

    void rename() {
        Path targetFile = m_source.getParent().resolve(format());
        try {
            Files.move(m_source, targetFile);
        } catch (IOException e) {
            System.err.println("Failed to rename " + m_source + " to " + targetFile);
            throw new RuntimeException(e);
        }
    }

    private String format() {
        if (m_formatted) {
            return m_targetName;
        } else {
            return String.format(m_targetName, m_number);
        }
    }

    @Override
    public String toString() {
        return "FileChange{" + "m_source=" + m_source + ", m_targetName='" + format() + '\'' + '}';
    }

    @Override
    public int compareTo(FileChange o) {
        return m_formatted ? m_targetName.compareTo(o.m_targetName) : Integer.compare(m_number, o.m_number);
    }
}
