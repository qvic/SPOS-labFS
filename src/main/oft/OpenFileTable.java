package main.oft;

import main.io.IOSystem;

import java.util.ArrayList;
import java.util.List;

public class OpenFileTable {

    private List<OpenFileTableEntry> table;
    private IOSystem ioSystem;

    public OpenFileTable(IOSystem ioSystem) {
        this.ioSystem = ioSystem;
        this.table = new ArrayList<>();

        // directory
        table.add(0, new OpenFileTableEntry(0, ioSystem));
    }

    public void add(String name) {
        /*
        search directory to find index of file descriptor (i)
        allocate a free OpenFileTable entry (reuse deleted entries)
        fill in current position (0) and file descriptor index (i)
        read block 0 of file into the r/w buffer (read-ahead)
        return OpenFileTable index (j) (or return error)
        consider adding a file length field (to simplify checking)
        */

        byte[] bytes = name.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            table.get(0).writeToBuffer(bytes[i]);
        }
    }
}
