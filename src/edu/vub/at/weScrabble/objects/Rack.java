package edu.vub.at.weScrabble.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents the rack of letters of a player.
 * 
 * @author
 *
 */
public class Rack {
	/**
	 * The cumulative frequencies for the letters of the Latin alphabet in English words.
	 * 
	 * These help the rack generate more usable letters than just pseudorandom letters.
	 */
	final private static double[] CUMULATIVE_LETTER_FREQUENCIES = {0.08167, 0.09659, 0.12441, 0.16694, 0.29396, 0.31624, 0.33639, 0.39733, 0.46699, 0.46852, 0.46642, 0.51649, 0.54055, 0.60804, 0.68311, 0.70240, 0.70335, 0.76322, 0.82649, 0.91705, 0.94436, 0.95441, 0.97802, 0.97952, 0.99926, Double.MAX_VALUE};
	
	private Random generator;
	private List<Character> letters;
	
	/**
	 * Generates a rack of {@code amountOfLetters} random letters.
	 * 
	 * @param amountOfLetters
	 */
	public Rack(int amountOfLetters) {
		generator = new Random(System.currentTimeMillis());
		letters = new ArrayList<Character>();
		
		for (int i = 0; i < amountOfLetters; ++i) {
			addRandomLetter();
		}
	}
	
	/**
	 * Adds a random letter to the rack.
	 */
	public void addRandomLetter() {
		double value = generator.nextDouble();
		
		int i = 0;
		
		// TODO: Make this stupid O(n) algorithm smarter (read: in O(log n) should be possible).
		// Does not matter all that much because n is always 26.
		while (i < CUMULATIVE_LETTER_FREQUENCIES.length) {
			if (value <= CUMULATIVE_LETTER_FREQUENCIES[i]) break;
			++i;
		}
		
		letters.add((char) (97 + i));
	}
	
	/**
	 * Removes a single letter from the rack. 
	 */
	public boolean removeLetter(char letter) {
		return letters.remove(Character.valueOf(letter));
	}
	
	public boolean isEmpty() {
		return letters.isEmpty();
	}
	
	/**
	 * Returns the letters in the rack.
	 * 
	 * @return the letters in the rack
	 */
	public List<Character> getLetters() {
		return letters;
	}
	
	/**
	 * Adds a single letter to the rack.
	 * 
	 * @param lastLetter
	 */
	public void addLetter(char lastLetter) {
		letters.add(lastLetter);
	}
}
