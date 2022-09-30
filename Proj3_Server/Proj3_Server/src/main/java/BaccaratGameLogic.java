import java.util.ArrayList;

/*
 * static class BaccaratGameLogic
 * 
 * class containing all of the logic required to play a game of baccarat. 
 * 
 * @author Alex Escatel
 * @version 11-21-21
 */


public class BaccaratGameLogic {
	
	public static String whoWon(ArrayList<Card> hand1, ArrayList<Card> hand2)
	{
		// hand 1 belongs to banker
		// hand 2 belongs to player
		
		int hand1Val = handTotal(hand1);
		int hand2Val = handTotal(hand2);
		
		if(hand1Val == hand2Val)
			return "Tie";
		else if(hand1Val > hand2Val)
			return "Player";
		else
			return "Banker";
	}
	
	public static boolean checkWinCond(ArrayList<Card> hand1, ArrayList<Card> hand2)
	{
		if(handTotal(hand1) >= 8 || handTotal(hand2) >= 8)
			return true;
		return false;
	}
		
	public static int handTotal(ArrayList<Card> hand)
	{
		int value = 0;
		
		for (int i=0; i < hand.size(); i++)
		{
			value += hand.get(i).getValue();
		}
		
		if(value > 9)
		{
			String modify = Integer.toString(value);
			String[] modArr = modify.split("");
			modify = modArr[1];
			value = Integer.parseInt(modify);
		}
		
		return value;
	}
	
	public static boolean evaluateBankerDraw(ArrayList<Card> hand, Card playerCard)
	{
		int hTot = handTotal(hand);
		
		if(hTot >= 7)
			return false;
		
		else if(playerCard != null)
		{
			if(hTot <= 2)
				return true;
			else if(hTot == 3 && (playerCard.getValue() == 9 || playerCard.getValue() <= 7))
				return true;
			else if(hTot == 4 && (playerCard.getValue() >= 2 && playerCard.getValue() <= 7))
				return true;
			else if(hTot == 5 && (playerCard.getValue() >= 4 && playerCard.getValue() <= 7))
				return true;
			else if(hTot == 6 && (playerCard.getValue() == 6 || playerCard.getValue() == 7))
				return true;
			else
				return false;
		}
		
		else
		{
			if(playerCard == null && hTot <= 5)
				return true;
			else
				return false;
		}
	}
	
	public static boolean evaluatePlayerDraw(ArrayList<Card> hand)
	{
		if(handTotal(hand) >= 6)
			return false; 

		return true;
	}
}
