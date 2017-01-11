package app;

import config.CommandLineParser;
import config.Configuration;
import display.Display;

public class Application {
    public static void main(String[] args) throws Exception {
        CommandLineParser c = new CommandLineParser(args);
        Configuration config = c.create();

        Runnable engine = UMLEngine.getInstance(config);
        engine.run();

        Display.showWindow(config);
    }
}
