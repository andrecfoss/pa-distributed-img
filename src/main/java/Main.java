// Main.java

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.locks.ReentrantLock;

// Javadoc: http://localhost:63342/pa-distributed-img/first-project/target/apidocs/package-summary.html

/**
 * Main class responsible for initiating the program.
 */
public class Main {

    /**
     * Main method that starts the execution of the program.
     *
     * @param args The command-line arguments passed to the program.
     */
    public static void main(String[] args) {
        ConfigReader configReader = new ConfigReader();
        configReader.ReadFile("Config.txt");
        ReentrantLock loadInfoLock = new ReentrantLock();
        LoadInfoWriter loadInfoWriter = new LoadInfoWriter("load.info.txt",loadInfoLock);
        LoadInfoReader loadInfoReader = new LoadInfoReader("load.info.txt",loadInfoLock);
        Server server = new Server(8888, 5,loadInfoWriter);
        server.start();
        Server server1 = new Server(8000, 5,loadInfoWriter);
        server1.start();
        Server server2 = new Server(8100, 5,loadInfoWriter);
        server2.start();
        new UserInterface(configReader,loadInfoReader,loadInfoWriter);
        new UserInterface(configReader,loadInfoReader,loadInfoWriter);
        new UserInterface(configReader,loadInfoReader,loadInfoWriter);
    }
}
