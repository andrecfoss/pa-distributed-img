import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a response received from a server. This class can be extended to include various types of data depending
 * on the application's needs.
 */
public class Response implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; // Ensure compatibility during serialization

    /**
     * The status of the response.
     */
    private String status;

    /**
     * The message content of the response.
     */
    private String message;

    /**
     * The section of the image included in the response.
     */
    private byte[] imageSection;

    /**
     * Constructs a new response with the given status, message, and image section.
     *
     * @param status       The status of the response.
     * @param message      The message content of the response.
     * @param imageSection The image section included in the response.
     */
    public Response ( String status , String message , BufferedImage imageSection) {
        this.status = status;
        this.message = message;
        this.imageSection = ImageTransformer.createBytesFromImage(imageSection);
    }

    /**
     * Returns the status of the response.
     *
     * @return The status of the response.
     */
    public String getStatus ( ) {
        return status;
    }

    /**
     * Sets the status of the response.
     *
     * @param status The status of the response.
     */
    public void setStatus ( String status ) {
        this.status = status;
    }

    /**
     * Returns the message of the response.
     *
     * @return The message of the response.
     */
    public String getMessage ( ) {
        return message;
    }

    /**
     * Sets the message of the response.
     *
     * @param message The message of the response.
     */
    public void setMessage ( String message ) {
        this.message = message;
    }

    /**
     * Retrieves the image section from this response.
     *
     * @return The image section included in the response.
     */
    public byte[] getImageSection() {
        return imageSection;
    }

    /**
     * Sets the image section for this response.
     *
     * @param imageSection The image section to be set in the response.
     */
    public void setImageSection(byte[] imageSection) {
        this.imageSection = imageSection;
    }

    @Override
    public String toString ( ) {
        return "Response{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}