import java.io.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class to write and update load information from Load.info file.
 */
public class LoadInfoWriter {

    /**
     * The file to write information to.
     */
    protected File file;

    /**
     * The lock used to synchronize access to resources.
     */
    public ReentrantLock lock;

    /**
     * Constructor for the LoadInfoWriter class.
     * Initializes the file and writes default values directly to it.
     * @param fileName The name of the file to write load information to.
     * @param loadInfoLock The lock to ensure safe writing and updating of load information.
     */
    public LoadInfoWriter(String fileName, ReentrantLock loadInfoLock) {
        this.file = new File(fileName);
        this.lock = loadInfoLock;

        try {
            FileWriter writer = new FileWriter(file);
            writer.write("8888: 0\n");
            writer.write("8000: 0\n");
            writer.write("8100: 0\n");
            writer.close();
            System.out.println("Values written successfully.");
        } catch (IOException e) {
            System.err.println("Error writing to the file: " + e.getMessage());
        }
    }

    /**
     * Method to add load to a specific port and update the file accordingly.
     * @param port The port to which load info will be updated.
     */
    public void AddLoad(int port){
        lock.lock();
        BufferedReader reader = null;
        BufferedWriter writer = null;
        boolean found = false;

        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            StringBuilder newContent = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                int key = Integer.parseInt(parts[0].trim());
                int value = Integer.parseInt(parts[1].trim());

                if (key == port) {
                    value++;
                    newContent.append(key).append(": ").append(value).append("\n");
                    found = true;
                } else {
                    newContent.append(line).append("\n");
                }
            }

            if (found) {
                writer = new BufferedWriter(new FileWriter(file));
                writer.write(newContent.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
                if (writer != null) writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        lock.unlock();
    }

    /**
     * Method to remove load from a specific port and update the file accordingly.
     * @param port The port from which load info will be updated.
     */
    public void RemoveLoad(int port){
        lock.lock();
        BufferedReader reader = null;
        BufferedWriter writer = null;
        boolean found = false;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            StringBuilder newContent = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                int key = Integer.parseInt(parts[0].trim());
                int value = Integer.parseInt(parts[1].trim());

                if (key == port) {
                    value--;
                    newContent.append(key).append(": ").append(value).append("\n");
                    found = true;
                } else {
                    newContent.append(line).append("\n");
                }
            }
            if (found) {
                writer = new BufferedWriter(new FileWriter(file));
                writer.write(newContent.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
                if (writer != null) writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        lock.unlock();
    }
}
