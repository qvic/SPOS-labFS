package shell;

import fs.FileSystem;
import fs.File;
import java.util.ArrayList;

public class Shell {
    FileSystem fs;

    public Shell(FileSystem fs) {
        this.fs = fs;
    }

    public void close(int index) {
        if (fs.close(index))
            System.out.println("ok");
        else
            System.out.println("error");
    }

    public void create(String name) {
        if (fs.create(name))
            System.out.println("ok");
        else
            System.out.println("error");
    }

    public void destroy(String name) {
        if (fs.destroy(name))
            System.out.println("ok");
        else
            System.out.println("error");
    }

    public void directory() {
        ArrayList<File> fileList = fs.directory();
        if (fileList!=null)
            System.out.println("Directory " + fileList);
        else
            System.out.println("error");


    }

    public void init(String name) {
        fs.input(name);
    }

    public void lseek(int index, int pos) {
        if (fs.seek(index, pos))
            System.out.println("ok");
        else
            System.out.println("error");

    }

    public void open(String name) {
        int index = fs.open(name);
        if (index != -1)
            System.out.println("Opening successful, index \"" + index + "\"");
        else
            System.out.println("Error occurred");
    }

    public void read(int index, int count) {
        byte[] mem_area = new byte[count];
        int bytesRead = fs.read(index, mem_area, count);
        if (bytesRead == -1) {
            System.out.println("Error occurred");
        } else {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < bytesRead; i++) {
                builder.append((char) mem_area[i]);
            }
            System.out.println("Read " + bytesRead + " bytes: " + builder);
        }
    }

    public void save(String name) {
        fs.save(name);
    }

    public void write(int index, byte data, int count) {
        byte[] mem_area = new byte[count];
        for (int i = 0; i < count; i++) {
            mem_area[i] = data;
        }
        int bytesWritten = fs.write(index, mem_area, count);
        if (bytesWritten == count)
            System.out.println("Writing successful, written " + bytesWritten + " bytes");
        else if (bytesWritten != -1)
            System.out.println(String.format("Not enough space, written only %d of total %d bytes", bytesWritten, count));
        else
            System.out.println("Error occurred");


    }


}
