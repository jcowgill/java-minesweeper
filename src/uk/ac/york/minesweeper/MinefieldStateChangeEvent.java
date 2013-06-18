package uk.ac.york.minesweeper;

import java.util.EventObject;

/**
 * Event fired when the minefield changes game state
 */
public class MinefieldStateChangeEvent extends EventObject
{
    private static final long serialVersionUID = 1L;

    /**
     * Initializes a new MinefieldStateChangeEvent object
     *
     * @param source event source
     */
    public MinefieldStateChangeEvent(Object source)
    {
        super(source);
    }
}
