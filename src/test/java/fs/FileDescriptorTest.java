package fs;

import io.LogicalBlock;
import org.junit.jupiter.api.Test;
import util.Config;

import static org.junit.jupiter.api.Assertions.*;

class FileDescriptorTest {

    @Test
    void asInts() {
        FileDescriptor descriptor = new FileDescriptor(40);

        descriptor.add(10);
        assertThrows(IllegalArgumentException.class, () -> descriptor.setFileLength(100));

        descriptor.add(11);
        descriptor.setFileLength(100);

        int[] ints = descriptor.asInts();

        assertEquals(ints.length, 3);
        assertEquals(ints[0], 100);
        assertEquals(ints[1], 10);
        assertEquals(ints[2], 11);
    }

    @Test
    void fromBlock() {
        LogicalBlock block = new LogicalBlock();
        for (int i = 0; i < Config.DESCRIPTORS_IN_BLOCK; i++) {
            block.setInt(i * (1 + Config.BLOCK_INDICES_IN_DESCRIPTOR), -1);
        }

        assertNull(FileDescriptor.fromBlock(block, 0));

        block.setInt(4, 63);
        block.setInt(5, 10);

        FileDescriptor descriptor1 = FileDescriptor.fromBlock(block, 1);
        assertNotNull(descriptor1);

        assertEquals(63, descriptor1.getFileLength());
        assertEquals(1, descriptor1.getBlockIndexes().size());
        assertEquals(10, descriptor1.getBlockIndexes().get(0));

        block.setInt(8, 191);
        block.setInt(9, 10);
        block.setInt(10, 11);
        block.setInt(11, 12);

        FileDescriptor descriptor2 = FileDescriptor.fromBlock(block, 2);
        assertNotNull(descriptor2);

        assertEquals(191, descriptor2.getFileLength());
        assertEquals(3, descriptor2.getBlockIndexes().size());
        assertEquals(10, descriptor2.getBlockIndexes().get(0));
        assertEquals(11, descriptor2.getBlockIndexes().get(1));
        assertEquals(12, descriptor2.getBlockIndexes().get(2));
    }
}