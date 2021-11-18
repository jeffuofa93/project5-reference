import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Connect4Controller.java Authors: Jeff Wiederkehr and Chris Herrera
 *
 * This class is the controller for our connect 4 game. The controller implements the networking for the game to allow a
 * client and server to connect. Also the controller acts as the go between for the model and view.
 */
public class Connect4Controller {
    private final Connect4Model model;
    private Socket connection;
    private int port;
    private String server;
    private boolean isServer = false;
    private boolean isConnected = false;

    ObjectOutputStream oos;
    ObjectInputStream ois;

    /**
     * Construct a Connect4Controller
     *
     * @param model The Connect4Model to associate with this Controller
     */
    public Connect4Controller(Connect4Model model) { // maybe pass the server and port here
        this.model = model;
    }


    /**
     * Start a server side connection
     *
     */
    public void startServer() {
        try {
            ServerSocket server = new ServerSocket(port);
            connection = server.accept();
            oos = new ObjectOutputStream(connection.getOutputStream());
            ois = new ObjectInputStream(connection.getInputStream());
            isServer = true;
            isConnected = true;
            model.setMyTurn(true);
        } catch (IOException e) {
            System.err.println("Something went wrong with the network! " + e.getMessage());
        }
    }

    
    /**
     * This method starts the connection for the client. The method connects to the active socket created by the the
     * server. This is always ran after the server. The client also takes the message which contains the first move from
     * the server and updates the clients state.
     */
    public void startClient() {
        try {
            connection = new Socket(server, port);
            isServer = false;
            isConnected = true;
            model.setMyTurn(false);
            oos = new ObjectOutputStream(connection.getOutputStream());
            ois = new ObjectInputStream(connection.getInputStream());
            Thread t = new Thread(() -> {
                try {
                    Connect4MoveMessage otherMsg = (Connect4MoveMessage) ois.readObject();
                    Platform.runLater(() -> {
                        model.setMyTurn(true);
                        model.updateCircle(otherMsg.getColumn(), otherMsg.getColor());
                        model.flipServerTurn();
                    });
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("Something went wrong with serialization: " + e.getMessage());
                }
            });
            t.start();
        } catch (IOException e) {
            System.err.println("Something went wrong with the network! " + e.getMessage());
        }
    }
    

    /**
     * Check if the game is over
     *
     * @return true if someone has won the game, false if the game should continue
     */
    public boolean isGameOver() {
        return model.isGameOver();
    }


    /**
     * Drop a circle into the given column if space is available. If the column is full, an error message will be given
     *
     * @param column specifies which column the move should be attempted on
     * @throws IllegalArgumentException to trigger an error message if the column is full
     */
    public void humanTurn(int column) throws IllegalArgumentException {
        model.updateCircle(column, (isServer) ? Connect4MoveMessage.YELLOW : Connect4MoveMessage.RED);
        Connect4MoveMessage msg = new Connect4MoveMessage(-1, column, (isServer) ? Connect4MoveMessage.YELLOW :
                Connect4MoveMessage.RED);
        sendMessage(msg);
    }

    /**
     * Drop a circle into a random legal column for the AI
     */
    public void computerTurn() {
        int column = model.updateCircleAI((isServer) ? Connect4MoveMessage.YELLOW : Connect4MoveMessage.RED);
        Connect4MoveMessage msg = new Connect4MoveMessage(-1, column, (isServer) ? Connect4MoveMessage.YELLOW :
                Connect4MoveMessage.RED);
        sendMessage(msg);
    }

    /**
     * Send the message with our move encoded into it to the other end of the network connection
     *
     * @param msg contains the move data
     */
    private void sendMessage(Connect4MoveMessage msg) {
        if (!isConnected)
            return;
        Thread t = new Thread(() -> {
            try {
                oos.writeObject(msg);
                oos.flush();
                Connect4MoveMessage otherMsg = (Connect4MoveMessage) ois.readObject();
                Platform.runLater(() -> {
                    model.setMyTurn(true);
                    model.updateCircle(otherMsg.getColumn(), otherMsg.getColor());
                });
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Something went wrong with serialization: " + e.getMessage());
            }
        });
        t.start();
    }

    /**
     * This method sets the turn for the server or the client to the passed in boolean value
     *
     * @param mine - boolean either true or false
     */
    public void setTurn(boolean mine) {
        model.setMyTurn(mine);
    }

    /**
     * This method returns whether it is the current instances turn or not which determines if the
     *
     * @return
     */
    public boolean getCanClick() {
        return model.isMyTurn();
    }

    /**
     * This method sets the port for the client or server
     *
     * @param port - int number for the port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * This method sets the server for the client or the server
     *
     * @param server - String server adress for the connection
     */
    public void setServer(String server) {
        this.server = server;
    }
    
    /**
     * Reset to a fresh instance of the game. This resets the grid on the backend (model) and the frontend (view).
     *
     */
    public void newGameReset() {
    	model.newGameReset();
    }

}
