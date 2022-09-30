import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

/*
 * class BaccaratGame
 * 
 * game instance for each player on the server
 * each client has their own instance of this class. Contains information about their bet,
 * winnings, and hand.
 * 
 * @author Alex Escatel
 * @version 11-21-21
 */


public class BaccaratGame {
	
	ArrayList<Card> playerHand;
	ArrayList<Card> bankerHand;
	BaccaratDealer theDealer;
	double currentBet;	// wager
	int bet;			// bet on
	String betAsStr;
	double totalWinnings;
	int winner;
	String winMsg;
	String servMsg;
	
	DecimalFormat df = new DecimalFormat("0.00");
	
	public BaccaratGame()
	{
		theDealer = new BaccaratDealer();
		totalWinnings = 0;
		bet = -1;
		winner = -1;
		winMsg = null;
		betAsStr = null;
		servMsg = null;
		currentBet = 0.0;
		playerHand = new ArrayList<Card>();
		bankerHand = new ArrayList<Card>();
		
		df.format(currentBet);
		df.format(totalWinnings);
	}
	
	// for debugging, prints hand data to console
	public void dumpHands()
	{
		System.out.print("Player Hand: ");
		for(int i = 0; i < playerHand.size(); i++)
		{
			System.out.print(playerHand.get(i).getValue() + " ");
		}
		System.out.println("| hand value: " + BaccaratGameLogic.handTotal(playerHand));
		
		System.out.print("Banker Hand: ");
		for(int i = 0; i < bankerHand.size(); i++)
		{
			System.out.print(bankerHand.get(i).getValue() + " ");
		}
		System.out.println("| hand value: " + BaccaratGameLogic.handTotal(bankerHand));
		
		
	}
	
	
	// evaluates winnings based on bet
	public double evaluateWinnings()
	{
		// 0 = player, 1 = banker, 2 = tie
		if (bet == 0)
		{
			// 1:1
			if(winner == bet)
			{
				totalWinnings = currentBet;
				winMsg = ("You bet $" + currentBet + " on Player. You win!");
				servMsg =("Bet $" + currentBet + " on Player. They won $" + currentBet);
			}
			else
			{
				totalWinnings -= currentBet;
				winMsg = ("You bet $" + currentBet + " on Player. You lost your bet!");
				servMsg =("Bet $" + currentBet + " on Player. They lost $" + currentBet);
			}

		}
		else if (bet == 1)
		{
			// 1:1 bank takes 5% on of winnings
			if(winner == bet)
			{
				totalWinnings = currentBet;
				winMsg = ("You bet $" + currentBet + " on Banker. You win! The banker takes a 5% fee.");
				
				totalWinnings = totalWinnings - (totalWinnings * 0.05);		// 5% fee
				
				servMsg = ("Bet $" + currentBet + " on Banker. They won $" + currentBet + "; Bank takes a 5% fee. New winnings are " + totalWinnings);
			}
			else
			{
				totalWinnings -= currentBet;
				winMsg = ("You bet $" + currentBet + " on Banker. You lost your bet!");
				servMsg =("Bet $" + currentBet + " on Banker. They lost $" + currentBet);
			}
		}
		else	// bet == 2
		{
			// 8:1		player loses x1, or wins x8
			if(winner == 2)
			{
				totalWinnings = currentBet * 8;
				winMsg = ("You bet $" + currentBet + " on Draw. You win 8x!!");
				servMsg =("Bet $" + currentBet + " on Draw. They won $" + totalWinnings);
			}
			else
			{
				totalWinnings -= currentBet;
				winMsg = ("You bet $" + currentBet + " on Draw. You lost your bet!");
				servMsg =("Bet $" + currentBet + " on Draw. They lost $" + totalWinnings);
			}
		}
		
		return totalWinnings;
	}
	
	public void setWinner(int win)
	{
		winner = win;
	}
	
	public String getWinMsg()
	{
		return winMsg;
	}
	
	public String getServMsg()
	{
		return servMsg;
	}
	
