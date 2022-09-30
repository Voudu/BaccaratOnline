import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/*
 * class JavaFXTemplate
 * 
 *  baccarat client GUI, uses Client.java to communicate to server
 *  this is the GUI representation of the user's game
 *  
 *  @author Alex Escatel
 *  @version 11-21-21
 */


public class JavaFXTemplate extends Application {

	HashMap<String, Scene> sceneMap;
	
	// Start scene UI elements
	HBox ipHBox, portHBox, buttonHBox;
	VBox startRoot;
	Label ipLabel, portLabel;
	TextField ipInput, portInput;
	Button connectButton, exitButton;
	// End of start scene UI elements
	
	
	// Game UI
	BorderPane root;
	VBox bettingBox, gameBox, centerBox;
	Label winLossLabel, earningsLabel, yourMoneyLabel, cashLabel, wagerLabel, betLabel, actionLabel;
	TextField wagerField;
	
	ToggleGroup betGroup;
	RadioButton playerBet, bankerBet, drawBet;
	
	Button placeBetButton, playAgainButton, quitButton;
	HBox bankersHand, playersHand, buttonBox;
	
	HBox playerInfoHeaderBox, betHeaderBox, wagerBox, betSelectBox,
		 actionLabelBox, cashBox, earningsBox;
	Label playerInfoLabel, betHeaderLabel,
		  bHandValueLabel, pHandValueLabel;
	
	HBox gameInfoBox;
	ListView<String> gameInfo = new ListView<>();
	
	DecimalFormat df = new DecimalFormat("0.00");
	
	// End of Game UI
	
	Client userClient;
	ArrayList<String> newCards;
	boolean turnStatus; // false = bankers turn , true = players turn
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
		
