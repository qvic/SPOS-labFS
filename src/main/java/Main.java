import fs.FileSystem;
import shell.Shell;
import util.CommandsReader;
import util.Config;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) {
        FileSystem fs = new FileSystem();
        Shell shell=new Shell(fs);
        try {
            CommandsReader reader=new CommandsReader(shell);
            reader.commandsExecution();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
