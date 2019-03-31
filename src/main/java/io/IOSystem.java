package io;

import util.Config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Logger;
import java.util.logging.Level;


public class IOSystem {
    private static final Logger logger=Logger.getLogger(IOSystem.class.getName());
    private LogicalBlock[] blocks;

    public IOSystem() {
        int numberOfBlocks = Integer.parseInt(Config.INSTANCE.getProperty("blocks"));
        blocks = new LogicalBlock[numberOfBlocks];

        for (int i = 0; i < numberOfBlocks; i++) {
            blocks[i] = new LogicalBlock();
        }
    }

    public void updateFromFile(String filename)  {
        logger.log(Level.INFO,String.format("Update from file:%s",filename));
        RandomAccessFile disk = null;
        try {
            disk = new RandomAccessFile(filename,"rw");
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING,"File not found");
        }
        try{
            for(int i=0;i<blocks.length;i++){
                byte[] bytes=new byte[Integer.parseInt(Config.INSTANCE.getProperty("blockSize"))];
                disk.read(bytes);
                writeBlock(i,new LogicalBlock(bytes));
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void dumpToFile(String filename) {
        logger.log(Level.INFO,String.format("Dump to file:%s",filename));
        RandomAccessFile disk = null;
        try {
            disk = new RandomAccessFile(filename, "rw");
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING,"File not found");
        }
        try {
            for (int i = 0; i < blocks.length; i++) {
                disk.write(this.readBlock(i).getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public LogicalBlock readBlock(int index) {
        return blocks[index].getCopy();
    }

    public void writeBlock(int index, LogicalBlock block) {
        blocks[index] = block.getCopy();
    }
}
