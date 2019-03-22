package main.fs;

import java.util.ArrayList;
import java.util.List;

public class FileDescriptor {

    private int length;
    private List<Integer> blockIndexes;

    public FileDescriptor(int length) {
        this.length = length;
        this.blockIndexes = new ArrayList<>();
    }

    public void add(int... indices) {
        for (int index : indices) {
            blockIndexes.add(index);
        }
    }
}
