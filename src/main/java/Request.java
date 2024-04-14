import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a request to be sent to a server. This class can be extended to include various types of data depending on
 * the application's needs.
 */
public class Request implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; // Ensure compatibility during serialization

    /**
     * The type of message in the request.
     */
    private String messageType;

    /**
     * The content of the message in the request.
     */
    private String messageContent;

    /**
     * The section of the image included in the request.
     */
    private byte[] imageSection;

    /**
     * Constructs a new request with the given message type, message content, and image section.
     *
     * @param messageType    The type of message in the request.
     * @param messageContent The content of the message in the request.
     * @param imageSection   The section of the image included in the request.
     */
    public Request ( String messageType , String messageContent , BufferedImage imageSection ) {
        this.messageType = messageType;
        this.messageContent = messageContent;
        this.imageSection = ImageTransformer.createBytesFromImage(imageSection);
    }

    /**
     * Returns the type of the message.
     *
     * @return The type of the message.
     */
    public String getMessageType ( ) {
        return messageType;
    }

    /**
     * Sets the type of the message.
     *
     * @param messageType The type of the message.
     */
    public void setMessageType ( String messageType ) {
        this.messageType = messageType;
    }

    /**
     * Returns the content of the message.
     *
     * @return The content of the message.
     */
    public String getMessageContent ( ) {
        return messageContent;
    }

    /**
     * Sets the content of the message.
     *
     * @param messageContent The content of the message.
     */
    public void setMessageContent ( String messageContent ) {
        this.messageContent = messageContent;
    }

    /**
     * Retrieves the image section from this request.
     *
     * @return The image section included in the request.
     */
    public byte[] getImageSection() {
        return imageSection;
    }

    /**
     * Sets the image section for this request.
     *
     * @param imageSection The image section to be set in the request.
     */
    public void setImageSection(byte[] imageSection) {
        this.imageSection = imageSection;
    }

    @Override
    public String toString ( ) {
        return "Request{" + "messageType='" + messageType + '\'' + ", messageContent='" + messageContent + '\'' + '}';
    }
}