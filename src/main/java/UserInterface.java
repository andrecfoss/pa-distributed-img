import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * This class represents the user interface for the image editing application.
 */
public class UserInterface {
    private JFrame frame;
    private JPanel panel;
    private JLabel label;

    /**
     * The ImageIcon used in the user interface.
     */
    public ImageIcon icon;

    /**
     * The edited image displayed in the user interface.
     */
    public BufferedImage sampleImage;

    /**
     * The edited image displayed in the user interface.
     */
    public BufferedImage editedImage;

    /**
     * The name of the image displayed in the user interface.
     */
    public String imageName;

    /**
     * Constructs a new UserInterface object with the specified components.
     *
     * @param configReader   the configuration reader for application settings
     * @param loadInfoReader the reader to work with each server load info
     * @param loadInfoWriter the writer to work with each server load info
     */
    public UserInterface(ConfigReader configReader, LoadInfoReader loadInfoReader, LoadInfoWriter loadInfoWriter) {
        frame = new JFrame("pa-distributed-img");
        frame.setSize(1200, 1200);
        panel = new JPanel();
        label = new JLabel();
        label.setPreferredSize(new Dimension(1200, 1200));
        panel.add(label);

        // Button to select an image
        JButton button = new JButton("Select Image");
        panel.add(button);

        // Action listener for the "Select Image" button
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        BufferedImage selectedImage = ImageIO.read(selectedFile);
                        sampleImage = resizeImage(selectedImage);
                        icon = new ImageIcon(sampleImage);
                        label.setIcon(icon);
                        imageName = selectedFile.getName();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        // Button to remove red from the image
        JButton removeRed = new JButton();
        removeRed.setText("Remove red");
        panel.add(removeRed);

        // Action listener for the "Remove red" button
        removeRed.addActionListener(e -> {
            if (icon != null) {
                editedImage = (ImageTransformer.removeRedFullImage(sampleImage, configReader.getnRows(), configReader.getnColumns(), loadInfoReader, loadInfoWriter));
                icon.setImage(editedImage);
                label.setIcon(icon);
                panel.repaint();
                SaveEditedImage saveEditedImage = new SaveEditedImage(imageName, editedImage);
                saveEditedImage.saveImage();
            }
        });

        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Resizes the given image to have equal width and height, maintaining the original aspect ratio.
     *
     * @param originalImage The original image to be resized.
     * @return The resized image with equal width and height.
     */
    private BufferedImage resizeImage(BufferedImage originalImage) {
        int minDimension = Math.min(originalImage.getWidth(), originalImage.getHeight());
        BufferedImage resizedImage = new BufferedImage(minDimension, minDimension, originalImage.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, minDimension, minDimension, null);
        g.dispose();
        return resizedImage;
    }
}


