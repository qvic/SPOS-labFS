package io;

import util.Config;

public class IOSystem {

    private LogicalBlock[] blocks;

    public IOSystem() {
        int numberOfBlocks = Integer.parseInt(Config.INSTANCE.getProperty("blocks"));
        blocks = new LogicalBlock[numberOfBlocks];

        for (int i = 0; i < numberOfBlocks; i++) {
            blocks[i] = new LogicalBlock();
        }
    }

    public void updateFromFile(String filename) {

    }

    public void dumpToFile(String filename) {

    }

    public LogicalBlock readBlock(int index) {
        return blocks[index].getCopy();
    }

    public void writeBlock(int index, LogicalBlock block) {
        blocks[index] = block.getCopy();
    }
}
