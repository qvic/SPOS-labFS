package oft;

import fs.FileDescriptorsArray;
import io.IOSystem;

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

    public int addEntry(int descriptorIndex) {
        table.add(new OpenFileTableEntry(descriptors, ioSystem, descriptorIndex));
        return table.size() - 1;
    }

    public OpenFileTableEntry getEntry(int oftIndex) {
        return table.get(oftIndex);
    }
}
