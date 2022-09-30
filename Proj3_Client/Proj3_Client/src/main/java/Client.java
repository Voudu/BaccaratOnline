import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.function.Consumer;

/*
 * class Client extends Thread
 * 
 * Threaded client-side program which connected to baccarat game server
 * contains gui and functionality to update gui.
 * 
 * @author Alex Escatel
 * @version 11-25-21
 */

public class Client extends Thread{
	
	Socket socketClient;
	Double cash;
	Double earnings;
	
	String ipAddress;
	int port;
	
	ObjectOutputStream out;
	ObjectInputStream in;
	
	private Consumer<Serializable> callback;		// used to update the listView
	private Consumer<Serializable> updateBankerCards;
	private Consumer<Serializable> updateBankerHandVal;
	private Consumer<Serializable> updatePlayerCards;
	private Consumer<Serializable> updatePlayerHandVal;
	private Consumer<Serializable> updateActionIndicator;
	private Consumer<Serializable> updateCash;
	private Consumer<Serializable> updateEarnings;
	private Consumer<Serializable> resetSignal;
	
	boolean gameStarted;
	DecimalFormat df = new DecimalFormat("0.00");
	
	
	public Client(String ip, int p, Consumer<Serializable> call, Consumer<Serializable> ubCards, Consumer<Serializable> ubHvals,
			Consumer<Serializable> upCards, Consumer<Serializable> upHvals, Consumer<Serializable> act, Consumer<Serializable> upCash,
			Consumer<Serializable> upEarn, Consumer<Serializable> reset)
	{
		ipAddress = ip;
		port = p;
		
		gameStarted = false;
		
		cash = 10000.00;
		earnings = 0.00;
		
		String.format("%.2f", cash);
		String.format("%.2f", earnings);
		
		callback = call;
		updateBankerCards = ubCards;
		updateBankerHandVal = ubHvals;
		
		updatePlayerCards = upCards;
		updatePlayerHandVal = upHvals;
		
		updateCash = upCash;
		updateEarnings = upEarn;
		
		updateActionIndicator = act;
		resetSignal = reset;
		
		upCash.accept(this.cash);
		upEarn.accept(this.earnings);
		
	}
	
	public void run()
	{
		try {
			socketClient = new Socket(ipAddress, port);
		    out = new ObjectOutputStream(socketClient.getOutputStream());
		    in = new ObjectInputStream(socketClient.getInputStream());
		    socketClient.setTcpNoDelay(true);
		    
		    callback.accept("Connected to server on ipAddress: " + ipAddress + "; and port: " + port);
		    
			}
			catch(Exception e) {}
		
			while(true) {
				
				BaccaratInfo gameData = new BaccaratInfo(0, 0);
				
				try {
					gameData = (BaccaratInfo) in.readObject();
					
					System.out.println("Object read");
					//bInfoDisp(gameData);		// for debugging, displays data passed back from server
					
					if(!gameData.getWinCond())
					{
						callback.accept("Dealing cards");
						updateUi(gameData);
					}
					else if(gameData.getWinCond())
					{
						int winner = gameData.getWinner();
						
						if(gameData.getNatural())
						{
							callback.accept(winnerToStr(winner) + " won with a natural!");
							updateActionIndicator.accept(winnerToStr(winner) + " pulled a natural!");
						}
						
						// updates ui and game data based on winner
						switch(winner) {
							case 0:
								System.out.println("Player wins!");
								callback.accept("Player wins with a hand of " + gameData.getPHandValue() + " vs banker's hand of " + gameData.getBHandValue());
								updateActionIndicator.accept("Player wins!");
								this.cash += gameData.getWinnings();
								this.earnings += gameData.getWinnings();
								break;
							case 1: 
								System.out.println("Banker wins!");
								callback.accept("Banker wins with a hand of " + gameData.getBHandValue() + " vs player's hand of " + gameData.getPHandValue());
								updateActionIndicator.accept("Banker wins!");
								this.cash += gameData.getWinnings();
								this.earnings += gameData.getWinnings();
								break;
							case 2:
								System.out.println("Tie!");
								callback.accept("Draw.");
								updateActionIndicator.accept("Tie game, No winners.");
								this.cash += gameData.getWinnings();
								this.earnings += gameData.getWinnings();
								break;
							default:
								break;
						}
						
						updateUi(gameData);
						
						callback.accept(gameData.getwinMsg());		// print winner message to gui
						
						// Update ui with win/loss
						updateCash.accept(cash);
						updateEarnings.accept(earnings);
						
						Thread.sleep(4000);							// pause thread for 4s
						updateActionIndicator.accept("Play again? Press the button below.");
						resetGame();								// allow the new game button to be pressed
					}
					
				} catch(Exception s) {
					s.printStackTrace();
				}
			}
	}
	
	public void resetGame()			// allows the play again button to be pressed
	{
		resetSignal.accept(1);
	}
	
	// helper
	public String winnerToStr(int winner)
	{
		if(winner == 0)
			return "Player";
		else if(winner == 1)
			return "Banker";
		else
			return "null";
	}
	
	// helper
	public boolean checkWager(double wager)
	{
		if(wager < 0 || wager > cash)
			return false;
		else
			return true;
	}
	
	// send bet to the server
	public void sendAction(double wager, int bet)
	{
		try
		{
			BaccaratInfo gameData = new BaccaratInfo(wager, bet);
			
			gameStarted = true;
			out.writeObject(gameData);
			out.reset();
		}
		catch(Exception e) {}
	}
	
	// place cards on screen and hand values
	public void updateUi(BaccaratInfo game)
	{
		try
		{
			if(!game.getPlayerFaces().isEmpty())
			{
				updatePlayerCards.accept(game.getPlayerFaces());
				updatePlayerHandVal.accept("Hand Value: " + game.getPHandValue());		
				updateActionIndicator.accept("Player's cards dealt");
			}
			Thread.sleep(2000);
			if(!game.getBankerFaces().isEmpty())
			{
				updateBankerCards.accept(game.getBankerFaces());
				updateBankerHandVal.accept("Hand Value: " + game.getBHandValue());
				updateActionIndicator.accept("Banker's cards dealt");
			}
			
		} catch(Exception e) {
			
			System.out.println("Dealing interuppted");
		}
	}
}
