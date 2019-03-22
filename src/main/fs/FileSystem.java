package main.fs;

import main.io.IOSystem;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FileSystem {

    private static final Logger LOGGER = Logger.getLogger(FileSystem.class.getName());

    private IOSystem ioSystem;

    public void create(String name) {
        LOGGER.log(Level.INFO, String.format("Create: name=%s", name));
    }

    public void destroy(String name) {
        LOGGER.log(Level.INFO, String.format("Destroy: name=%s", name));
    }

    public void open(String name) {
        LOGGER.log(Level.INFO, String.format("Open: name=%s", name));
    }

    public void close(int index) {
        LOGGER.log(Level.INFO, String.format("Close: index=%d", index));
    }

    public void read(int index, int count) {
        LOGGER.log(Level.INFO, String.format("Read: index=%d, count=%d", index, count));
    }

    public void write(int index, byte data, int count) {
        LOGGER.log(Level.INFO, String.format("Write: index=%d, data=%c, count=%d", index, data, count));
    }

    public void seek(int index, int position) {
        LOGGER.log(Level.INFO, String.format("Seek: index=%d, position=%d", index, position));
    }

    public void directory() {
        LOGGER.log(Level.INFO, "Directory");
    }

    public void input(String name) {
        LOGGER.log(Level.INFO, String.format("Input: name=%s", name));
    }

    public void save(String name) {
        LOGGER.log(Level.INFO, String.format("Save: name=%s", name));
    }
}
