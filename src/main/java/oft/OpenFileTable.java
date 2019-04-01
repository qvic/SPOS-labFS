package oft;

import exceptions.DiskIsFullException;
import exceptions.ReadOutOfFileException;
import exceptions.SeekOutOfFileException;
import fs.FileDescriptorsArray;
import io.IOSystem;

import java.nio.ReadOnlyBufferException;
import java.util.ArrayList;
import java.util.List;

public class OpenFileTable {

    private List<OpenFileTableEntry> table;
    private FileDescriptorsArray descriptors;
    private IOSystem ioSystem;

    public OpenFileTable(FileDescriptorsArray descriptors, IOSystem ioSystem) {
        this.descriptors = descriptors;
        this.ioSystem = ioSystem;
        this.table = new ArrayList<>();
    }

    public void writeByte(int oftIndex, byte data) throws DiskIsFullException {
        OpenFileTableEntry entry = table.get(oftIndex);

        entry.writeToBuffer(data);
    }

    public byte readByte(int index) throws ReadOutOfFileException {
        OpenFileTableEntry entry = table.get(index);

        return entry.readBuffer();
    }

    public void seek(int index, int position) throws SeekOutOfFileException {
        OpenFileTableEntry entry = table.get(index);

        entry.seekBuffer(position);
    }
}
