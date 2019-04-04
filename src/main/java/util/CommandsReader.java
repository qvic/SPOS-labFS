package util;

import fs.FileSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CommandsReader {
    FileSystem fs;
    Scanner scanner;

    public CommandsReader(FileSystem fs) throws FileNotFoundException {
        this.fs=fs;
        File commands=new File(Config.COMMANDS_FILE_PATH);
        scanner=new Scanner(commands);
    }
    public void commandsExecution(){
        while(scanner.hasNext()) {
            String command = scanner.next();
            if(command.equals("cd")){
                if(scanner.hasNext()){
                    String name=scanner.next();
                    fs.create(name);
                }
            }
            if (command.equals("de")) {
                if(scanner.hasNext()){
                    String name=scanner.next();
                    fs.destroy(name);
                }
            }
            if (command.equals("op")) {
                if(scanner.hasNext()){
                    String name=scanner.next();
                    fs.open(name);
                }
            }
            if (command.equals("cl")) {
                if (scanner.hasNextInt()) {
                    int index=scanner.nextInt();
                    fs.close(index);
                }
            }
            if (command.equals("rd")) {
                if (scanner.hasNextInt()) {
                    int index = scanner.nextInt();
                    if (scanner.hasNextInt()) {
                        int count = scanner.nextInt();
                        fs.read(index,count);
                    }
                }
            }
            if (command.equals("wr")) {
                if (scanner.hasNextInt()) {
                    int index = scanner.nextInt();
                    if (scanner.hasNext(".")) {
                        char data = scanner.next().charAt(0);
                        if(scanner.hasNextInt()){
                            int count=scanner.nextInt();
                            fs.write(index,(byte)data,count);
                        }
                    }
                }
            }

            if (command.equals("sk")) {
                if (scanner.hasNextInt()) {
                    int index = scanner.nextInt();
                    if (scanner.hasNextInt()) {
                        int pos = scanner.nextInt();
                        fs.seek(index,pos);
                    }
                }
            }

            if (command.equals("dr")) {
                fs.directory();
            }
        }
       scanner.close();
    }
}
