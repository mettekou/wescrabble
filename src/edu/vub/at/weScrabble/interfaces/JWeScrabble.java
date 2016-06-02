package edu.vub.at.weScrabble.interfaces;

import java.util.List;
import java.util.Map;

import edu.vub.at.objects.natives.NATText;

/** 
 * Interface that the Java object implements with the methods that AmbientTalk objects call on it. 
 */
public interface JWeScrabble {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Methods for local use, i.e. Java-only
	
	/**
	 * Adds a letter to the word formed so far, removing it from the rack.
	 * 
	 * @param letter the letter to add to the word formed
	 */
	public void addLetterToWord(final char letter);
	
	/**
	 * Removes a letter from the word formed so far, putting it back on the rack.
	 */
	public void removeLetterFromWord();
	
	void prepareRequestLetter(final char letter);
	
	/**
	 * Sends the local player's id to AmbientTalk.
	 * 
	 * @param id the id of the local player
	 */
	public void sendSetId(final String id);
	
	/**
	 * Sends the local player's team to AmbientTalk.
	 * 
	 * @param team the team of the local player
	 */
	public void sendSetTeam(final String team);
	
	/**
	 * Sends a word to AmbientTalk for validation through {@code dictionary.at}.
	 */
	public void sendValidateWord();
	
	/**
	 * Sends the donation of a letter to AmbientTalk.
	 * 
	 * @param receiver
	 * @param letter
	 */
	public void sendDonateLetter(final String receiver, final String letter);
	
	/**
	 * Sends the refusal of a donation of a letter to AmbientTalk.
	 * 
	 * @param receiver
	 * @param letter
	 */
	public void sendRefuseToDonateLetter(final String receiver, final String letter);
	
	/**
	 * Tells AmbientTalk that the local player's rack is empty.
	 */
	public void sendEmptyRack();
	
	/**
	 * Tells AmbientTalk that the local player's rack is no longer empty.
	 */
	public void sendNonEmptyRack();
	
	/**
	 * Sends a request for a letter from a player to AmbientTalk.
	 * 
	 * @param player the name of the player to request the letter from
	 * @param letter the letter to request
	 */
	public void sendRequestLetter(final String player, final String letter);
	
	/**
	 * Sends a request for the letters in all teammates' racks to AmbientTalk.
	 */
	public void sendCollectLettersFromTeammates();
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Handshake method
	
	/**
	 * Registers an AmbientTalk application to listen for GUI events
	 * which trigger the method calls declared in ATWeScrabble.
	 */
	public JWeScrabble registerATApp(ATWeScrabble weScrabble);
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Methods which request information or action from the local player, i.e. requests to the player
	
	/**
	 * Retrieves the letters in the rack of the local player.
	 * 
	 * @param player the name of the player who has requested to see the letters in the rack of the local player
	 * @return the letters in the rack of the local player
	 */
	List<Character> getLettersInRack(String player);
	
	/**
	 * Requests a donation of a letter from the local player to a teammate.
	 * 
	 * @param player the teammate who requests the donation
	 * @param letter the letter requested
	 */
	void requestDonation(final String player, final String letter);
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Notification methods for remote events which the local player needs to know about, i.e. responses for the player
	
	/**
	 * Notifies the local player that his teammates' letters have arrived.
	 */
	void notifyTeammatesLettersArrived(final Map<NATText, List<Character>> lettersPerTeammate);
	
	/**
	 * Notifies the local player that a donation he requested was accepted.
	 * 
	 * @param donator the player donating the letter
	 * @param letter the letter requested
	 */
	void notifyDonationAccepted(final String letter, final String donator);
	
	/**
	 * Notifies the local player that a donation he requested was denied.
	 * 
	 * @param donator the player not donating the letter
	 * @param letter the letter requested
	 */
	void notifyDonationDenied(final String letter, final String donator);
	
	/**
	 * Notifies the local player that the word he formed was in the dictionary.
	 * 
	 * @param word the word to remove from the local player's rack
	 */
	void notifyWordValidated(final String word);
	
	/**
	 * Notifies the local player that the word he formed was not in the dictionary.
	 * 
	 * @param word the word to remove from the local player's rack
	 */
	void notifyWordInvalidated(final String word);
	
	/**
	 * Notifies the local player that a new team was created.
	 * 
	 * @param team
	 * @param firstPlayer
	 */
	void notifyTeamCreated(final String team, final String firstPlayer);
	
	/**
	 * Notifies the local player of a remote player joining the game.
	 * 
	 * @param player the name of the remote player
	 * @param team the name of the team the remote player joined
	 */
	void notifyPlayerJoined(final String player, final String team);
	
	/**
	 * Notifies the local player of a remote player's disconnection.
	 * 
	 * @param player the remote player's name
	 */
	void notifyPlayerDisconnected(final String player);
	
	/**
	 * Notifies the local player of a remote player's reconnection.
	 * 
	 * @param player the remote player's name
	 */
	void notifyPlayerReconnected(final String player);
	
	/**
	 * Notifies the local player that the game is over.
	 * 
	 * @param winningTeam the name of the team which has won the game
	 */
	void notifyGameOver(final String winningTeam);
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Punishment methods which modify the local player's rack for bad gameplay.
	
	/**
	 * Punishes the local player due to another team forming a word.
	 * 
	 * @param team the team which formed a word
	 * @param letter the letter to add to the local player's rack
	 */
	void punishForOpponentForming(final String team, final String letter);
	
	/**
	 * Punishes the local player for not forming a word in time.
	 */
	void punishForSloth();
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}