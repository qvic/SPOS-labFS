package oft;

import exceptions.*;
import fs.BitMap;
import fs.FileDescriptorsArray;
import io.IOSystem;
import org.junit.jupiter.api.Test;
import util.Config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OpenFileTableEntryTest {

    public static final String TEST_STRING1 = "Hello world, it's a test message! Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi pulvinar vitae dolor luctus volutpat.";
    public static final String TEST_STRING2 = "Praesent finibus tempor sapien, sit amet facilisis leo nu.";

    @Test
    void seekBuffer() throws DiskIsFullException, SeekOutOfFileException, NoFreeDescriptorsException, ReadOutOfFileException, DescriptorIsFullException {
        IOSystem ioSystem = new IOSystem();

        BitMap bitMap = new BitMap(Config.BLOCKS);
        bitMap.setOccupied(0); // bitmap itself
        ioSystem.writeBlock(0, bitMap.asBlock());

        FileDescriptorsArray descriptorsArray = new FileDescriptorsArray(ioSystem);

        int freeDescriptorIndex = descriptorsArray.findFreeDescriptorIndex();
        descriptorsArray.addDescriptor(freeDescriptorIndex);

        OpenFileTableEntry entry = new OpenFileTableEntry(descriptorsArray, ioSystem, freeDescriptorIndex);

        // writing to file
        byte[] bytes = TEST_STRING1.getBytes();
        for (byte aByte : bytes) {
            entry.writeToBuffer(aByte);
        }

        // reading all file
        entry.seekBuffer(0);
        StringBuilder result = new StringBuilder();
        while (true) {
            try {
                result.append((char) entry.readBuffer());
            } catch (ReadOutOfFileException e) {
                break;
            }
        }
        assertEquals(TEST_STRING1, result.toString());

        // reading at position
        entry.seekBuffer(70);
        assertEquals('t', (char) entry.readBuffer());

        entry.seekBuffer(2);
        assertEquals('l', (char) entry.readBuffer());

        // writing at position
        entry.seekBuffer(6);
        entry.writeToBuffer((byte) 'm');

        entry.seekBuffer(6);
        assertEquals('m', entry.readBuffer());
        String newTestString = TEST_STRING1.replaceFirst("w", "m");

        // close and reopen
        entry.closeEntry();
        entry = new OpenFileTableEntry(descriptorsArray, ioSystem, freeDescriptorIndex);

        entry.seekBuffer(0);
        result = new StringBuilder();
        while (true) {
            try {
                result.append((char) entry.readBuffer());
            } catch (ReadOutOfFileException e) {
                break;
            }
        }
        assertEquals(newTestString, result.toString());

        // appending more data
        byte[] bytes2 = TEST_STRING2.getBytes();
        entry.seekBuffer(entry.getFileLength());
        for (int i = 0; i < bytes2.length; i++) {
            entry.writeToBuffer(bytes2[i]);
        }

        // trying to add more than capacity
        OpenFileTableEntry finalEntry = entry;
        assertThrows(DescriptorIsFullException.class, () -> finalEntry.writeToBuffer((byte) 'a'));

        entry.seekBuffer(0);
        result = new StringBuilder();
        while (true) {
            try {
                result.append((char) entry.readBuffer());
            } catch (ReadOutOfFileException e) {
                break;
            }
        }
        assertEquals(newTestString + TEST_STRING2, result.toString());
    }

    @Test
    void writeToBuffer() {
    }

    @Test
    void readBuffer() {
    }
}