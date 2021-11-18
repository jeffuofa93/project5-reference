import javafx.application.Application;

/**
 * Connect4.java Authors: Jeff Wiederkehr and Chris Herrera
 *
 * This class is the main method for our Connect4Game. The method calls and launches our GUI view.
 */
public class Connect4 {

    /**
     * Launch the GUI for the game
     *
     * @param args Won't be used in this iteration of the game
     */
    public static void main(String[] args) {
        Application.launch(Connect4View.class, args);
    }

}
