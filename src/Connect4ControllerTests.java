import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Connect4Controller.java Authors: Jeff Wiederkehr and Chris Herrera
 *
 * This class implements the testing for the Connect4Controller. Our group struggled with th testing on the networking
 * sections and were unable to test the Platform.run and messaging components.
 */
public class Connect4ControllerTests {

    /**
     * This test tests the servers win game functionality as well as adding elements to the board, setting the turn and
     * the exception for too many elements in a column. The Client is created on a thread and then the server events
     * are triggered.
     */
    @Test
    void testServer() {
        Connect4Model clientModel = new Connect4Model();
        Connect4Controller clientController = new Connect4Controller(clientModel);
        clientController.setServer("localhost");
        clientController.setPort(4000);
        Thread t = new Thread(clientController::startClient);
        t.start();
        Connect4Model serverModel = new Connect4Model();
        Connect4Controller serverController = new Connect4Controller(serverModel);
        serverController.setPort(4000);
        serverController.setServer("localhost");
        serverController.startServer();
        // test human turn
        serverController.humanTurn(0);
        serverController.humanTurn(0);
        serverController.humanTurn(0);
        serverController.humanTurn(0);
        serverController.humanTurn(0);
        serverController.humanTurn(0);

        // test game over
        assertTrue(serverController.isGameOver());
        serverController.setTurn(false);
        // test can click functionality
        assertFalse(serverController.getCanClick());
        // test full column
        assertThrows(IllegalArgumentException.class, () -> serverController.humanTurn(0));
    }

    /**
     * This test has the same set up as the previous method but instead it tests the computerTurn functionality. This
     * test completely fills the board with moves and then checks the game over and that adding a new element causes
     * an exception
     */
    @Test
    void testServerComputer() {
        Connect4Model clientModel = new Connect4Model();
        Connect4Controller clientController = new Connect4Controller(clientModel);
        clientController.setServer("localhost");
        clientController.setPort(4000);
        Thread t = new Thread(clientController::startClient);
        t.start();
        Connect4Model serverModel = new Connect4Model();
        Connect4Controller serverController = new Connect4Controller(serverModel);
        serverController.setPort(4000);
        serverController.setServer("localhost");
        serverController.startServer();
        // fill board with moves
        for (int i = 0; i < 42; i++) {
            serverController.computerTurn();
        }
        serverModel.flipServerTurn();
        // check game over
        assertTrue(serverController.isGameOver());
        // check exception due to board full
        assertThrows(IllegalArgumentException.class, () -> serverController.humanTurn(0));
    }

    /**
     * This test has the same set up as previous tests but after winning the game the method calls the newGameReset
     * method and checks that the game state has restarted and that gameOver is now false
     */
    @Test
    void testNewGame() {
        Connect4Model clientModel = new Connect4Model();
        Connect4Controller clientController = new Connect4Controller(clientModel);
        clientController.setServer("localhost");
        clientController.setPort(4000);
        Thread t = new Thread(clientController::startClient);
        t.start();
        Connect4Model serverModel = new Connect4Model();
        Connect4Controller serverController = new Connect4Controller(serverModel);
        serverController.setPort(4000);
        serverController.setServer("localhost");
        serverController.startServer();

        serverController.humanTurn(0);
        serverController.humanTurn(0);
        serverController.humanTurn(0);
        serverController.humanTurn(0);
        serverController.humanTurn(0);
        serverController.humanTurn(0);
        serverModel.flipServerTurn();
        assertTrue(serverController.isGameOver());
        serverController.newGameReset();
        assertFalse(serverController.isGameOver());
    }


}



