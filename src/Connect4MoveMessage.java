import java.io.Serializable;

/**
 * Connect4MoveMessage.java Authors: Jeff Wiederkehr and Chris Herrera
 *
 * This class is the message that is used to communicate from the model to the view in notify observers and to
 * communicate from the client to the server to notify the other of changes in state.
 */
public class Connect4MoveMessage implements Serializable {
    public static int YELLOW = 1;
    public static int RED = 2;
    private static final long serialVersionUID = 1L;
    private int row;
    private int col;
    private int color;

    /**
     * This constructor takes the row, column and color of the circle as parameters and sets them to their respective
     * class values
     *
     * @param row - int row location of the circle
     * @param col - int column location of the circle
     * @param color - int color the color of the circle 1 = yellow, 2 = red, 0 = white
     */
    public Connect4MoveMessage(int row, int col, int color) {
        this.row = row;
        this.col = col;
        this.color = color;
    }

    /**
     * Returns the row value which is the circles row position
     *
     * @return - int row circles row position
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the circles column position
     *
     * @return - int column the circles column position
     */
    public int getColumn() {
        return col;
    }

    /**
     * Returns the color. 1 represents yellow, 2 represents red and 0 represents white
     *
     * @return - int color of the circle at the coordinates
     */
    public int getColor() {
        return color;
    }

}