	public void setWager(double w)
	{
		currentBet = w;
	}
	
	public double getWager()
	{
		return currentBet;
	}
	
	public String getBetStr()
	{
		switch(this.bet)
		{
		case 0:
			betAsStr = "Player";
			break;
		case 1:
			betAsStr = "Banker";
			break;
		case 2:
			betAsStr = "Draw";
			break;
		}
		
		return betAsStr;
	}
	
	public void setBet(int betOn)
	{
		bet = betOn;
	}
	
	public ArrayList<Card> getPlayerHand()
	{
		return playerHand;
	}
	
	public ArrayList<Card> getBankerHand()
	{
		return bankerHand;
	}
	
	public ArrayList<String> getCardStrings(ArrayList<Card> pArr)
	{
		ArrayList<String> handAsStrings = new ArrayList<>();
		
		for(Card c : pArr)
		{
			handAsStrings.add(c.getFile());
		}
		
		return handAsStrings;
	}
	
	public Card dealOneToPlayer()
	{
		Card temp = theDealer.drawOne();
		playerHand.add(temp);
		return temp;
	}
	
	public Card dealOneToBanker()
	{
		Card temp = theDealer.drawOne();
		bankerHand.add(temp);
		return temp;
	}

	public class BaccaratDealer {
		
		ArrayList<Card> deck;
		
		public BaccaratDealer()										// Constructor
		{
			deck = new ArrayList<Card>();
			playerHand = new ArrayList<>();
			bankerHand = new ArrayList<>();
			
			generateDeck();
		}
		
		/*
		 * generateDeck()
		 * generates a new 52 card deck
		 */
		public void generateDeck()
		{	
			
			try
			{
				File f = new File("cardOrderings.txt");
				Scanner fReader = new Scanner(f);
				String cardF;
				
				if(!deck.isEmpty())
					deck.clear();
				
				for(int i=0; i < 4; i++)
				{
					for(int j=1; j < 14; j++)
					{
						Card newCard = new Card(Integer.toString(i), j);
						
						cardF = fReader.nextLine();
						
						newCard.setFile(cardF);
				
						deck.add(newCard);			// Create a card to add to the deck.
					}
				}				
				
				shuffleDeck();
				fReader.close();
			} catch(FileNotFoundException fe) {
				System.out.println("File Exception: couldn't read from cardOrderings.txt: deck not created");
				fe.printStackTrace();
				return;
			}	
			
		}
		
		/*
		 * resetDeck()
		 * resets deck
		 */
		public void resetDeck()
		{
			deck.clear();
			generateDeck();
		}
		
		/*
		 * dealHand()
		 * deals two cards and returns an ArrayList with them
		 */
		public ArrayList<Card> dealHand()
		{
			ArrayList<Card> hand = new ArrayList<>();
			hand.add(drawOne());
			hand.add(drawOne());
			
			return hand;
		}
		
		/*
		 * drawOne()
		 * deal a single card and return it
		 */
		public Card drawOne()
		{
			Card retCard = deck.get(0);
			deck.remove(0);
			
			return retCard;
		}
		
		/*
		 * shuffleDeck()
		 * create a new 52 card deck and shuffle it
		 */
		public void shuffleDeck()
		{
			
			int swapPos1, swapPos2;
			Random rand = new Random();
			int upperLimit = 52;
			
			for(int k=0; k < 2702; k++)									// Shuffles 2 random cards 52^2 times to shuffle the entire deck
			{
				swapPos1 = rand.nextInt(upperLimit);					// Generate two random indexes used to shuffle 2 random cards
				swapPos2 = rand.nextInt(upperLimit);
				Collections.swap(deck, swapPos1, swapPos2);				// Swaps cards in two positions
			}

		}
		
		/*
		 * deckSize()
		 * returns how many cards are in the deck
		 */
		public int deckSize()
		{
			return deck.size();
		}
		
		public void dealPlayer()
		{
			playerHand = dealHand();
		}
		
		public void dealBanker()
		{
			bankerHand = dealHand();
		}
	}
}
