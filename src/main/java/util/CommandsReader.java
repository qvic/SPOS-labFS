package util;

import fs.FileSystem;
import shell.Shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CommandsReader {
    private Shell shell;
    private Scanner scanner;

    public CommandsReader(Shell shell) throws FileNotFoundException {
        this.shell = shell;
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
                        shell.create(name);
                    }
                    break;
                case "de":
                    if (scanner.hasNext()) {
                        String name = scanner.next();
                        shell.destroy(name);
                    }
                    break;
                case "op":
                    if (scanner.hasNext()) {
                        String name = scanner.next();
                        shell.open(name);
                    }
                    break;
                case "cl":
                    if (scanner.hasNextInt()) {
                        int index = scanner.nextInt();
                        shell.close(index);
                    }
                    break;
                case "rd":
                    if (scanner.hasNextInt()) {
                        int index = scanner.nextInt();
                        if (scanner.hasNextInt()) {
                            int count = scanner.nextInt();
                            shell.read(index, count);
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
                                shell.write(index, (byte) data, count);
                            }
                        }
                    }
                    break;
                case "sk":
                    if (scanner.hasNextInt()) {
                        int index = scanner.nextInt();
                        if (scanner.hasNextInt()) {
                            int pos = scanner.nextInt();
                            shell.lseek(index, pos);
                        }
                    }
                    break;
                case "dr":
                    shell.directory();
                    break;
                case "sv":
                    if (scanner.hasNext()) {
                        String name = scanner.next();
                        shell.save(name);
                    }
                    break;
                case "in":
                    if (scanner.hasNext()) {
                        String name = scanner.next();
                        shell.init(name);
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + command);
            }
        }
        scanner.close();
    }
}
