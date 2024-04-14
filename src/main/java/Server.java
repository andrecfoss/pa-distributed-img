import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A TCP/IP server that listens for connections on a specified port and handles each client connection in a separate
 * thread.
 */
public class Server extends Thread {

    /**
     * The port number on which the server listens.
     */
    protected final int port;
    private final TaskPool taskPool;

    /**
     * The writer object for loading information.
     */
    public LoadInfoWriter writer;


    /**
     * Constructs a new server with the specified port, thread pool size, and load info writer.
     *
     * @param port           The port number on which the server listens.
     * @param poolSize       The size of the thread pool for handling incoming requests.
     * @param loadInfoWriter The writer object for loading information.
     */
    public Server ( int port , int poolSize, LoadInfoWriter loadInfoWriter) {
        this.port = port;
        writer = loadInfoWriter;
        this.taskPool = new TaskPool(poolSize);
    }

    /**
     * The entry point of the server thread. Starts the server to accept and handle client connections.
     */
    @Override
    public void run ( ) {
        try {
            startServer ( );
        } catch ( Exception e ) {
            e.printStackTrace ( );
        }
    }

    /**
     * Initializes the server socket and continuously listens for client connections. Each client connection is handled
     * in a new thread.
     *
     * @throws IOException If an I/O error occurs when opening the socket.
     */
    protected void startServer ( ) throws IOException {
        try ( ServerSocket serverSocket = new ServerSocket ( port ) ) {
            System.out.println ( "Server started on port " + port );
            while ( true ) {
                Socket clientSocket = serverSocket.accept ( );
                taskPool.submitTask(new ClientHandler(clientSocket));
                writer.AddLoad(port);
            }
        }
    }

    /**
     * Handles client connections. Reads objects from the client, processes them, and sends a response back.
     */
    private static class ClientHandler implements Runnable {

        private final Socket clientSocket;

        /**
         * Constructs a new ClientHandler instance.
         *
         * @param socket The client socket.
         */
        public ClientHandler ( Socket socket ) {
            this.clientSocket = socket;
        }

        /**
         * The entry point of the client handler thread. Manages input and output streams for communication with the
         * client.
         */
        @Override
        public void run ( ) {
            try ( ObjectOutputStream out = new ObjectOutputStream ( clientSocket.getOutputStream ( ) ) ;
                  ObjectInputStream in = new ObjectInputStream ( clientSocket.getInputStream ( ) ) ) {

                Request request;
                // Continuously read objects sent by the client and respond to each.
                while ( ( request = ( Request ) in.readObject ( ) ) != null ) {
                    out.writeObject ( handleRequest ( request ) );
                }

                System.out.println(request.toString());

            } catch ( EOFException e ) {
                System.out.println ( "Client disconnected." );
            } catch ( IOException | ClassNotFoundException e ) {
                System.err.println ( "Error handling client: " + e.getMessage ( ) );
            } finally {
                try {
                    clientSocket.close ( );
                } catch ( IOException e ) {
                    System.err.println ( "Error closing client socket: " + e.getMessage ( ) );
                }
            }
        }



        /**
         * Processes the client's request and generates a response.
         *
         * @param request The object received from the client.
         *
         * @return The response object to be sent back to the client.
         */
        private Response handleRequest ( Request request ) {
            if(request.getImageSection()!=null){
                BufferedImage editedImage = ImageTransformer.removeReds(ImageTransformer.createImageFromBytes(request.getImageSection()));
                return new Response ( "OK" , "Hello, Client!" ,editedImage);
            }else{
                return  new Response("Error","No image in request",null);
            }
        }
    }

}