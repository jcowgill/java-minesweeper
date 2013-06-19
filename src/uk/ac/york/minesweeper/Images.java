package uk.ac.york.minesweeper;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * Static class containing the game's images
 */
public final class Images
{
    /** Resources directory (beginning and ending with forward slash) */
    private static final String RES_DIRECTORY = "/res/";

    /** Image of a sea mine */
    public static final BufferedImage MINE = loadImageResource("mine.png");

    /** Image of a generic flag */
    public static final BufferedImage FLAG = loadImageResource("flag.png");

    /** Image of a normal face */
    public static final BufferedImage FACE_NORMAL = loadImageResource("default.png");

    /** Image of a face when you win */
    public static final BufferedImage FACE_WON = loadImageResource("won.png");

    /** Image of a face when you lose */
    public static final BufferedImage FACE_LOST = loadImageResource("lost.png");

    /**
     * Loads an image from the resources directory
     *
     * @param name image name
     * @return the loaded image
     */
    private static BufferedImage loadImageResource(String name)
    {
        // Load image stream
        try (InputStream imgStream = Images.class.getResourceAsStream(RES_DIRECTORY + name))
        {
            // Decompress image
            return ImageIO.read(imgStream);
        }
        catch (IOException e)
        {
            // Oh noes
            throw new RuntimeException("Could not load image file: " + name, e);
        }
    }

    private Images()
    {
    }
}
