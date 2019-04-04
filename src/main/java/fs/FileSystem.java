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
            throw new RuntimeException("Disk is full on start");
        }
        int directoryOftIndex = oft.addEntry(directoryDescriptorIndex);
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
        } catch (SeekOutOfFileException e) {
            e.printStackTrace();
        } catch (FullDiskException e) {
            e.printStackTrace();
        } catch (FullDescriptorException e) {
            e.printStackTrace();
        }
    }

    public void destroy(String name) {
        LOGGER.log(Level.INFO, String.format("Destroy: name=%s", name));
        int directoryEntry = findDirectoryEntryByName(name);
        byte[] bytes=new byte[4];
        OpenFileTableEntry directory = oft.getEntry(0);
        try {
            directory.seekBuffer(4*2*directoryEntry+4);
        } catch (SeekOutOfFileException e) {
            e.printStackTrace();
        }
        for (int i = 0; i <4; i++) {
            try {
                bytes[i]=directory.readBuffer();
            } catch (ReadOutOfFileException e) {
                e.printStackTrace();
            }
        }
        int descriptorIndex = Util.getIntByBytes(bytes[0],bytes[1],bytes[2],bytes[3]);
        try {
            directory.seekBuffer(4*2*directoryEntry+4);
        } catch (SeekOutOfFileException e) {
            e.printStackTrace();
        }
        byte[] zero=Util.getBytesByInt(0);
        for (int i = 0; i <4; i++) {
            try {
                directory.writeToBuffer(zero[i]);
            } catch (FullDiskException e) {
                e.printStackTrace();
            } catch (FullDescriptorException e) {
                e.printStackTrace();
            }
        }
        descriptors.removeDescriptor(descriptorIndex);

    }

    public void open(String name) {
        LOGGER.log(Level.INFO, String.format("Open: name=%s", name));

        int fileDescriptor = 0;
        try {
            fileDescriptor = findFileDescriptor(name);
        } catch (SeekOutOfFileException e) {
            e.printStackTrace();
        } catch (ReadOutOfFileException e) {
            e.printStackTrace();
        }
        int i = oft.addEntry(fileDescriptor);
        LOGGER.log(Level.INFO, String.format("OFT index: %d", i));
    }

    public void close(int index) {
        LOGGER.log(Level.INFO, String.format("Close: index=%d", index));
    }

    public void read(int index, int count) {
        LOGGER.log(Level.INFO, String.format("Read: index=%d, count=%d", index, count));

        for (int i = 0; i < count; i++) {
            try {
                byte b = oft.getEntry(index).readBuffer();
                LOGGER.log(Level.INFO, String.format("Read byte: %s", (char)b));
            } catch (ReadOutOfFileException e) {
                LOGGER.log(Level.WARNING, "Nothing to read");
            }
        }
    }

    public void write(int index, byte data, int count) {
        LOGGER.log(Level.INFO, String.format("Write: index=%d, data=%c, count=%d", index, data, count));

        for (int i = 0; i < count; i++) {
            try {
                oft.getEntry(index).writeToBuffer(data);
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

        try {
            oft.getEntry(index).seekBuffer(position);
        } catch (SeekOutOfFileException e) {
            LOGGER.log(Level.WARNING, "Seek position is out of bounds");
        }
    }

    public void directory() {
        LOGGER.log(Level.INFO, "Directory");
        OpenFileTableEntry directory = oft.getEntry(0);
        int numberOfEntries = directory.getFileLength() / (4 * 2);
        String result="";
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
            result+=builder.toString().trim()+" ";
                byte[] intBytes = new byte[4];
                for (int j = 0; j < 4; j++) {
                    try {
                        intBytes[j] = directory.readBuffer();
                    } catch (ReadOutOfFileException e) {
                        e.printStackTrace();
                    }
                }
              int index= Util.getIntByBytes(intBytes[0], intBytes[1], intBytes[2], intBytes[3]);
                result+=descriptors.getDescriptor(index).getFileLength()+" ";
        }
        LOGGER.log(Level.INFO,result);


    }

    public void input(String name) {
        LOGGER.log(Level.INFO, String.format("Input: name=%s", name));
    }

    public void save(String name) {
        LOGGER.log(Level.INFO, String.format("Save: name=%s", name));
    }

    private int findFileDescriptor(String name) throws SeekOutOfFileException, ReadOutOfFileException {
        if (name.length() > 4) throw new IllegalArgumentException("Name is too long");

        OpenFileTableEntry directory = oft.getEntry(0);

        int numberOfEntries = directory.getFileLength() / (4 * 2);
        for (int i = 0; i < numberOfEntries; i++) {
            directory.seekBuffer(i * 4 * 2);
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < 4; j++) {
                builder.append((char) directory.readBuffer());
            }
            if (builder.toString().trim().equals(name)) {
                byte[] intBytes = new byte[4];
                for (int j = 0; j < 4; j++) {
                    intBytes[j] = directory.readBuffer();
                }
                return Util.getIntByBytes(intBytes[0], intBytes[1], intBytes[2], intBytes[3]);
            }
        }
        return 0;
    }

    private int findDirectoryEntryByName(String name) {
        OpenFileTableEntry directory = oft.getEntry(0);
        if (name.length() > 4) throw new IllegalArgumentException("Name is too long");
        for (int i = 0; i < directory.getFileLength() / 8; i++) {
            try {
                directory.seekBuffer(i * 2 *4 );
            } catch (SeekOutOfFileException e) {
                LOGGER.log(Level.WARNING, "Seek position is out of bounds");
            }
            byte[] bytes = new byte[4];
            for (int j = 0; j < 4; j++) {
                try {
                    bytes[j] = directory.readBuffer();
                } catch (ReadOutOfFileException e) {
                    LOGGER.log(Level.WARNING, "");
                }
            }

            if (Arrays.equals(bytes,name.getBytes())) return i;
        }
        throw new IllegalArgumentException("File with such name does not exist");
    }

    private int findFreeDirectoryEntry() throws NoFreeDirectoryEntryException {
        OpenFileTableEntry directory = oft.getEntry(0);
        if (directory.getFileLength() < Config.BLOCK_INDICES_IN_DESCRIPTOR * Config.BLOCK_SIZE)
            return directory.getFileLength() / 8;
        for (int i = 0; i < directory.getFileLength() / 8; i++) {
            try {
                directory.seekBuffer(i * 2 *4 + 4);
            } catch (SeekOutOfFileException e) {
                LOGGER.log(Level.WARNING, "Seek position is out of bounds");
            }
            byte[] bytes = new byte[LogicalBlock.BYTES_IN_INT];
            for (int j = 0; j < LogicalBlock.BYTES_IN_INT; j++) {
                try {
                    bytes[j] = directory.readBuffer();
                } catch (ReadOutOfFileException e) {
                    LOGGER.log(Level.WARNING, "Free directory entry reading fails");
                }
            }
            int result = Util.getIntByBytes(bytes[0], bytes[1], bytes[2], bytes[3]);
            if (result == 0) return i;
        }

        throw new NoFreeDirectoryEntryException();
    }

    private void writeDirectoryEntry(int freeDirectoryEntry, String name, int freeDescriptorIndex) throws SeekOutOfFileException, FullDiskException, FullDescriptorException {
        if (name.length() > 4) throw new IllegalArgumentException("Name is too long");

        OpenFileTableEntry directory = oft.getEntry(0);

        directory.seekBuffer(4 * 2 * freeDirectoryEntry);
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
