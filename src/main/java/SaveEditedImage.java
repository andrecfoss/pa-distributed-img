import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * This class is responsible for saving an edited image to a specified location.
 */
public class SaveEditedImage {
    private String imageName;
    private BufferedImage image;

    /**
     * Constructs a new SaveEditedImage object with the given image name and image.
     *
     * @param imageName the name of the image file
     * @param image the BufferedImage object representing the image
     */
    public SaveEditedImage(String imageName, BufferedImage image) {
        this.imageName = imageName;
        this.image = image;
    }

    /**
     * Saves the edited image to a specified location.
     */
    public void saveImage() {
        try {
            File outputFolder = new File("result");
            if (!outputFolder.exists()) {
                outputFolder.mkdirs();
            }

            File outputFile = new File(outputFolder, imageName);
            ImageIO.write(image, "png", outputFile);
            System.out.println("Edited Image Saved Successfully: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to Save the Edited Image: " + e.getMessage());
        }
    }
}
