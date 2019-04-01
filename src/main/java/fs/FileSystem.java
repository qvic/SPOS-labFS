package fs;

import exceptions.DiskIsFullException;
import exceptions.NoFreeDescriptorsException;
import exceptions.ReadOutOfFileException;
import exceptions.SeekOutOfFileException;
import io.IOSystem;
import oft.OpenFileTable;
import util.Config;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FileSystem {

    private static final Logger LOGGER = Logger.getLogger(FileSystem.class.getName());

    private final OpenFileTable oft;
    private final IOSystem ioSystem;
    private final FileDescriptorsArray descriptors;

    public FileSystem() {
        ioSystem = new IOSystem();
        descriptors = new FileDescriptorsArray(ioSystem);
        oft = new OpenFileTable(descriptors, ioSystem);

        BitMap bitMap = new BitMap(Config.BLOCKS);
        bitMap.setOccupied(0); // bitmap itself
        ioSystem.writeBlock(0, bitMap.asBlock());
    }

    public void create(String name) {
        LOGGER.log(Level.INFO, String.format("Create: name=%s", name));

        int freeDescriptorIndex;
        try {
            freeDescriptorIndex = descriptors.findFreeDescriptorIndex();
        } catch (NoFreeDescriptorsException e) {
            LOGGER.log(Level.WARNING, "No free descriptors left");
            return;
        }

        int freeDirectoryEntry = findFreeDirectoryEntry();
        try {
            descriptors.addDescriptor(freeDescriptorIndex);
        } catch (DiskIsFullException e) {
            LOGGER.log(Level.WARNING, "Disk is full");
            return;
        }
        writeDirectoryEntry(freeDirectoryEntry, name, freeDescriptorIndex);
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

        for (int i = 0; i < count; i++) {
            try {
                byte b = oft.readByte(index);
                LOGGER.log(Level.INFO, "Read byte: %s", b);
            } catch (ReadOutOfFileException e) {
                LOGGER.log(Level.WARNING, "Nothing to read");
            }
        }
    }

    public void write(int index, byte data, int count) {
        LOGGER.log(Level.INFO, String.format("Write: index=%d, data=%c, count=%d", index, data, count));

        for (int i = 0; i < count; i++) {
            try {
                oft.writeByte(index, data);
            } catch (DiskIsFullException e) {
                LOGGER.log(Level.WARNING, String.format("Disk is full, written only %d of total %d bytes", i, count));
                return;
            }
        }
    }

    public void seek(int index, int position) {
        LOGGER.log(Level.INFO, String.format("Seek: index=%d, position=%d", index, position));

        try {
            oft.seek(index, position);
        } catch (SeekOutOfFileException e) {
            LOGGER.log(Level.WARNING, "Seek position is out of bounds");
        }
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

    private int findFreeDirectoryEntry() {
        // todo
        return 0;
    }

    private void writeDirectoryEntry(int freeDirectoryEntry, String name, int freeDescriptorIndex) {
        // todo
    }
}
