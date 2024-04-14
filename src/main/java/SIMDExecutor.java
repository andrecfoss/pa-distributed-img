import java.awt.image.BufferedImage;

/**
 * The SIMDExecutor implements a SIMD executor for removing the red component of an image, by removing such component
 * from every sub-image.
 */
public class SIMDExecutor {

    private final int finalWidth;
    private final int finalHeight;
    private final int finalType;
    private final BufferedImage image;
    private final int nRows;
    private final int nColumns;
    private BufferedImage[][] splitsImage;
    private BufferedImage imageWithoutRed;
    private ThreadRemoveRed[][] threads;

    /**
     * The reader object for loading information.
     */
    public LoadInfoReader reader;

    /**
     * The writer object for loading information.
     */
    public LoadInfoWriter writer;

    /**
     * Constructs a new SIMDExecutor with the specified parameters.
     *
     * @param image          The input image to be processed.
     * @param nRows          The number of rows in the image.
     * @param nColumns       The number of columns in the image.
     * @param finalWidth     The final width of the processed image.
     * @param finalHeight    The final height of the processed image.
     * @param finalType      The final type of the processed image.
     * @param loadInfoReader The reader object for loading information.
     * @param loadInfoWriter The writer object for loading information.
     */
    public SIMDExecutor ( BufferedImage image , int nRows , int nColumns , int finalWidth , int finalHeight , int finalType, LoadInfoReader loadInfoReader , LoadInfoWriter loadInfoWriter) {
        this.image = image;
        this.nRows = nRows;
        this.nColumns = nColumns;
        this.finalWidth = finalWidth;
        this.finalHeight = finalHeight;
        this.finalType = finalType;
        splitsImage = new BufferedImage[ nRows ][ nColumns ];
        reader= loadInfoReader;
        writer= loadInfoWriter;
    }

    /**
     * Executes a set of instructions <i>before</i> the execution of the executor.
     */
    private void beforeExecute ( ) {
        // Split the image
        splitsImage = ImageTransformer.splitImage ( image , nRows , nColumns );
    }

    /**
     * Runs the SIMD pool executor.
     */
    public void execute ( ) {
        // ***
        beforeExecute ( );

        threads = new ThreadRemoveRed[ nRows ][ nColumns ];
        // Start all the threads
        for ( int i = 0 ; i < nRows ; i++ ) {
            for ( int j = 0 ; j < nColumns ; j++ ) {
                ThreadRemoveRed thread = new ThreadRemoveRed ( splitsImage[ i ][ j ] , i , j , reader, writer);
                thread.start ( );
                threads[ i ][ j ] = thread;
            }
        }

        // ***
        terminated ( );

    }

    /**
     * Executes a set of instructions <i>after</i> the execution of the executor.
     */
    private void terminated ( ) {
        try {
            // Waits for all threads to finish
            for ( int i = 0 ; i < nRows ; i++ ) {
                for ( int j = 0 ; j < nColumns ; j++ ) {
                    threads[ i ][ j ].join ( );
                    splitsImage[ threads[ i ][ j ].getI ( ) ][ threads[ i ][ j ].getJ ( ) ] = threads[ i ][ j ].getResultingImage ( );
                }

            }
        } catch ( InterruptedException e ) {
            throw new RuntimeException ( e );
        }
        imageWithoutRed = ImageTransformer.joinImages ( splitsImage , finalWidth , finalHeight , finalType );
    }

    /**
     * Gets the joined image without the red component.
     *
     * @return a joined BufferedImage without the red component
     */
    public BufferedImage getImageWithoutRed ( ) {
        return imageWithoutRed;
    }
}