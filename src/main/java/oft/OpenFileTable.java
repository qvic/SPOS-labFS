package oft;

import exceptions.NoFreeOpenFileEntriesException;
import fs.FileDescriptorsArray;
import io.IOSystem;
import util.Config;

import java.util.ArrayList;
import java.util.List;

public class OpenFileTable {

    private List<OpenFileTableEntry> table;
    private FileDescriptorsArray descriptors;
    private IOSystem ioSystem;
    private int count;

    public OpenFileTable(FileDescriptorsArray descriptors, IOSystem ioSystem) {
        this.descriptors = descriptors;
        this.ioSystem = ioSystem;
        this.table = new ArrayList<>();
        this.count = 0;
    }

    public int addEntry(int descriptorIndex) throws NoFreeOpenFileEntriesException {
        if (count == Config.MAX_OPEN_FILE_ENTRIES) throw new NoFreeOpenFileEntriesException();

        OpenFileTableEntry entry = new OpenFileTableEntry(descriptors, ioSystem, descriptorIndex);
        count++;

        for (int i = 0; i < table.size(); i++) {
            if (table.get(i) == null) {
                table.set(i, entry);
                return i;
            }
        }

        table.add(entry);
        return table.size() - 1;
    }

    public OpenFileTableEntry getEntry(int oftIndex) {
        OpenFileTableEntry entry = table.get(oftIndex);
        if (entry == null) throw new IndexOutOfBoundsException();

        return entry;
    }

    public void removeEntry(int index) {
        OpenFileTableEntry entry = table.get(index);
        if (entry == null) throw new IndexOutOfBoundsException();

        entry.closeEntry();
        table.set(index, null);
        count--;
    }

    public void closeAll() {
        for (OpenFileTableEntry entry : table) {
            if (entry != null) {
                entry.closeEntry();
            }
        }
        count = 0;
        table.clear();
    }
}
