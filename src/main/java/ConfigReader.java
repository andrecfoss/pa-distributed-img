import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * The ConfigReader class implements a set of methods that read files most specific the Config file
 */
public class ConfigReader {
    private int nColumns;
    private int nRows;
    private int nServers;
    private int nClients;

    /**
     * Reads a configuration file and extracts information about the number of columns, rows, servers and clients.
     * @param fileName the name of the file to read
     */
    public void ReadFile(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("number of columns:")) {
                    nColumns = Integer.parseInt(line.split(":")[1].trim());
                } else if (line.startsWith("number of rows:")) {
                    nRows = Integer.parseInt(line.split(":")[1].trim());
                } else if (line.startsWith("number of servers:")) {
                    nServers = Integer.parseInt(line.split(":")[1].trim());
                } else if (line.startsWith("number of clients:")) {
                    nClients = Integer.parseInt(line.split(":")[1].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the number of columns.
     * @return the number of columns
     *
     */
    public int getnColumns() {
        return nColumns;
    }

    /**
     * Gets the number of rows.
     * @return the number of rows
     *
     */
    public int getnRows() {
        return nRows;
    }

    /**
     * Gets the number of servers.
     * @return the number of servers
     *
     */
    public int getnServers() { return nServers; }

    /**
     * Gets the number of clients.
     * @return the number of clients
     *
     */
    public int getnClients() {
        return nClients;
    }
}