package fs;

import exceptions.*;
import io.IOSystem;
import io.LogicalBlock;
import oft.OpenFileTable;
import oft.OpenFileTableEntry;
import util.Config;
import util.Util;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileSystem {

    private static final Logger LOGGER = Logger.getLogger(FileSystem.class.getName());

    private final OpenFileTable oft;
    private final IOSystem ioSystem;
    private final FileDescriptorsArray descriptors;

    public FileSystem() {
        ioSystem = new IOSystem();

        BitMap bitMap = BitMap.fromBlock(Config.BLOCKS, ioSystem.readBlock(0));
        bitMap.setOccupied(0); // bitmap itself
        ioSystem.writeBlock(0, bitMap.asBlock());

        descriptors = new FileDescriptorsArray(ioSystem);
        oft = new OpenFileTable(descriptors, ioSystem);

        int directoryDescriptorIndex = 0;
        try {
            descriptors.addDescriptor(directoryDescriptorIndex);
        } catch (FullDiskException e) {
            throw new IllegalStateException("Disk is full on start");
        }
        try {
            oft.addEntry(directoryDescriptorIndex);
        } catch (NoFreeOpenFileEntriesException e) {
            throw new IllegalStateException("No free OFT entries on start");
        } catch (FileAlreadyOpenedException e) {
            throw new IllegalStateException("Directory is already open");
        }
    }

    public void create(String name) {
        LOGGER.log(Level.INFO, String.format("Create: name=%s", name));
        try {
            findDirectoryEntryByName(name);
            LOGGER.log(Level.WARNING, "File already exists");
        } catch (NoSuchDirectoryEntryException e) {
            createIfNotExist(name);
        }


    }

    public void destroy(String name) {
        LOGGER.log(Level.INFO, String.format("Destroy: name=%s", name));

        int directoryEntry = 0;
        try {
            directoryEntry = findDirectoryEntryByName(name);
        } catch (NoSuchDirectoryEntryException e) {
            LOGGER.log(Level.WARNING, String.format("Directory %s was not found", name));
            return;
        }

        byte[] bytes = new byte[4];
        OpenFileTableEntry directory = oft.getEntry(0);
        try {
            directory.seekBuffer(4 * 2 * directoryEntry);
            directory.writeToBuffer((byte) 0);
        } catch (SeekOutOfFileException | FullDiskException | FullDescriptorException e) {
            throw new IllegalStateException("Can't read directory");
        }
        try {
            directory.seekBuffer(4 * 2 * directoryEntry + 4);
            for (int i = 0; i < 4; i++) {
                bytes[i] = directory.readBuffer();

            }
        } catch (ReadOutOfFileException | SeekOutOfFileException e) {
            throw new IllegalStateException("Can't read directory");
        }

        int descriptorIndex = Util.getIntByBytes(bytes[0], bytes[1], bytes[2], bytes[3]);
        try {
            directory.seekBuffer(4 * 2 * directoryEntry + 4);

            byte[] zero = Util.getBytesByInt(0);
            for (int i = 0; i < 4; i++) {
                directory.writeToBuffer(zero[i]);
            }
        } catch (SeekOutOfFileException | FullDiskException | FullDescriptorException e) {
            throw new IllegalStateException("Can't read directory");
        }

        descriptors.removeDescriptor(descriptorIndex);

    }

    public void open(String name) {
        LOGGER.log(Level.INFO, String.format("Open: name=%s", name));

        int fileDescriptor = 0;
        try {
            fileDescriptor = findFileDescriptor(name);
        } catch (NoSuchDescriptorException e) {
            LOGGER.log(Level.WARNING, String.format("Descriptor for file %s was not found", name));
            return;
        }

        int i = 0;
        try {
            i = oft.addEntry(fileDescriptor);
        } catch (NoFreeOpenFileEntriesException e) {
            LOGGER.log(Level.WARNING, String.format("Free OFT entry for file %s was not found", name));
            return;
        } catch (FileAlreadyOpenedException e) {
            LOGGER.log(Level.WARNING, String.format("File %s is already open", name));
            return;
        }
        LOGGER.log(Level.INFO, String.format("OFT index=%d", i));
    }

    public void close(int index) {
        LOGGER.log(Level.INFO, String.format("Close: index=%d", index));

        try {
            oft.removeEntry(index);
        } catch (IndexOutOfBoundsException e) {
            LOGGER.log(Level.WARNING, String.format("OFT entry for index %d was not found", index));
        }
    }

    public void read(int index, int count) {
        LOGGER.log(Level.INFO, String.format("Read: index=%d, count=%d", index, count));

        OpenFileTableEntry entry;
        try {
            entry = oft.getEntry(index);
        } catch (IndexOutOfBoundsException e) {
            LOGGER.log(Level.WARNING, String.format("OFT entry for index %d was not found", index));
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            try {
                byte b = entry.readBuffer();
                builder.append((char) b);
            } catch (ReadOutOfFileException e) {
                LOGGER.log(Level.WARNING, String.format("Nothing to read, read only %d of total %d bytes", i, count));
                break;
            }
        }
        LOGGER.log(Level.INFO, String.format("Read string: %s", builder.toString()));
    }

    public void write(int index, byte data, int count) {
        LOGGER.log(Level.INFO, String.format("Write: index=%d, data=%c, count=%d", index, data, count));

        OpenFileTableEntry entry;
        try {
            entry = oft.getEntry(index);
        } catch (IndexOutOfBoundsException e) {
            LOGGER.log(Level.WARNING, String.format("OFT entry for index %d was not found", index));
            return;
        }

        for (int i = 0; i < count; i++) {
            try {
                entry.writeToBuffer(data);
            } catch (FullDiskException e) {
                LOGGER.log(Level.WARNING, String.format("Disk is full, written only %d of total %d bytes", i, count));
                return;
            } catch (FullDescriptorException e) {
                LOGGER.log(Level.WARNING, "Descriptor is full, can't add more blocks");
                return;
            }
        }
    }

    public void seek(int index, int position) {
        LOGGER.log(Level.INFO, String.format("Seek: index=%d, position=%d", index, position));

        OpenFileTableEntry entry;
        try {
            entry = oft.getEntry(index);
        } catch (IndexOutOfBoundsException e) {
            LOGGER.log(Level.WARNING, String.format("OFT entry for index %d was not found", index));
            return;
        }

        try {
            entry.seekBuffer(position);
        } catch (SeekOutOfFileException e) {
            LOGGER.log(Level.WARNING, "Seek position is out of bounds");
        }
    }

    public void directory() {
        OpenFileTableEntry directory = oft.getEntry(0);
        int numberOfEntries = directory.getFileLength() / (4 * 2);
        String result = "";
        for (int i = 0; i < numberOfEntries; i++) {
            try {
                directory.seekBuffer(i * 4 * 2);
            } catch (SeekOutOfFileException e) {
                LOGGER.log(Level.WARNING, "Seek position is out of bounds");
                return;
            }
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < 4; j++) {
                try {
                    builder.append((char) directory.readBuffer());
                } catch (ReadOutOfFileException e) {
                    e.printStackTrace();
                    return;
                }
            }
            if (builder.toString().getBytes()[0] == 0) continue;
            result += builder.toString().trim() + " ";
            byte[] intBytes = new byte[4];
            for (int j = 0; j < 4; j++) {
                try {
                    intBytes[j] = directory.readBuffer();
                } catch (ReadOutOfFileException e) {
                    e.printStackTrace();
                }
            }
            int index = Util.getIntByBytes(intBytes[0], intBytes[1], intBytes[2], intBytes[3]);
            result += "(size=" + descriptors.getDescriptor(index).getFileLength() + ")";
            if (i < numberOfEntries - 1) result += ", ";
        }
        LOGGER.log(Level.INFO, "Directory: " + result);
    }

    public void input(String name) {
        LOGGER.log(Level.INFO, String.format("Input: name=%s", name));

        oft.closeAll();
        ioSystem.updateFromFile(name);

        try {
            oft.addEntry(0); // directory
        } catch (NoFreeOpenFileEntriesException e) {
            throw new IllegalStateException("No free entries after closeAll");
        } catch (FileAlreadyOpenedException e) {
            throw new IllegalStateException("Directory is open after closeAll");
        }
    }

    public void save(String name) {
        LOGGER.log(Level.INFO, String.format("Save: name=%s", name));

        oft.closeAll();
        ioSystem.dumpToFile(name);

        try {
            oft.addEntry(0);
        } catch (NoFreeOpenFileEntriesException e) {
            throw new IllegalStateException("No free entries on closeAll");
        } catch (FileAlreadyOpenedException e) {
            throw new IllegalStateException("Directory is open after closeAll");
        }
    }

    private int findFileDescriptor(String name) throws NoSuchDescriptorException {
        if (name.length() > 4) throw new IllegalArgumentException("Name is too long");

        OpenFileTableEntry directory = oft.getEntry(0);

        int numberOfEntries = directory.getFileLength() / (4 * 2);
        for (int i = 0; i < numberOfEntries; i++) {
            try {
                directory.seekBuffer(i * 4 * 2);
            } catch (SeekOutOfFileException e) {
                throw new IllegalStateException();
            }
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < 4; j++) {
                try {
                    builder.append((char) directory.readBuffer());
                } catch (ReadOutOfFileException e) {
                    throw new IllegalStateException();
                }
            }
            if (builder.toString().trim().equals(name)) {
                byte[] intBytes = new byte[4];
                for (int j = 0; j < 4; j++) {
                    try {
                        intBytes[j] = directory.readBuffer();
                    } catch (ReadOutOfFileException e) {
                        throw new IllegalStateException();
                    }
                }
                return Util.getIntByBytes(intBytes[0], intBytes[1], intBytes[2], intBytes[3]);
            }
        }
        throw new NoSuchDescriptorException();
    }

    private void createIfNotExist(String name) {
        int freeDescriptorIndex;
        try {
            freeDescriptorIndex = descriptors.findFreeDescriptorIndex();
        } catch (NoFreeDescriptorsException e) {
            LOGGER.log(Level.WARNING, "No free descriptors left");
            return;
        }
        try {
            descriptors.addDescriptor(freeDescriptorIndex);
        } catch (FullDiskException e) {
            LOGGER.log(Level.WARNING, "Disk is full");
            return;
        }

        int freeDirectoryEntry = 0;
        try {
            freeDirectoryEntry = findFreeDirectoryEntry();
        } catch (NoFreeDirectoryEntryException e) {
            LOGGER.log(Level.WARNING, "No free directory entry left");
            return;
        }
        try {
            writeDirectoryEntry(freeDirectoryEntry, name, freeDescriptorIndex);
        } catch (FullDiskException e) {
            LOGGER.log(Level.WARNING, "Disk is full");
        } catch (FullDescriptorException e) {
            LOGGER.log(Level.WARNING, "No free descriptors left");
        }

    }

    private int findDirectoryEntryByName(String name) throws NoSuchDirectoryEntryException {
        OpenFileTableEntry directory = oft.getEntry(0);
        if (name.length() > 4) throw new IllegalArgumentException("Name is too long");
        for (int i = 0; i < directory.getFileLength() / 8; i++) {
            try {
                directory.seekBuffer(i * 2 * 4);
            } catch (SeekOutOfFileException e) {
                throw new IllegalStateException("Can't seek to position in file");
            }
            byte[] bytes = new byte[4];
            for (int j = 0; j < 4; j++) {
                try {
                    bytes[j] = directory.readBuffer();
                } catch (ReadOutOfFileException e) {
                    throw new IllegalStateException("Can't read from position in file");
                }
            }

            if (bytes[0] != 0 && new String(bytes).trim().equals(name)) return i;
        }
        throw new NoSuchDirectoryEntryException();

    }

    private int findFreeDirectoryEntry() throws NoFreeDirectoryEntryException {
        OpenFileTableEntry directory = oft.getEntry(0);
        if (directory.getFileLength() < Config.BLOCK_INDICES_IN_DESCRIPTOR * Config.BLOCK_SIZE)
            return directory.getFileLength() / 8;
        for (int i = 0; i < directory.getFileLength() / 8; i++) {
            try {
                directory.seekBuffer(i * 2 * 4);
            } catch (SeekOutOfFileException e) {
                LOGGER.log(Level.WARNING, "Seek position is out of bounds");
            }

            try {
                if (directory.readBuffer() == 0) return i;
            } catch (ReadOutOfFileException e) {
                LOGGER.log(Level.WARNING, "Free directory entry reading fails");
            }

        }

        throw new NoFreeDirectoryEntryException();
    }

    private void writeDirectoryEntry(int freeDirectoryEntry, String name, int freeDescriptorIndex) throws FullDiskException, FullDescriptorException {
        if (name.length() > 4) throw new IllegalArgumentException("Name is too long");

        OpenFileTableEntry directory = oft.getEntry(0);

        try {
            directory.seekBuffer(4 * 2 * freeDirectoryEntry);
        } catch (SeekOutOfFileException e) {
            throw new IllegalArgumentException("Passed directory entry index can't be written");
        }
        byte[] nameBytes = name.getBytes();
        for (byte nameByte : nameBytes) {
            directory.writeToBuffer(nameByte);
        }
        for (int j = nameBytes.length; j < 4; j++) {
            directory.writeToBuffer((byte) ' ');
        }
        byte[] descriptorBytes = Util.getBytesByInt(freeDescriptorIndex);
        for (byte descriptorByte : descriptorBytes) {
            directory.writeToBuffer(descriptorByte);
        }
    }
}