		Platform.exit();
		System.exit(0);
	}

	//feel free to remove the starter code from this method
	@Override
	public void start(Stage primaryStage) throws Exception {
		sceneMap = new HashMap<>();
		sceneMap.put("start", createStartScene(primaryStage));
		sceneMap.put("game", createGameScene(primaryStage));
		
		newCards = new ArrayList<>();
		turnStatus = false;
		primaryStage.setScene(sceneMap.get("start"));
		primaryStage.show();		
	}
	
	@SuppressWarnings("unchecked")
	public Scene createStartScene(Stage stage)
	{
		stage.setTitle("Connect to Baccarat Server");
		
		ipLabel = new Label("IP Adress:");
		ipLabel.setStyle("-fx-font-size: 18;");
		ipInput = new TextField();
		ipInput.setPrefWidth(250);
		ipInput.setStyle("-fx-font-size: 16;");
		portLabel = new Label("Port:");
		portLabel.setStyle("-fx-font-size: 18;");
		portInput = new TextField();
		portInput.setPrefWidth(250);
		portInput.setStyle("-fx-font-size: 16;");
		
		connectButton = new Button("Connect");
		connectButton.setPrefSize(125, 40);
		exitButton = new Button("Exit");
		exitButton.setPrefSize(70, 40);
		
		ipHBox = new HBox(10, ipLabel, ipInput);
		portHBox = new HBox(10, portLabel, portInput);
		buttonHBox = new HBox(45, connectButton, exitButton);	
		

		
		ipHBox.setPrefWidth(500);
		portHBox.setPrefWidth(500);
		buttonHBox.setPrefWidth(500);
		ipLabel.setPrefWidth(100);
		portLabel.setPrefWidth(100);
		
		connectButton.setAlignment(Pos.CENTER);
		connectButton.setStyle("-fx-font-size: 18;");
		exitButton.setAlignment(Pos.CENTER);
		exitButton.setStyle("-fx-font-size: 18;");
		
		ipHBox.setAlignment(Pos.CENTER);
		portHBox.setAlignment(Pos.CENTER);
		buttonHBox.setAlignment(Pos.CENTER);
		ipInput.setAlignment(Pos.CENTER_LEFT);
		ipInput.setText("127.0.0.1");
		
		portInput.setAlignment(Pos.CENTER_LEFT);
		ipLabel.setAlignment(Pos.CENTER_LEFT);
		portLabel.setAlignment(Pos.CENTER_LEFT);
		
		// Top Right Bottom Left
		ipHBox.setPadding(new Insets(25, 25, 0, 25));
		portHBox.setPadding(new Insets(0, 25, 0, 25));
		buttonHBox.setPadding(new Insets(35, 25, 0, 25));
		
		startRoot = new VBox(15, ipHBox, portHBox, buttonHBox);
		startRoot.setFillWidth(true);
		
		connectButton.setOnAction(e -> {
			// take down ip address, ip address
			userClient = new Client(
					ipInput.getText(),
					Integer.parseInt(portInput.getText()),
					data->{ Platform.runLater( ()->{ gameInfo.getItems().add( data.toString() ); } );},
					bCards->{ Platform.runLater( ()->{ addCardsToView( (ArrayList<String>) bCards, false ); } );},
					bhVal->{ Platform.runLater( ()->{ bHandValueLabel.setText( bhVal.toString() ); } );},
					pCards->{ Platform.runLater( ()->{ addCardsToView( (ArrayList<String>) pCards, true ); } );},
					phVal->{ Platform.runLater( ()->{ pHandValueLabel.setText( phVal.toString() ); } );},
					act->{ Platform.runLater( ()->{ actionLabel.setText( act.toString() ); } );},
					uCash->{ Platform.runLater( ()->{ cashLabel.setText( "$"+ df.format(uCash).toString() ); } );},
					uEarn->{ Platform.runLater( ()->{ winLossLabel.setText( "$"+ df.format(uEarn).toString() ); } );},
					reset->{ Platform.runLater( ()->{ playAgainButton.setDisable(false); } );}
			);
		
			// attempt connection
			try 
			{ 
				userClient.start();
				stage.setScene(sceneMap.get("game"));
			}
			catch(Exception e1)
			{
				System.out.println("Server does not exist at this Ip address and port");
			}
			
		});
		
		exitButton.setOnAction(e -> {
			stage.close();
		});
		
		return new Scene(startRoot, 450, 225);
	}
	
	public void addCardsToView(ArrayList<String> cards, boolean whoIs)
	{	
		turnStatus = whoIs;
				
		for (String c : cards)
		{
			c = "JPEG/" + c;
			//System.out.println(c);				// debugging
			newCards.add(c);
			
			if(turnStatus)
			{
				CardView newCard = new CardView(c);
				addToView(playersHand, newCard);
			}
			else
			{
				CardView newCard = new CardView(c);
				addToView(bankersHand, newCard);
			}
		}
	}
	
	public void addToView(HBox hand, CardView card)
	{
		hand.getChildren().add(card);
	}
		
	public Scene createGameScene(Stage stage)
	{
		stage.setTitle("Play Baccarat");
		stage.setResizable(false);
		root = new BorderPane();
		
		
		// BORDER PANE LEFT
			// Player Data Box
			playerInfoLabel = new Label("Player Data");
			playerInfoHeaderBox = new HBox(playerInfoLabel);
			playerInfoHeaderBox.setStyle("-fx-border-color: #000000;");
			playerInfoLabel.setAlignment(Pos.CENTER);
			playerInfoLabel.setStyle("-fx-font-size: 24;");
			playerInfoHeaderBox.setPrefWidth(246);
			playerInfoHeaderBox.setAlignment(Pos.CENTER);
			bettingBox = new VBox(20, playerInfoHeaderBox);
			bettingBox.setPrefWidth(256);
			bettingBox.setPadding(new Insets(10, 0, 10, 0));
			
			yourMoneyLabel = new Label("Cash: ");		// update to get cash from client data
			earningsLabel = new Label("Earnings: ");	// update based on win or loss
			
			cashLabel = new Label("$" );
			winLossLabel = new Label("$");
			cashLabel.setStyle("-fx-font-size: 14;");
			earningsLabel.setStyle("-fx-font-size: 14;");
			
			cashBox = new HBox(5, yourMoneyLabel, cashLabel);
			earningsBox = new HBox(5, earningsLabel, winLossLabel);
			
			yourMoneyLabel.setStyle("-fx-font-size: 14;");
			earningsLabel.setStyle("-fx-font-size: 14;");
			bettingBox.getChildren().addAll(cashBox, earningsBox);
			// end of Player Data Box
			
			// Place a Bet Box
			betHeaderLabel = new Label("Place a Bet");
			betHeaderBox = new HBox(betHeaderLabel);
			betHeaderBox.setStyle("-fx-border-color: #000000;");
			betHeaderLabel.setStyle("-fx-font-size: 24;");
			betHeaderBox.setPrefWidth(246);
			betHeaderBox.setAlignment(Pos.CENTER);
			
			wagerLabel = new Label("Wager:");
			wagerField = new TextField();
			wagerField.setPrefWidth(175);
			wagerBox = new HBox(10, wagerLabel, wagerField);
			wagerBox.setPadding(new Insets(0, 0, 25, 0));
			
			betLabel = new Label("Bet:");
			playerBet = new RadioButton("Player");
			bankerBet = new RadioButton("Banker");
			drawBet = new RadioButton("Tie");
			betGroup = new ToggleGroup();
			playerBet.setToggleGroup(betGroup);
			bankerBet.setToggleGroup(betGroup);
			drawBet.setToggleGroup(betGroup);
			betSelectBox = new HBox(10, betLabel, playerBet, bankerBet, drawBet);
			
			playerBet.setSelected(true);
			
			placeBetButton = new Button("Place Bet");
			placeBetButton.setStyle("-fx-font-size: 18;");
			placeBetButton.setPrefSize(246, 50);
			VBox.setMargin(placeBetButton, new Insets(245, 0, 15, 0));
			
			placeBetButton.setOnAction(e -> {
				
				int bet = 0;
				
				try {
					double wager = Double.parseDouble(wagerField.getText());
					
					String betee = "";
					
					if(playerBet.isSelected())
					{
						bet = 0;
						betee = "yourself";
					}
						
					else if(bankerBet.isSelected())
					{
						bet = 1;
						betee = "banker";
					}
					else if(drawBet.isSelected())
					{
						bet = 2;
						betee = "tie";
					}
					
					if(userClient.checkWager(wager))
					{
						playAgainButton.setDisable(true);
						placeBetButton.setDisable(true);
						playerBet.setDisable(true);
						bankerBet.setDisable(true);
						drawBet.setDisable(true);
						wagerField.setDisable(true);
						
						gameInfo.getItems().add("Wager of $" + wager + " placed on " + betee + " accepted.");
						actionLabel.setText("Bet placed, waiting for banker");
						
						
						userClient.sendAction(wager, bet);
					}
					else
					{
						gameInfo.getItems().add("Invalid wager or bet. Try again.");
					}
					
				} catch(NumberFormatException n) {
					
					gameInfo.getItems().add("Please enter the number as a wager.");
				}
			});
			
			
			bettingBox.getChildren().addAll(betHeaderBox, wagerBox, betSelectBox, placeBetButton);
			// end of Place a Bet Box
			
			bettingBox.setStyle("-fx-border-color: #000000;");
			bettingBox.setPadding(new Insets(15, 15, 15, 15));
			BorderPane.setMargin(bettingBox, new Insets(5, 5, 5, 5));
			VBox.setMargin(betHeaderBox, new Insets(75, 0, 5, 0));
			root.setLeft(bettingBox);
		// END OF BORDER PANE LEFT
		
		
		
		
		// BORDER PANE CENTER
			// gameplay center
			bankersHand = new HBox(5);
			bankersHand.setPrefHeight(265);
			playersHand = new HBox(5);
			playersHand.setPrefHeight(265);
			
			bankersHand.setStyle("-fx-border-color: #000000;"+"-fx-background-color: transparent;");
			playersHand.setStyle("-fx-border-color: #000000;"+"-fx-background-color: transparent;");
			
			actionLabel = new Label("Waiting for player's action");
			actionLabelBox = new HBox(actionLabel);
			actionLabel.setPrefWidth(1000);
			actionLabel.setStyle("-fx-font-size: 18;"+"-fx-font-weight: bold;");
			actionLabel.setAlignment(Pos.CENTER);
			
			VBox.setMargin(actionLabelBox, new Insets(20, 0, 20, 0));
			
			bHandValueLabel = new Label("Hand Value: 0");
			pHandValueLabel = new Label("Hand Value: 0");
			
			bHandValueLabel.setStyle("-fx-text-fill: #FFFFFF;"+"-fx-font-size: 14;");
			pHandValueLabel.setStyle("-fx-text-fill: #FFFFFF;"+"-fx-font-size: 14;");
			
			gameBox = new VBox(5, bankersHand, bHandValueLabel, actionLabelBox,
							   pHandValueLabel, playersHand);
			
			
			// end of center gameplay
			
			
			// buttons at bottom center
			playAgainButton = new Button("Play Again");
			playAgainButton.setStyle("-fx-font-size: 18");
			playAgainButton.setPrefSize(175, 35);
			
			playAgainButton.setDisable(true);
			
			quitButton = new Button("Quit");
			quitButton.setStyle("-fx-font-size: 18;");
			quitButton.setPrefSize(100, 35);
			buttonBox = new HBox(25, playAgainButton, quitButton);
			
			playAgainButton.setOnAction(q -> {
				
				gameInfo.getItems().add("Table reset, place your bet.");
				
				playAgainButton.setDisable(true);
				
				placeBetButton.setDisable(false);
				playerBet.setDisable(false);
				bankerBet.setDisable(false);
				drawBet.setDisable(false);
				wagerField.setDisable(false);
				
				bHandValueLabel.setText("Hand Value: 0");
				pHandValueLabel.setText("Hand Value: 0");
				
				actionLabel.setText("Waiting for player's action");
				
				bankersHand.getChildren().clear();
				playersHand.getChildren().clear();
			});
			
			quitButton.setOnAction(e -> { stage.close(); });
		
			buttonBox.setPrefHeight(50);
			buttonBox.setAlignment(Pos.CENTER_LEFT);
			
			centerBox = new VBox(5, gameBox, buttonBox);
			// end of bottom center buttons
			
			centerBox.setPadding(new Insets(15, 15, 15, 15));
			centerBox.setStyle("-fx-border-color: #000000;");
			BorderPane.setMargin(centerBox, new Insets(5, 5, 5, 5));
			root.setCenter(centerBox);
		// END OF BORDER PANE CENTER
		
		
		
		// BORDER PANE RIGHT
			gameInfoBox = new HBox(gameInfo);
			gameInfoBox.setFillHeight(true);
			
			gameInfoBox.setPadding(new Insets(7, 7, 7, 7));
			gameInfoBox.setStyle("-fx-border-color: #000000;");
			BorderPane.setMargin(gameInfoBox, new Insets(5, 5, 5, 5));
			root.setRight(gameInfoBox);
		// END OF BORDER PANE RIGHT
		
		BackgroundSize backgroundSize = new BackgroundSize(1700, 712, true, true, true, false);
		BackgroundImage img = new BackgroundImage(new Image("table-texture.png"), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
		centerBox.setBackground(new Background(img));
		
		return new Scene(root, 1700, 720);
	}
	
}
