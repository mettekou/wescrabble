package edu.vub.at.weScrabble;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import edu.vub.at.objects.natives.NATText;
import edu.vub.at.weScrabble.interfaces.ATWeScrabble;
import edu.vub.at.weScrabble.interfaces.JWeScrabble;
import edu.vub.at.weScrabble.objects.Rack;

/**
 * An implementation of the game state of weScrabble.
 * 
 * @author Dylan Meysmans
 */
public class WeScrabble implements JWeScrabble {
	// AmbientTalk communication
	
	/**
	 * Represents the types of messages we can send to AmbientTalk.
	 * @author Dylan Meysmans
	 */
	private enum MessageType {
		SET_ID,
		SET_TEAM,
		VALIDATE_WORD,
		DONATE_LETTER,
		REFUSE_TO_DONATE_LETTER,
		EMPTY_RACK,
		NON_EMPTY_RACK,
		REQUEST_LETTER,
		COLLECT_LETTERS_FROM_TEAMMATES
	}
	
	private ATWeScrabble atws;
	private Handler messageHandler;

	// Current Android activity, for interacting with the user interface
	
	private Activity context;
	
	// Game state
	
	private Rack rack;
	private String wordFormed;
	// TODO: Preferring String over NATText here causes a ClassCastException.
	// We still need to figure out why casting does not seem to happen.
	private Map<NATText, List<Character>> lettersByTeammate;
	// TODO: We should use Bundle to pass the WeScrabble instance around to prevent static data.
	// In order to do this efficiently, we need to make it an instance of android.os.Parcelable.
	// However, in the solutions to the exercise sessions static data is preferred, so we follow these.
	public static List<Character> lettersFromTeammates;
	
	// View adapters for pushing data to the user interface
	
	// TODO: These adapters to views should not clutter the model code, a better approach to push data to views should be found.
	// However, for this small application this approach works just fine.
	private ArrayAdapter<Character> rackListViewAdapter;
	private ArrayAdapter<Character> donationListViewAdapter;
	private TextView wordFormedTextView;
	
	/**
	 * Creates an instance of the weScrabble game state.
	 * 
	 * @param context the first activity of the weScrabble application
	 */
	public WeScrabble(Activity context) {
		this.context = context;
		rack = new Rack(5);
		wordFormed = "";
		lettersFromTeammates = new LinkedList<Character>();
		
		// We start the communication with AmbientTalk in a separate thread.
		LooperThread lt = new LooperThread();
		lt.start();
		// We contact this thread through the messageHandler field.
		messageHandler = lt.mHandler;
	}
	
	/**
	 * Returns the local player's rack of letters.
	 * 
	 * @return the local player's rack of letters
	 */
	public Rack getRack() {
		return rack;
	}
	
	/**
	 * Sets the context the game state should contact to push user interface elements to the screen.
	 * 
	 * @param context the context to be contacted by the game state
	 */
	public void setContext(Activity context) {
		this.context = context;
	}
	
	/**
	 * Sets the rack list view adapter to allow the game state to push rack updates to the rack list view.
	 * 
	 * @param rackListViewAdapter the adapter to push rack updates to
	 */
	public void setRackListViewAdapter(ArrayAdapter<Character> rackListViewAdapter) {
		this.rackListViewAdapter = rackListViewAdapter;
	}
	
	/**
	 * Sets the donation list view adapter to allow the game state to push teammates' letters up for donation to it.
	 * 
	 * @param donationListViewAdapter the adapter to push donation updates to
	 */
	public void setDonationListViewAdapter(
			ArrayAdapter<Character> donationListViewAdapter) {
		this.donationListViewAdapter = donationListViewAdapter;
	}
	
	/**
	 * Sets the word formed text view adapter to allow the word formed to be updated.
	 * 
	 * @param wordFormedTextView the adapter to push word formed updates to
	 */
	public void setWordFormedTextView(TextView wordFormedTextView) {
		this.wordFormedTextView = wordFormedTextView;
	}
	
