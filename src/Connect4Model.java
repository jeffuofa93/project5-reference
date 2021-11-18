import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Random;

/**
 * Connect4Model.java Authors: Jeff Wiederkehr and Chris Herrera
 *
 * This class is the model for the Connect 4 game. This class stores the game state and provides public methods to
 * communicate the game state to the controller and model. Also it performs computation to determine certain aspects of
 * the game state like whether the game is over. We uses the Observable class to be able to notify the view whenever we
 * change the state of the game
 */
public class Connect4Model extends Observable {
    private List<List<Integer>> gameGrid = new ArrayList<>();
    private boolean myTurn = false;
    private static final int ROWS = 6;
    private static final int COLUMNS = 7;
    private boolean serverTurn = false;

    /**
     * Single constructor for the Connect4Model. The constructor calls build game grid which initializea the game board
     * with all empty circles represented by 0
     */
    public Connect4Model() {
        buildGameGrid();
    }

    /**
     * This method sets the myTurn variable to the boolean passed in
     *
     * @param mine - boolean true or false
     */
    public void setMyTurn(boolean mine) {
        myTurn = mine;
    }

    /**
     * Returns myTurn boolean
     *
     * @return - boolean True or False to see if it's the current instances turn or not
     */
    public boolean isMyTurn() {
        return myTurn;
    }

    /**
     * This method flips the state of the server turn
     */
    public void flipServerTurn() {
        serverTurn = !serverTurn;
    }

    /**
     * Drop a circle into the given column if space is available by updating gameGrid. If the column is full, an error
     * message will be given
     *
     * @param column specifies which column the move should be attempted on
     * @param color  specifies the color of the circle to place
     * @throws IllegalArgumentException to trigger an error message if the column is full
     */
    public void updateCircle(int column, int color) throws IllegalArgumentException {
        int row = findRow(column);
        if (row == -1)
            throw new IllegalArgumentException("Column full, pick somewhere else!");
        gameGrid.get(column).set(row, color);
        setChanged();
        notifyObservers(new Connect4MoveMessage(row, column, color));
    }

    /**
     * This method performs a computer move. The method first generates a random column to attempt to place the circle
     * then if the column is full it iterates through the columns until it finds the openining. This method will never
     * be called when the game is already over. After finding the row and column we update the color of the circle at
     * that location notify the view and return the column where we made the modification. The return is used to
     * provided the controller with information to pass to the client or server of the changes to the model
     *
     * @param color specifies the color of the circle to place
     * @return - int column location of the replacement
     */
    public int updateCircleAI(int color) {
        int column = new Random().nextInt(gameGrid.size());
        // -1 if column full
        int row = findRow(column);
        while (row == -1) {
            row = findRow(column);
            column++;
            if (column == COLUMNS)
                column = 0;
        }
        gameGrid.get(column).set(row, color);
        setChanged();
        notifyObservers(new Connect4MoveMessage(row, column, color));
        return column;
    }

    /**
     * Find a legal move for the Computer to make in the given column if no column is found it returns -1 otherwise it
     * returns the index of the valid row
     *
     * @param column specifies which column the move should be attempted on
     * @return - int location of the valid row or -1 if column is full
     */
    private int findRow(int column) {
        int bound = gameGrid.get(column).size();
        for (int i = bound - 1; i >= 0; i--) {
            if (gameGrid.get(column).get(i) == 0) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Builds the 2d grid that holds the game state. The grid is initialized with every index to 0 representing a white
     * circle
     *
     * @throws IllegalArgumentException to trigger an error message if the column is full
     */
    private void buildGameGrid() {
        for (int i = 0; i < COLUMNS; i++) {
            List<Integer> curRow = new ArrayList<>();
            for (int j = 0; j < ROWS; j++)
                curRow.add(j, 0);
            gameGrid.add(curRow);
        }
    }

    /**
     * Check if the 4 of the same color circles exist in any direction in the game board. The method calls seperate
     * methods to check vertical and horizontal wins. Then it creates two groups of columns 1-4 and 4-7 to check the
     * diaganol directions
     *
     * @return true if game is over, false if not
     */
    public boolean isGameOver() {
        int win = serverTurn ? 1 : 2; // only server can win like this?
        if (verticalCheck(win))
            return true;
        if (horizontalCheck(win))
            return true;
        // columns 0-3
        for (int startCol = 0; startCol <= 3; startCol++) {
            List<Integer> first = gameGrid.get(startCol);
            List<Integer> second = gameGrid.get(startCol + 1);
            List<Integer> third = gameGrid.get(startCol + 2);
            List<Integer> fourth = gameGrid.get(startCol + 3);
            if (diagonalCheck(win, first, second, third, fourth))
                return true;
        }
        // columns 3-6
        for (int startCol = 3; startCol < gameGrid.size(); startCol++) {
            List<Integer> first = gameGrid.get(startCol);
            List<Integer> second = gameGrid.get(startCol - 1);
            List<Integer> third = gameGrid.get(startCol - 2);
            List<Integer> fourth = gameGrid.get(startCol - 3);
            if (diagonalCheck(win, first, second, third, fourth))
                return true;
        }
        return false;
    }

    /**
     * Check if a line of 4 vertical circles of the same color exist
     *
     * @param win specifies the color of the most recent move so we can check if that move created a win state
     * @return true if a line of 4 circles exist, false if not
     */
    private boolean verticalCheck(int win) {
        for (List<Integer> integers : gameGrid) {
            int line = 0;
            for (Integer integer : integers) {
                if (integer == win)
                    line++;
                else
                    line = 0;
                if (line == 4)
                    return true;
            }
        }
        return false;
    }

    /**
     * Check if a line of 4 horizontal circles of the same color exist
     *
     * @param win specifies the color of the most recent move so we can check if that move created a win state
     * @return true if a line of 4 circles exist, false if not
     */
    private boolean horizontalCheck(int win) {
        for (int startCol = 0; startCol <= 3; startCol++) {
            List<Integer> first = gameGrid.get(startCol);
            List<Integer> second = gameGrid.get(startCol + 1);
            List<Integer> third = gameGrid.get(startCol + 2);
            List<Integer> fourth = gameGrid.get(startCol + 3);
            for (int j = 0; j < first.size(); j++)
                if (first.get(j) == win && second.get(j) == win && third.get(j) == win && fourth.get(j) == win)
                    return true;
        }
        return false;
    }

    /**
     * Check if a line of 4 diagonal circles of the same color exist
     *
     * @param win    specifies the color of the most recent move so we can check if that move created a win state
     * @param first  is one column to check in combination with the others
     * @param second is another column to check in combination with the others
     * @param third  is another column to check in combination with the others
     * @param fourth is another column to check in combination with the others
     * @return true if a line of 4 circles exist, false if not
     */
    private boolean diagonalCheck(int win, List<Integer> first, List<Integer> second, List<Integer> third,
                                  List<Integer> fourth) {
        for (int j = 0; j < 3; j++)
            if (first.get(j) == win && second.get(j + 1) == win && third.get(j + 2) == win && fourth.get(j + 3) == win)
                return true;
        return false;
    }
    
    
    /**
     * Reset to a fresh instance of the game. This resets the grid on the backend (model) and the frontend (view).
     *
     */
    public void newGameReset() {
    	gameGrid = new ArrayList<>();
    	buildGameGrid();
    	setChanged();
    	notifyObservers("reset");
    }
}
