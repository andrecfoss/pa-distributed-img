import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class to reads load information from the Load.info text file.
 */
public class LoadInfoReader {

    /**
     * The name of the file being read.
     */
    public String fileName;
    private int [] servers={0,0,0};

    /**
     * The port number of the server.
     */
    public int serverPort;

    /**
     * The lock used to synchronize access to resources.
     */
    public ReentrantLock lock;

    /**
     * Constructor of the LoadInfoReader class.
     * @param fileName The name of the file to be read.
     * @param loadInfoLock The lock to ensure safe reading of load information.
     */
    public LoadInfoReader(String fileName, ReentrantLock loadInfoLock) {
        this.fileName = fileName;
        this.lock= loadInfoLock;
    }

    /**
     * Method to read load information from the specified file.
     * @return The port of the server with the lowest load.
     */
    public int LoadRead(){
        lock.lock();
        try (BufferedReader br = new BufferedReader(new FileReader( fileName ))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("8888:")) {
                    servers[0] = Integer.parseInt(line.split(":")[1].trim());
                } else if (line.startsWith("8000:")) {
                    servers[1] = Integer.parseInt(line.split(":")[1].trim());
                } else if (line.startsWith("8100:")) {
                    servers[2] = Integer.parseInt(line.split(":")[1].trim());
                }
            }
            int min = servers[0];
            for(int i=0;i<servers.length;i++){
                if(servers[i]<min){
                    min=servers[i];
                }
            }
            if(servers[0]==min){
                serverPort=8888;
            } else if (servers[1]==min) {
                serverPort=8000;
            } else if (servers[2]==min) {
                serverPort=8100;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        lock.unlock();
        return serverPort;
    }
}
