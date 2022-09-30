/*
 * class Card
 * 
 * represents a card to be used in the game of baccarat, standard
 * also contains the file name for each visual representation of each
 * card
 * 
 * @author Alex Escatel
 * @version 11-21-21
 */

public class Card {

	String suit;
	int value;
	
	String file;
	final String back = "Gray_back.jpg";
	
	public Card(String theSuit, int theValue)
	{	
		// Suits dont matter, this will mostly be used for displaying the correct card
		switch(theSuit) {
		
			case "0": suit = "Clubs";
					  break;
			case "1": suit = "Spades";
					  break;
			case "2": suit = "Hearts";
					  break;
			case "3": suit = "Diamonds";
					  break; 
		}
		
		// In Baccarat Aces = 1 | 10, J, Q, K = 0 | all else equal face values
		if(theValue > 9)
		{
			value = 0;
		}
		else
		{
			value = theValue;
		}
	}
	
	public void setFile(String f)
	{
		 file = f;
	}
	
	public String getFile()
	{
		return file;
	}
	
	public String getSuit()
	{
		return suit;
	}
	
	public int getValue()
	{
		return value;
	}
	
}
