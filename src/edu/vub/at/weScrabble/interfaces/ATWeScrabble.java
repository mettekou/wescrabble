package edu.vub.at.weScrabble.interfaces;

import edu.vub.at.objects.coercion.Async;

/** 
 * Interface that the AmbientTalk object needs to implement so that Java objects talk to it. 
 */
public interface ATWeScrabble {
	@Async
	public void setId(final String id);
	
	@Async
	public void setTeam(final String team);
	
	@Async
	public void validateWord(final String wordFormed);
	
	@Async
	public void donateLetter(final String receiver, final String letter);
	
	@Async
	public void refuseToDonateLetter(final String receiver, final String letter);
	
	@Async
	public void emptyRack();
	
	@Async
	public void nonEmptyRack();

	@Async
	public void requestLetter(final String receiver, final String letter);
	
	@Async
	public void collectLettersFromTeammates();
}
