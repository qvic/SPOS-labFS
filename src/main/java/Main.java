import fs.FileSystem;
import util.CommandsReader;
import util.Config;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) {
        FileSystem fs = new FileSystem();
        try {
            CommandsReader reader=new CommandsReader(fs);
            reader.commandsExecution();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
