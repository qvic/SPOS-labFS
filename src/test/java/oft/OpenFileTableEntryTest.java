package oft;

import exceptions.DiskIsFullException;
import exceptions.ReadOutOfFileException;
import exceptions.SeekOutOfFileException;
import fs.BitMap;
import fs.FileDescriptor;
import fs.FileDescriptorsArray;
import io.IOSystem;
import io.LogicalBlock;
import org.junit.jupiter.api.Test;
import util.Config;

import static org.junit.jupiter.api.Assertions.*;

class OpenFileTableEntryTest {

    @Test
    void seekBuffer() throws DiskIsFullException, SeekOutOfFileException, ReadOutOfFileException {
        IOSystem ioSystem = new IOSystem();

        BitMap bitMap = new BitMap(Config.BLOCKS);
        bitMap.setOccupied(0); // bitmap itself
        ioSystem.writeBlock(0, bitMap.asBlock());

        FileDescriptorsArray descriptorsArray = new FileDescriptorsArray(ioSystem);

        descriptorsArray.addDescriptor(0);
        OpenFileTableEntry entry = new OpenFileTableEntry(descriptorsArray, ioSystem, 0);

        byte[] bytes = "Hello world, it's a test message! 123456789012345678912345678901234567890".getBytes();
        for (int i = 0; i < bytes.length; i++) {
            entry.writeToBuffer(bytes[i]);
        }

        entry.seekBuffer(0);
        for (int i = 0; i < 11; i++) {
            System.out.print((char) entry.readBuffer());
        }
        System.out.println();

        entry.seekBuffer(70);
        System.out.println((char) entry.readBuffer());

        entry.seekBuffer(2);
        System.out.println((char) entry.readBuffer());
    }

    @Test
    void writeToBuffer() {
    }

    @Test
    void readBuffer() {
    }
}