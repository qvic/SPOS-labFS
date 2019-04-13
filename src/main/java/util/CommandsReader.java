package util;

import fs.FileSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CommandsReader {

    private FileSystem fs;
    private Scanner scanner;

    public CommandsReader(FileSystem fs) throws FileNotFoundException {
        this.fs = fs;
        if (Config.READ_COMMANDS_FROM_FILE) {
            File commands = new File(Config.COMMANDS_FILE_PATH);
            scanner = new Scanner(commands);
        } else {
            scanner = new Scanner(System.in);
        }
    }

    public void commandsExecution() {
        while (scanner.hasNext()) {
            String command = scanner.next();
            switch (command) {
                case "cr":
                    if (scanner.hasNext()) {
                        String name = scanner.next();
                        fs.create(name);
                    }
                    break;
                case "de":
                    if (scanner.hasNext()) {
                        String name = scanner.next();
                        fs.destroy(name);
                    }
                    break;
                case "op":
                    if (scanner.hasNext()) {
                        String name = scanner.next();
                        fs.open(name);
                    }
                    break;
                case "cl":
                    if (scanner.hasNextInt()) {
                        int index = scanner.nextInt();
                        fs.close(index);
                    }
                    break;
                case "rd":
                    if (scanner.hasNextInt()) {
                        int index = scanner.nextInt();
                        if (scanner.hasNextInt()) {
                            int count = scanner.nextInt();
                            fs.read(index, count);
                        }
                    }
                    break;
                case "wr":
                    if (scanner.hasNextInt()) {
                        int index = scanner.nextInt();
                        if (scanner.hasNext(".")) {
                            char data = scanner.next().charAt(0);
                            if (scanner.hasNextInt()) {
                                int count = scanner.nextInt();
                                fs.write(index, (byte) data, count);
                            }
                        }
                    }
                    break;
                case "sk":
                    if (scanner.hasNextInt()) {
                        int index = scanner.nextInt();
                        if (scanner.hasNextInt()) {
                            int pos = scanner.nextInt();
                            fs.seek(index, pos);
                        }
                    }
                    break;
                case "dr":
                    fs.directory();
                    break;
                case "sv":
                    if (scanner.hasNext()) {
                        String name = scanner.next();
                        fs.save(name);
                    }
                    break;
                case "in":
                    if (scanner.hasNext()) {
                        String name = scanner.next();
                        fs.input(name);
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + command);
            }
        }
        scanner.close();
    }
}
