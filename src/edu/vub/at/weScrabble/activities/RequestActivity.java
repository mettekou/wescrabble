package edu.vub.at.weScrabble.activities;

import edu.vub.at.weScrabble.R;
import edu.vub.at.weScrabble.WeScrabble;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * The activity which allows the local player to view and request letters from his teammates.
 * 
 * @author Dylan Meysmans
 *
 */
public class RequestActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_request);
		
		WeScrabble gameState = MainActivity.getGameState();
		gameState.setContext(this);
		
		ArrayAdapter<Character> adapter = new ArrayAdapter<Character>(this, android.R.layout.simple_list_item_1, WeScrabble.lettersFromTeammates);
		gameState.setDonationListViewAdapter(adapter);
		
		final ListView donationListView = (ListView) findViewById(R.id.donationListView);
		donationListView.setAdapter(adapter);
		donationListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				MainActivity.getGameState().prepareRequestLetter((Character) donationListView.getItemAtPosition(arg2));
				finish();
			}
		});
		
		gameState.sendCollectLettersFromTeammates();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		MainActivity.getGameState().setContext(this);
	}
}
