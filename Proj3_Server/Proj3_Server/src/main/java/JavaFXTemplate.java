import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/*
 * class JavaFXTemplate
 * 
 * GUI interface for the server. Contains views about the server
 * status and each of its clients. How many clients are connected etc
 * 
 * @author Alex Escatel
 * @version 11-21-21
 */

public class JavaFXTemplate extends Application {
	
	HashMap<String, Scene> sceneMap;
	
	// Server port selection elements
	HBox inputElements, buttonHBox;
	VBox ssRoot;
	Label portLabel;
	TextField portInput;
	Button startButton, exitButton;
	// End of server port selection elements
	
	// Server window elements
	VBox swRoot, serverDataBox, topBox;
	HBox controlsBox;
	Label numConnected, portOn;
	ListView<String> gameInfo;
	Button stopButton;
	//End of server window elements
	
	Server serverConnection;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	//feel free to remove the starter code from this method
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("Welcome to JavaFX");
		
		sceneMap = new HashMap<>();
		
		sceneMap.put("start", SelectionScene(primaryStage));
		sceneMap.put("server", ServerWindow(primaryStage));
				
		primaryStage.setScene(sceneMap.get("start"));
		primaryStage.show();
	}
	
	public Scene SelectionScene(Stage stage)
	{
		portLabel = new Label("Port:");
		portInput = new TextField();
		portLabel.setStyle("-fx-font-size: 16;");
		portLabel.setAlignment(Pos.CENTER);
		portLabel.setPrefWidth(90);
		portInput.setPrefWidth(200);
		portInput.setStyle("-fx-font-size: 16;");
		inputElements = new HBox(15, portLabel, portInput);
		inputElements.setAlignment(Pos.CENTER);
		inputElements.setPadding(new Insets(50, 0, 0, 0));
		
		startButton = new Button("Start Server");
		startButton.setStyle("-fx-font-size: 16;");
		startButton.setPrefSize(130, 40);
		
		exitButton = new Button("Exit");
		exitButton.setStyle("-fx-font-size: 16;");
		exitButton.setPrefSize(80, 40);
		
		exitButton.setOnAction(e -> { stage.close(); });
		
		startButton.setOnAction(e -> {
			try {
				serverConnection = new Server(portInput.getText(), 
					data->{
							Platform.runLater(()->{ gameInfo.getItems().add(data.toString()); });
					},
					
					clients->{
							Platform.runLater(()-> { numConnected.setText( "Clients connected: " + clients.toString()); });
					}
				);
				
				portOn.setText("Port: " + portInput.getText());
				
				stage.setScene(sceneMap.get("server"));
			}
			catch(Exception q)
			{}
		});
		
		buttonHBox = new HBox(75, startButton, exitButton);
		buttonHBox.setAlignment(Pos.CENTER);
		
		
		ssRoot = new VBox(25, inputElements, buttonHBox);
		ssRoot.setFillWidth(true);
		
		return new Scene(ssRoot ,500,230);
	}
	
	public Scene ServerWindow(Stage stage)
	{
		numConnected = new Label("Clients connected: 0");
		portOn = new Label("Port listening on: ");
		numConnected.setStyle("-fx-font-size: 18;");
		portOn.setStyle("-fx-font-size: 18;");
		numConnected.setPadding(new Insets(10, 0, 0, 10));
		portOn.setPadding(new Insets(0, 0, 25, 10));
		topBox = new VBox(15, numConnected, portOn);
		
		gameInfo = new ListView<>();
		gameInfo.setPrefHeight(550);
		serverDataBox = new VBox(gameInfo);
		VBox.setMargin(gameInfo, new Insets(0, 10, 0, 10));
		
		stopButton = new Button("Stop Server");
		stopButton.setStyle("-fx-font-size: 18;");
		stopButton.setPrefSize(140, 45);
		
		stopButton.setOnAction(e -> { stage.close(); });
		
		controlsBox = new HBox(stopButton);
		HBox.setMargin(stopButton, new Insets(10, 0, 10, 10));
		
		swRoot = new VBox(5, topBox, serverDataBox, controlsBox);
		
		return new Scene(swRoot, 750,750);
	}
}
