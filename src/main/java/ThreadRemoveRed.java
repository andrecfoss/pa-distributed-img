import java.awt.image.BufferedImage;

/**
 * The RunnableRemoveRed implements a runnable that executes a function to remove the red component of an image split.
 */
public class ThreadRemoveRed extends Thread {
    private final BufferedImage imageSplit;
    private final int i;
    private final int j;
    private BufferedImage resultingImage;

    /**
     * The reader object for loading information.
     */
    public LoadInfoReader reader;

    /**
     * The writer object for loading information.
     */
    public LoadInfoWriter writer;

    /**
     * Constructs a new ThreadRemoveRed with the specified parameters.
     *
     * @param imageSplit     The image split to process.
     * @param i              The row index of the image split.
     * @param j              The column index of the image split.
     * @param loadInfoReader The reader object for loading information.
     * @param loadInfoWriter The writer object for loading information.
     */
    public ThreadRemoveRed ( BufferedImage imageSplit , int i , int j , LoadInfoReader loadInfoReader, LoadInfoWriter loadInfoWriter) {
        this.imageSplit = imageSplit;
        this.i = i;
        this.j = j;
        reader = loadInfoReader;
        writer = loadInfoWriter;
    }

    /**
     * Gets a BufferedImage corresponding to a split without the red component.
     *
     * @return a BufferedImage corresponding to a split without the red component, or null
     */
    public BufferedImage getResultingImage ( ) {
        return resultingImage;
    }

    /**
     * Gets the row index of the matrix of BufferedImages.
     *
     * @return the row index of the matrix of BufferedImages
     */
    public int getI ( ) {
        return i;
    }

    /**
     * Gets the column index of the matrix of BufferedImages.
     *
     * @return the column index of the matrix of BufferedImages
     */
    public int getJ ( ) {
        return j;
    }

    /**
     * Executes the client operation.
     * This method is part of the Runnable interface and is intended to be run in a separate thread.
     * It creates a client instance, sends a request to a server, and processes the response to generate the one part of
     * the image without the red.
     */
    @Override
    public void run ( ) {
        Client client = new Client ( "Client A" );
        Request request = new Request ( "greeting" , "Hello, Server!", imageSplit);
        int port = reader.LoadRead();
        Response response = client.sendRequestAndReceiveResponse ( "localhost" , port , request );
        writer.RemoveLoad(port);
        resultingImage = ImageTransformer.createImageFromBytes(response.getImageSection());
    }

    @Override
    public String toString ( ) {
        return "InstructionThread{" + "i=" + i + ", j=" + j + '}' + " Thread=#" + Thread.currentThread ( ).getId ( );
    }
}