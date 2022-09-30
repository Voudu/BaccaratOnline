import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MyTest {

	// define objects to use
	static ArrayList<Card> hand1 = new ArrayList<>();
	static ArrayList<Card> hand2 = new ArrayList<>();
	static ArrayList<Card> hand3 = new ArrayList<>();
	static ArrayList<Card> hand4 = new ArrayList<>();
	static ArrayList<Card> hand5 = new ArrayList<>();
	static ArrayList<Card> hand6 = new ArrayList<>();
	
	static Card[] cards = new Card[18];
	
	static BaccaratGame game = new BaccaratGame();
	static BaccaratGame game2 = new BaccaratGame();
	
	@BeforeAll
	static void setup()
	{
		cards[0] = new Card("1", 5);
		cards[1] = new Card("1", 10);
		cards[2] = new Card("2", 12);
		cards[3] = new Card("0", 2);
		cards[4] = new Card("3", 6);
		cards[5] = new Card("0", 8);
		cards[6] = new Card("1", 9);
		cards[7] = new Card("1", 4);
		cards[8] = new Card("2", 2);
		cards[9] = new Card("2", 5);
		cards[10] = new Card("0", 6);
		cards[11] = new Card("2", 13);
		cards[12] = new Card("3", 3);
		cards[13] = new Card("3", 11);
		cards[14] = new Card("1", 12);
		cards[15] = new Card("0", 3);
		cards[16] = new Card("2", 5);
		cards[17] = new Card("3", 10);
		
		hand1.add(cards[0]);
		hand1.add(cards[1]);
		hand1.add(cards[2]);
		
		hand2.add(cards[3]);
		hand2.add(cards[4]);
		
		hand3.add(cards[5]);
		hand3.add(cards[6]);
		
		hand4.add(cards[7]);
		hand4.add(cards[8]);
		hand4.add(cards[9]);
		hand4.add(cards[10]);
		
		hand5.add(cards[11]);
		hand5.add(cards[12]);
		hand5.add(cards[13]);
		hand5.add(cards[14]);
		
		hand6.add(cards[15]);
		hand6.add(cards[16]);
		hand6.add(cards[17]);
		
		game.dealOneToBanker();
		game.dealOneToBanker();
		
		game.dealOneToPlayer();
		game.dealOneToPlayer();
		
		
	}
	
	@Test
	void test1()
	{
		// bgl - whowon
		assertEquals("Banker", BaccaratGameLogic.whoWon(hand1, hand2), "wrong value");
	}
	
	@Test
	void test2()
	{
		// blg - whowon
		assertEquals("Player", BaccaratGameLogic.whoWon(hand2, hand3), "wrong value");
	}
	
	@Test
	void test3()
	{
		// blg - handTotal
		assertEquals(7, BaccaratGameLogic.handTotal(hand3), "wrong value");
	}
	
	@Test
	void test4()
	{
		//blg - handTotal
		assertEquals(8, BaccaratGameLogic.handTotal(hand2), "wrong value");
	}
	
	@Test
	void test5()
	{
		//blg - evalbankdraw
		assertEquals(true, BaccaratGameLogic.evaluateBankerDraw(hand1, null), "wrong boolean");
	}
	
	@Test
	void test6()
	{
		//blg - evalbankdraw
		assertEquals(true, BaccaratGameLogic.evaluateBankerDraw(hand1, cards[10]), "wrong boolean");
	}
	
	@Test
	void test7()
	{
		//blg - evalplaydraw
		assertEquals(true, BaccaratGameLogic.evaluatePlayerDraw(hand1), "wrong boolean");
	}
	
	@Test
	void test8()
	{
		//blg - evalplaydraw
		assertEquals(true, BaccaratGameLogic.evaluatePlayerDraw(hand5), "wrong boolean");
	}
	
	@Test
	void test9()
	{
		//blg - checkWinCond
		assertEquals(true, BaccaratGameLogic.checkWinCond(hand2, hand4), "wrong boolean");
	}
	
	@Test
	void test10()
	{
		//blg - checkWinCond
		assertEquals(false, BaccaratGameLogic.checkWinCond(hand1, hand5), "wrong boolean");
	}
	
	@Test
	void test11()
	{
		assertEquals(48, game.theDealer.deckSize(), "wrong value");
	}
	
	@Test
	void test12()
	{
		assertEquals(52, game2.theDealer.deckSize(), "wrong value");
	}
	
	@Test
	void test13()
	{
		// baccaratGame Constructor
		assertEquals(0.0, game.getWager(), "wrong value");
		assertEquals(null, game.getWinMsg(), "wrong value");
		assertEquals(null, game.getServMsg(), "wrong value");
		assertEquals(0, game2.getPlayerHand().size(), "wrong value");
		assertEquals(0, game2.getBankerHand().size(), "wrong value");
	}
	
	@Test
	void test14()
	{
		assertEquals(2, game.getPlayerHand().size(), "wrong value");
	}
}
