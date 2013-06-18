package uk.ac.york.minesweeper;

import java.awt.image.BufferedImage;
import java.io.InputStream;

/**
 * Static class containing the game's images
 */
public final class Images
{
    public static final BufferedImage MINE;
    public static final BufferedImage FLAG;

    static
    {
        // Open core image file
        InputStream coreImage = Images.class.getClassLoader().getResourceAsStream("images.png");

        // TODO Load Images
        MINE = null;
        FLAG = null;
    }

    private Images()
    {
    }
}