	/**
	 * The thread which handles communication with AmbientTalk separately from the user interface thread.
	 * 
	 * @author Dylan Meysmans
	 */
	private class LooperThread extends Thread {
		public Handler mHandler = new Handler() {

			public void handleMessage(Message msg) {
				if (null == atws)
					return;
				switch (MessageType.values()[msg.what]) {
				case SET_ID:
					atws.setId(((SetIdMessage) msg.obj).id);
				break;
				
				case SET_TEAM:
					atws.setTeam(((SetTeamMessage) msg.obj).team);
				break;
				
				case VALIDATE_WORD:
					atws.validateWord(wordFormed);
				break;
				
				case DONATE_LETTER:
					DonateLetterMessage dlm = (DonateLetterMessage) msg.obj;
					atws.donateLetter(dlm.receiver, dlm.letter);
				break;
				
				case REFUSE_TO_DONATE_LETTER:
					DonateLetterMessage rdlm = (DonateLetterMessage) msg.obj;
					atws.refuseToDonateLetter(rdlm.receiver, rdlm.letter);
				break;
				
				case EMPTY_RACK:
					atws.emptyRack();
				break;
				
				case NON_EMPTY_RACK:
					atws.nonEmptyRack();
				break;
				
				case REQUEST_LETTER:
					RequestLetterMessage rlm = (RequestLetterMessage) msg.obj;
					atws.requestLetter(rlm.receiver, rlm.letter);
				break;
				
				case COLLECT_LETTERS_FROM_TEAMMATES:
					atws.collectLettersFromTeammates();
				break;
				}
			}
		};

		public void run() {
			Looper.prepare();
			Looper.loop();
		}
	}
	
	private class SetIdMessage {
		final public String id;
		
		public SetIdMessage(final String id) {
			this.id = id;
		}
	}
	
	private class SetTeamMessage {
		final public String team;
		
		public SetTeamMessage(final String team) {
			this.team = team;
		}
	}
	
	private class RequestLetterMessage {
		final public String receiver;
		final public String letter;
		
		public RequestLetterMessage(final String player, final String letter) {
			this.receiver = player;
			this.letter = letter;
		}
	}
	
	private class DonateLetterMessage {
		final public String receiver;
		final public String letter;
		
		public DonateLetterMessage(final String receiver, final String letter) {
			this.receiver = receiver;
			this.letter = letter;
		}
	}
	
