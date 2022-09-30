import java.io.Serializable;
import java.util.ArrayList;

/*
 * class BaccaratInfo implemenets Serializable
 * 
 * serializable class which is used to communicate game data between the client
 * and the baccarat game server
 * 
 * @author Alex Escatel
 * @version 10-25-21
 */

public class BaccaratInfo implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		int pHandValue;
		int bHandValue;
		double wager;
		double winnings;
		boolean winCond;
		String winMsg;
		boolean natural;
		
		ArrayList<String> playerCardFaces;
		ArrayList<String> bankerCardFaces;
		
		int bet; 	// 0 = player, 1 = banker, 2 = tie
		int winner; // same # as bet

		public BaccaratInfo(double w, int b)
		{
			this.wager = w;
			this.bet = b;
			this.winner = -1;
			this.winnings = 0.0;
			winCond = false;
			winMsg = "null";
			natural = false;
			
			playerCardFaces = new ArrayList<>();
			bankerCardFaces = new ArrayList<>();
		}
		
		public void setNatural(boolean tf)
		{
			natural = tf;
		}
		
		public boolean getNatural()
		{
			return natural;
		}
		
		public void setWinMsg(String msg)
		{
			winMsg = msg;
		}
		
		public String getwinMsg()
		{
			return winMsg;
		}
		
		public void clearFaceArrays()
		{
			playerCardFaces.clear();
			bankerCardFaces.clear();
		}
		
		public void setWinnings(double w)
		{
			winnings = w;
		}
		
		public double getWinnings()
		{
			return winnings;
		}
		
		public void setWinner(String win)
		{
			if(win == "Tie")
				winner = 2;
			else if(win == "Player")
				winner = 0;
			else if(win == "Banker")
				winner = 1;
		}
		
		public void setWinCond(boolean tf)
		{
			winCond = tf;
		}
		
		public boolean getWinCond()
		{
			return winCond;
		}
		
		public int getWinner()
		{
			return winner;
		}
		
		public double getWager()
		{
			return wager;
		}
		
		public int getBet()
		{
			return bet;
		}
		
		public void setPHandValue(int val)
		{
			pHandValue = val;
		}
		
		public void setBHandValue(int val)
		{
			bHandValue = val;
		}
		
		public void setPlayerCardFaces(ArrayList<String> p)
		{
			playerCardFaces = p;
		}
		
		public void addPlayerCardFace(String f)
		{
			playerCardFaces.add(f);
		}
		
		public void setBankerCardFaces(ArrayList<String> b)
		{
			bankerCardFaces = b;
		}

		public void addBankerCardFace(String f)
		{
			bankerCardFaces.add(f);
		}
		
		public int getPHandValue()
		{
			return pHandValue;
		}
		
		public int getBHandValue()
		{
			return bHandValue;
		}
		
		public ArrayList<String> getPlayerFaces()
		{
			return playerCardFaces;
		}
		
		public ArrayList<String> getBankerFaces()
		{
			return bankerCardFaces;
		}
	}