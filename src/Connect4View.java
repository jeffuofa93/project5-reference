import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Connect4View.java Authors: Jeff Wiederkehr and Chris Herrera
 *
 * This class implements the GUI view for the Connect 4 Game. The class has two separate modes of operation where either
 * the current active user is a player or when their is a computer simulated move
 */
public class Connect4View extends Application implements Observer {
    private GridPane grid = new GridPane();
    private NetworkWindow networkWindow;
    private Connect4Controller controller;
    private static final int ROWS = 6;
    private static final int COLUMNS = 7;
    private Map<compoundKey, Circle> nodeMap = new HashMap<>();

    /**
     * Launch the GUI for the game
     *
     * @param args Won't be used in this iteration of the game
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Call startGame to set up the GUI
     *
     * @param stage the main Stage to set up
     */
    public void start(Stage stage) {
        startGame(stage);
    }

    /**
     * Initialize the model and controller; set up the GUI to receive user input and setup white circles
     *
     * @param stage the main Stage to set up
     */
    public void startGame(Stage stage) {
        // set model and controller
        Connect4Model model = new Connect4Model();
        controller = new Connect4Controller(model);
        model.addObserver(this);

        networkWindow = new NetworkWindow(controller);
        BorderPane window = new BorderPane();
        window.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));
        Scene scene = new Scene(window);
        scene.setFill(Color.BLUE);
        setGrid();
        MenuBar menuBar = createMenu(stage);
        window.setTop(menuBar);
        window.setCenter(grid);
        BorderPane.setMargin(grid, new Insets(8, 8, 8, 8));
        setStage(stage);
        stage.setScene(scene);
        stage.setTitle("Connect 4");
        stage.show();
        Timeline threeSecondsEvent = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            if (!networkWindow.isHuman() && controller.getCanClick() && !controller.isGameOver()) {
                gameLoopAI();
            }
        }));
        threeSecondsEvent.setCycleCount(Timeline.INDEFINITE);
        threeSecondsEvent.play();
    }

    /**
     * This method performs a game move by the computer. The method first calls the controller to execute a turn and
     * then sets the turn to false in the controller
     */
    private void gameLoopAI() {
        controller.computerTurn();
        controller.setTurn(false);
    }

    /**
     * Create the MenuBar to hold the New Game option
     *
     * @param stage the main Stage to set up
     * @return menuBar containing the New Game option which will open the Network Setup box
     */
    private MenuBar createMenu(Stage stage) {
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("File");
        MenuItem newGame = new MenuItem("New Game");
        newGame.setOnAction(actionEvent -> networkWindow.showAndWait());
        menu.getItems().add(newGame);
        menuBar.getMenus().add(menu);
        return menuBar;
    }

    /**
     * This method sets the dimensions for the stage
     *
     * @param stage - Stage for display
     */
    private void setStage(Stage stage) {
        stage.setMaxWidth(344);
        stage.setMaxHeight(360);
        stage.setHeight(360);
        stage.setWidth(344);
    }

    /**
     * This method creates the 7 columns in the game board, adds the circle elements to a map for updates and adds event
     * handlers to each column for user click events. The click events first check that it is the users turn and then
     * notify the controller of the move and set the turn to false. If the column is full an exception is thrown from
     * the model through the controller and we use the message from the exception in our popup alert
     */
    private void setGrid() {
        grid.setHgap(8);
        grid.setVgap(8);
        for (int i = 0; i < COLUMNS; i++) {
            VBox vBox = new VBox();
            for (int j = 0; j < ROWS; j++) {
                Circle circle = new Circle();
                circle.setRadius(20);
                circle.setFill(Color.WHITE);
                compoundKey circleKey = new compoundKey(i, j);
                vBox.getChildren().add(circle);
                vBox.setSpacing(8);
                vBox.setOnMouseClicked(mouseEvent -> {
                    try {
                        if (!controller.getCanClick())
                            return;
                        controller.humanTurn(circleKey.i);
                        controller.setTurn(false);
                    } catch (IllegalArgumentException e) {
                        popupEventAlert(e.getMessage(), Alert.AlertType.ERROR);
                    }
                });
                nodeMap.put(new compoundKey(j, i), circle);
            }
            grid.add(vBox, i, 0);
        }
    }

    /**
     * This record is used to make a unique key to use in our node map for each i,j index in the 2d grid pane. This is
     * to prevent issues where 2,3 and 3,2 would overwrite each other in the map
     */
    public record compoundKey(int i, int j) {
    }

    /**
     * This method receives an update from the model every time the model changes. The model passes a
     * Connect4MoveMessage object which contains information for which
     *
     * @param o   The model
     * @param arg The Connect4MoveMessage representing the change that was made in the model
     */
    @Override
    public void update(Observable o, Object arg) {
    	if (arg instanceof String) {
    		if ( ((String) arg).equals("reset")) {
    			setGrid();
    		}
    	}
    	else {
	        Connect4MoveMessage message = (Connect4MoveMessage) arg;
	        int row = message.getRow();
	        int column = message.getColumn();
	        int color = message.getColor();
	        if (controller.isGameOver()) {
	            popupEventAlert("You Won", Alert.AlertType.CONFIRMATION);
	            grid.getChildren().forEach(col -> col.setDisable(true));
	        }
	        Paint paint = color == 1 ? Color.YELLOW : Color.RED;
	        compoundKey key = new compoundKey(row, column);
	        Circle updateCircle = nodeMap.get(key);
	        updateCircle.setFill(paint);
    	}
    }

    /**
     * Create a pop up event alert with the given message and alert type
     *
     * @param message   The message to place in the pop up
     * @param alertType The type of alert to use for the pop up (ERROR if column full or CONFIRMATION if game over)
     */
    private void popupEventAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setAlertType(alertType);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
