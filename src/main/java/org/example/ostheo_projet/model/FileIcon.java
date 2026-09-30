package org.example.ostheo_projet.model;

import org.example.ostheo_projet.utility.Utils;

import java.io.File;
import java.io.InputStream;

public class FileIcon {
    private File file;

    public FileIcon(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public InputStream getStream() {
        String extension = this.file.getName().substring(file.getName().lastIndexOf('.') + 1);
        return Utils.getIconStream(extension);
    }

}
