import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

/*
 * class Server
 * 
 * A class representation of the game server. This contains multiple
 * nested classes for the main server thread, and server-side client threads
 * used for establishing connections between clients and their server
 * 
 * @author Alex Escatel
 * @version 11-21-21
 */

public class Server {
	
	private int port;
	int count = 1;
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	Queue<Integer> recClientNums = new LinkedList<>();
	
	ServerThread theServer;
	
	private Consumer<Serializable> callback;
	private Consumer<Serializable> updateClientsLabel;
	
	Server(String p, Consumer<Serializable> call, Consumer<Serializable> clientLabel) 
	{
		callback = call;
		updateClientsLabel = clientLabel;
		
		theServer = new ServerThread();
		port = Integer.parseInt(p);
		
		
		theServer.setDaemon(true);
		theServer.start();
	}
	
	public class ServerThread extends Thread {
		
		// start server and listen for clients
		public void run()
		{
			try(ServerSocket mysocket = new ServerSocket(port);){
				System.out.println("Server is waiting for clients . . .");
				
				int nextClientNum;
				
				while(true)
				{
					if(recClientNums.isEmpty())
					{
						nextClientNum = count;
						count++;
					}
					else
						nextClientNum = recClientNums.remove();
					
					ClientThread c = new ClientThread(mysocket.accept(), nextClientNum);
					callback.accept("Client #" + nextClientNum + ": New client connected");
					clients.add(c);
					c.start();
					
					updateClientsLabel.accept(clients.size());
				}
			}
			catch(Exception e)
			{
				callback.accept("Server socket did not launch");
			}
		}
	}
	
	public class ClientThread extends Thread{
		
		Socket connection;
		int clientId;
		ObjectInputStream in;
		ObjectOutputStream out;
		
		boolean gameState;
		BaccaratGame game;
		
		public ClientThread(Socket s, int count)
		{
			this.connection = s;
			this.clientId = count;
			gameState = false;
		}
		
		public void run()
		{	
			try {
				in = new ObjectInputStream(connection.getInputStream());
				out = new ObjectOutputStream(connection.getOutputStream());
				connection.setTcpNoDelay(true);
			}
			catch(Exception e)
			{
				System.out.println("Streams not open");
			}
			
			BaccaratInfo gameData;
			
			while(true)
			{
				try
				{
					gameData = (BaccaratInfo) in.readObject();		// read in wager data
					
					callback.accept("Client #" + clientId + " started a new hand");
					
					game = new BaccaratGame();
					game.setBet(gameData.getBet());
					game.setWager(gameData.getWager());
					
					callback.accept("Client #" + clientId + ": bet received for $" + game.getWager() + " on " + game.getBetStr());
					
					gameState = true;
					
					game.theDealer.dealPlayer();													// deal hands
					game.theDealer.dealBanker();					
					
					gameData.setPHandValue( BaccaratGameLogic.handTotal( game.getPlayerHand() ) );	// calculate pHand val and add to baccInfo
					gameData.setBHandValue( BaccaratGameLogic.handTotal( game.getBankerHand() ) );	// calculate bHand send to baccInfo
					
					gameData.setPlayerCardFaces( game.getCardStrings( game.getPlayerHand() ) );		// add faces to a string array for client to display
					gameData.setBankerCardFaces( game.getCardStrings( game.getBankerHand() ) );		// add faces to a string array
					
					out.writeObject(gameData);
					gameData.clearFaceArrays();
					out.reset();
					
					//try{ Thread.sleep(1500); } catch(Exception q) {};
					
					
					if(BaccaratGameLogic.checkWinCond(game.getPlayerHand(), game.getBankerHand()))
					{
						System.out.println("Server: Natural");
						gameData.setNatural(true);
						gameState = false;
					}
					
					// loop here
					while(gameState)
					{
						System.out.println("Server: top of game loop");
						// check for any valid card draws
						Card playerDraw = null;
						Card bankerDraw = null;
						gameData.clearFaceArrays();
						
						if(BaccaratGameLogic.evaluatePlayerDraw(game.getPlayerHand()))
						{
							System.out.println("Server: draw cards for player");
							playerDraw = game.dealOneToPlayer();
							gameData.addPlayerCardFace(playerDraw.getFile());
							gameData.setPHandValue( BaccaratGameLogic.handTotal( game.getPlayerHand() ) );
						}
						
						if(BaccaratGameLogic.evaluateBankerDraw(game.getBankerHand(), playerDraw))
						{
							System.out.println("Server: draw cards for banker");
							bankerDraw = game.dealOneToBanker();
							gameData.addBankerCardFace(bankerDraw.getFile());
							gameData.setBHandValue( BaccaratGameLogic.handTotal( game.getBankerHand() ) );
						}
						else if(playerDraw == null || (BaccaratGameLogic.checkWinCond(game.getPlayerHand(), game.getBankerHand())))				// neither the player nor the banker drew
						{
							gameState = false;
							break;
						}

						System.out.println("Server: send new card data");
						
						//try{ Thread.sleep(3000); } catch(Exception q) {};
						
						out.writeObject(gameData);
						out.reset();
					}	// repeat
					
					
					
					
					// Once no one can draw a card, check for a winner
					System.out.println("Server: check for winner");
					String winner = BaccaratGameLogic.whoWon(game.getPlayerHand(), game.getBankerHand());
					
					gameData.setWinCond(true);						// Winner found
					gameData.setWinner(winner);						// Who was the winner
					game.setWinner(gameData.getWinner());		
					
					if(gameData.getNatural())
					{
						callback.accept("Client #" + clientId + ": " + gameData.getWinner() + " Won with a natural");
					}
					
					double pWinnings = game.evaluateWinnings();		// How much did the client win
					gameData.setWinnings(pWinnings);
					gameData.setWinMsg(game.getWinMsg());			// Message to send back to the clientside user
					
					callback.accept("Client #" + clientId + ": " + game.getServMsg());	// pushes win data to the server console
					
					System.out.println("Server: write final object containing win data");
					
					try{ Thread.sleep(3000); } catch(Exception q) {};		// pause this thread for 3s
					out.writeObject(gameData);								// write game data back to client
					out.reset();
					
					game.dumpHands();
					
					game = null;											// reset game
					gameData = null;
					
				} catch(Exception e) {
					e.printStackTrace();
			    	callback.accept("Client #" + clientId + ": Disconnected from server");
			    	clients.remove(this); 										// shifts all clients to the left
			    	recClientNums.add(clientId);
			    	updateClientsLabel.accept(clients.size());
			    	break;
				}
			}
		}
		
		public void bInfoDisp(BaccaratInfo data)
		{
			System.out.println();
			System.out.println("----------- Printing BaccaratInfo Data -----------");
			
			
			System.out.println("Player hand value: " + data.getPHandValue());
			System.out.println("Banker hand value: " + data.getBHandValue());
			System.out.println("Current wager: " + data.getWager());
			System.out.println("Current bet: " + data.getBet());
			System.out.println("Win cond: " + data.getWinCond());
			System.out.println("Winner: " + data.getWinner());
			
			
			System.out.print("Player Cards: ");
			for(String p : data.getPlayerFaces())
			{
				System.out.print(p + " ");
			}
			
			System.out.println();
			
			System.out.print("Banker Cards: ");
			for(String b : data.getBankerFaces())
			{
				System.out.print(b + " ");
			}
			
			System.out.println();
		
			System.out.println("----------- End of BaccaratInfo Data -----------");
		}
	}
	
	
}
