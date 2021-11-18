import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.function.UnaryOperator;

/**
 * NetworkWindow.java
 * Authors: Jeff Wiederkehr and Chris Herrera
 *
 * This class represent the GUI for the network window as well as storing some state regarding the network of the
 * program. The GUI is built using a borderpane with a Vbox and nested Hboxes to contain each radio button or input
 * field.
 */
public class NetworkWindow extends Stage {

    private boolean isServer;
    private boolean isHuman;
    private String server;
    private String port;
    private boolean canClick;
    private Connect4Controller controller;
    private VBox networkBox = new VBox();

    /**
     * Single constructor for the network window. The constructor sets default values for the server and port and
     * sets the title for the program and calls the set network method
     * @param controller - controller for the current users instance of connect 4
     */
    public NetworkWindow(Connect4Controller controller) {
        this.controller = controller;
        isServer = isHuman = true;
        server = "localhost";
        port = "4000";
        this.setTitle("Network Setup");
        // make application blocking
        this.initModality(Modality.APPLICATION_MODAL);
        setNetworkWindow();
    }

    /**
     * This is the base method that sets the different fields and subregions of the network window. It divides the
     * window into 4 section representing horizontal rows in the window
     */
    private void setNetworkWindow() {
        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane);
        networkBox = new VBox();
        networkBox.setSpacing(14);
        setCreateRow();
        setPlayAsRow();
        setServerRow();
        createButtonRow();
        borderPane.setCenter(networkBox);
        this.setScene(scene);
        setStage();
        BorderPane.setMargin(networkBox, new Insets(8, 8, 8, 8));
    }

    /**
     * This method sets the create row in the network window. The create row contains a label and two radio buttons
     * inside a Hbox. The method also adds the radio buttons to a Toggalable group so only on can be selected and
     * adds event listeners on each radio button to update the state when the user makes a selection
     */
    private void setCreateRow() {
        HBox create = new HBox();
        create.setSpacing(8);
        Label createLabel = new Label("Create: ");
        // makes radio buttons exclusive
        ToggleGroup createToggle = new ToggleGroup();
        RadioButton selectServer = new RadioButton("Server");
        selectServer.setToggleGroup(createToggle);
        selectServer.setSelected(true);
        // update state on selection
        selectServer.setOnAction(actionEvent -> isServer = true);
        RadioButton selectClient = new RadioButton("Client");
        selectClient.setToggleGroup(createToggle);
        // update state on selection
        selectClient.setOnAction(actionEvent -> isServer = false);
        create.getChildren().addAll(createLabel, selectServer, selectClient);
        networkBox.getChildren().add(create);
    }

    /**
     * This method sets the Play as row of the network window. This row contains a label and two radio buttons. This
     * implementation is identical to the above setCreateBox but instead of changing isServer it changes isHuman
     */
    private void setPlayAsRow() {
        HBox playAs = new HBox();
        playAs.setSpacing(6);
        Label playAsLabel = new Label("Play as: ");
        ToggleGroup playAsToggle = new ToggleGroup();

        // human toggle
        RadioButton human = new RadioButton();
        human.setToggleGroup(playAsToggle);
        human.setText("Human");
        human.setSelected(true);
        human.setOnAction(actionEvent -> isHuman = true);

        // computer toggle
        RadioButton computer = new RadioButton();
        computer.setToggleGroup(playAsToggle);
        computer.setText("Computer");
        computer.setOnAction(actionEvent -> isHuman = false);
        playAs.getChildren().addAll(playAsLabel, human, computer);
        networkBox.getChildren().add(playAs);
    }

    /**
     * This method sets the Server and Port textfield row. The method contains an Hbox with two nested Hboxes both
     * containg a label and a textfield. Each texfield has an event to update the server and portfields to the new
     * values everytime the textbox changes
     */
    private void setServerRow() {
        HBox serverAndPort = new HBox();
        serverAndPort.setSpacing(8);

        // server label and textfield
        HBox serverTextAndLabel = new HBox();
        serverTextAndLabel.setSpacing(8);
        Label serverLabel = new Label("Server");
        TextField serverTextField = new TextField(server);
        // update server state
        serverTextField.setOnKeyTyped(keyEvent -> server = serverTextField.getText());
        serverTextAndLabel.getChildren().addAll(serverLabel, serverTextField);

        // port label and text field
        HBox portTextAndLabel = new HBox();
        setPortTextAndLabel(portTextAndLabel);
        serverAndPort.getChildren().addAll(serverTextAndLabel, portTextAndLabel);
        networkBox.getChildren().add(serverAndPort);
    }

    /**
     * This sets the port text field and restricts the port text field to only integers.
     *
     * @param portTextAndLabel
     */
    private void setPortTextAndLabel(HBox portTextAndLabel) {
        portTextAndLabel.setSpacing(8);
        Label portLabel = new Label("Port");
        TextField portTextField = new TextField(String.valueOf(port));
        // if new text is not a digit don't update textfield
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String input = change.getText();
            return input.matches("[0-9]*") ? change : null;
        };
        portTextField.setTextFormatter(new TextFormatter<>(integerFilter));
        // update port state
        portTextField.setOnKeyTyped(keyEvent -> port = portTextField.getText());
        portTextAndLabel.getChildren().addAll(portLabel, portTextField);
    }

    /**
     * This method creates the button row. It adds the ok and cancel buttons to a Hbox. It adds event's to both
     * buttons that trigger the controller to start the sever or client depending on the current game state
     */
    private void createButtonRow() {
        HBox buttonRow = new HBox();
        buttonRow.setSpacing(8);
        Button ok = new Button("OK");
        // call method to notify controller
        ok.setOnAction(actionEvent -> {
        	controller.newGameReset();
            startNetworkConnections();
            this.close();
        });
        // call method to notify controller
        Button cancel = new Button("Cancel");
        cancel.setOnAction(actionEvent -> {
        	controller.newGameReset();
            startNetworkConnections();
            this.close();
        });
        buttonRow.getChildren().addAll(ok, cancel);
        networkBox.getChildren().add(buttonRow);
    }


    /**
     * This method starts the game by notifying the controller when ok or cancel is clicked. It uses the game state
     * to determine to start the server or the client. The server and the port are also passed to the controller
     */
    private void startNetworkConnections() {
        controller.setPort(Integer.parseInt(port));
        controller.setServer(server);
        if (isServer)
            controller.startServer();
        else {
            controller.startClient();
            canClick = false;
        }
    }

    /**
     * This method sets the dimensions for the stage
     */
    private void setStage() {
        this.setMaxWidth(500);
        this.setMinWidth(500);
        this.setMaxHeight(200);
        this.setMinHeight(200);
    }

    /**
     * This method returns a boolean whether the current instance is a computer or human player
     * @return - true if human player else false
     */
    public boolean isHuman() {
        return isHuman;
    }
}