	@Override
	public void addLetterToWord(char letter) {
		rack.removeLetter(letter);
		wordFormed = wordFormed.concat(Character.valueOf(letter).toString());
		
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				rackListViewAdapter.notifyDataSetChanged();
				wordFormedTextView.setText(wordFormed);
			}
		});
	}

	@Override
	public void removeLetterFromWord() {
		if (!wordFormed.isEmpty()) {
			char lastLetter = wordFormed.charAt(wordFormed.length() - 1);
		
			wordFormed = wordFormed.substring(0, wordFormed.length() - 1);
		
			if (rack.isEmpty()) {
				messageHandler.sendMessage(Message.obtain(messageHandler, MessageType.NON_EMPTY_RACK.ordinal()));
			}
		
			rack.addLetter(lastLetter);
		
			context.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					rackListViewAdapter.notifyDataSetChanged();
					wordFormedTextView.setText(wordFormed);
				}
			});
		}
	}
	
	@Override
	public void sendSetId(final String id) {
		messageHandler.sendMessage(Message.obtain(messageHandler, MessageType.SET_ID.ordinal(), new SetIdMessage(id)));
	}
	
	@Override
	public void sendSetTeam(final String team) {
		messageHandler.sendMessage(Message.obtain(messageHandler, MessageType.SET_TEAM.ordinal(), new SetTeamMessage(team)));
	}
	
	@Override
	public void sendValidateWord() {
		messageHandler.sendMessage(Message.obtain(messageHandler, MessageType.VALIDATE_WORD.ordinal()));
	}
	
	@Override
	public void sendDonateLetter(final String receiver, final String letter) {
		messageHandler.sendMessage(Message.obtain(messageHandler, MessageType.DONATE_LETTER.ordinal(), new DonateLetterMessage(receiver, letter)));
	}
	
	@Override
	public void sendRefuseToDonateLetter(final String receiver, final String letter) {
		messageHandler.sendMessage(Message.obtain(messageHandler, MessageType.REFUSE_TO_DONATE_LETTER.ordinal(), new DonateLetterMessage(receiver, letter)));
	}
	
	@Override
	public void sendEmptyRack() {
		messageHandler.sendMessage(Message.obtain(messageHandler, MessageType.EMPTY_RACK.ordinal()));
	}
	
	@Override
	public void sendNonEmptyRack() {
		messageHandler.sendMessage(Message.obtain(messageHandler, MessageType.NON_EMPTY_RACK.ordinal()));
	}
	
	@Override
	public void sendRequestLetter(final String player, final String letter) {
		messageHandler.sendMessage(Message.obtain(messageHandler, MessageType.REQUEST_LETTER.ordinal(), new RequestLetterMessage(player, letter)));
	}
	
	@Override
	public void sendCollectLettersFromTeammates() {
		messageHandler.sendMessage(Message.obtain(messageHandler, MessageType.COLLECT_LETTERS_FROM_TEAMMATES.ordinal()));
	}
	
	@Override
	public JWeScrabble registerATApp(ATWeScrabble atws) {
		this.atws = atws;
		return this;
	}
	
	@Override
	public List<Character> getLettersInRack(final String player) {
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, "Teammate " + player + " has requested to see your letters.", Toast.LENGTH_SHORT).show();
			}
		});
		
		return rack.getLetters();
	}
	
	@Override
	public void requestDonation(final String player, final String letter) {
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				DonationListener dl = new DonationListener(letter, player);
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage(player + " has requested letter " + letter + " from you. Would you like to donate it to his rack?").setPositiveButton("Yes", dl).setNegativeButton("No", dl).show();
			}
		});
	}
	
	/**
	 * 
	 * @author Dylan Meysmans
	 *
	 */
	private class DonationListener implements DialogInterface.OnClickListener {
		final private String letter;
		final private String player;
		
		private DonationListener(final String letter, final String player) {
			this.letter = letter;
			this.player = player;
		}
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				sendDonateLetter(player, letter);
			break;

			case DialogInterface.BUTTON_NEGATIVE:
				sendRefuseToDonateLetter(player, letter);
			break;
			}
		}
	}
	
	// TODO: Create a single notify method which takes an instance of a subclass of an Event
	// class (to be created as well). The Event subclass then encapsulates the parameters
	// to the notification and overrides the behaviour for the appropriate response
	// to the notification. This shields us from having to modify JWeScrabble and WeScrabble
	// every time a new notification is added.
	
	@Override
	public void notifyDonationAccepted(final String letter, final String donator) {
		rack.addLetter(letter.toCharArray()[0]);
		
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				rackListViewAdapter.notifyDataSetChanged();
				Toast.makeText(context, donator + " has accepted to donate letter " + letter + " to you.", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void notifyDonationDenied(final String letter, final String donator) {		
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, donator + " has refused to donate letter " + letter + " to you.", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	@Override
	public void notifyWordValidated(final String word) {		
		wordFormed = "";
		
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				wordFormedTextView.setText("");
				Toast.makeText(context, "You have formed a valid word!", Toast.LENGTH_SHORT).show();
			}
		});
		
		if (rack.isEmpty()) sendEmptyRack();
	}
	
	@Override
	public void notifyWordInvalidated(final String word) {
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, "You have formed a invalid word!", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	@Override
	public void notifyTeamCreated(final String team, final String firstPlayer) {
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, "Team " + team + " was created by player " + firstPlayer + ".", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	@Override
	public void notifyPlayerJoined(final String player, final String team) {
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, "Player " + player + " has joined the game for team " + team + ".", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void notifyPlayerDisconnected(final String player) {
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, "Player " + player + " was disconnected from the game.", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void notifyPlayerReconnected(final String player) {
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, "Player " + player + " was reconnected to the game.", Toast.LENGTH_SHORT).show();
			}
		});	}
	
	@Override
	public void notifyGameOver(final String team) {
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, "Team " + team + " has won the game!", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	@Override
	public void notifyTeammatesLettersArrived(final Map<NATText, List<Character>> lettersByTeammate) {
		this.lettersByTeammate = lettersByTeammate;
		for (List<Character> letters : lettersByTeammate.values()) {
			lettersFromTeammates.addAll(letters);
		}
		
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				donationListViewAdapter.notifyDataSetChanged();
				Toast.makeText(context, "Received letters from teammates!", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void punishForOpponentForming(final String team, final String letter) {
		rack.addLetter(letter.toCharArray()[0]);
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				rackListViewAdapter.notifyDataSetChanged();
				Toast.makeText(context, "Adding letter " + letter + " to your rack from a word formed by team " + team, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void punishForSloth() {
		rack.addRandomLetter();
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				rackListViewAdapter.notifyDataSetChanged();
				Toast.makeText(context, "You have received a random letter to punish you for your sloth.", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	@Override
	public void prepareRequestLetter(final char letter) {
		for (final Entry<NATText, List<Character>> lettersByPlayer : lettersByTeammate.entrySet()) {
			if (lettersByPlayer.getValue().contains(letter)) {
				sendRequestLetter(lettersByPlayer.getKey().javaValue, letter + "");
				lettersFromTeammates = new LinkedList<Character>();
				lettersByTeammate = null;
				context.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(context, "A request for letter " + letter + " has been sent to player " + lettersByPlayer.getKey().javaValue + ".", Toast.LENGTH_SHORT).show();
					}
				});
				break;
			}
		}
	}
}
