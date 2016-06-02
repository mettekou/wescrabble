package edu.vub.at.weScrabble.activities;

import edu.vub.at.weScrabble.R;
import edu.vub.at.weScrabble.WeScrabble;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class GameActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		WeScrabble gameState = MainActivity.getGameState();
		gameState.setContext(this);
		gameState.setWordFormedTextView((TextView) findViewById(R.id.wordFormedTextView));
		
		ArrayAdapter<Character> adapter = new ArrayAdapter<Character>(this, android.R.layout.simple_list_item_1, gameState.getRack().getLetters());
		gameState.setRackListViewAdapter(adapter);
		
		final ListView rackListView = (ListView) findViewById(R.id.rackListView);
		rackListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		rackListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				MainActivity.getGameState().addLetterToWord((Character) rackListView.getItemAtPosition(arg2));
			}
		});
		
		final Button putLetterBackButton = (Button) findViewById(R.id.putLetterBackButton);
		putLetterBackButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.getGameState().removeLetterFromWord();
			}
		});
		
		final Button confirmWordButton = (Button) findViewById(R.id.confirmWordButton);
		confirmWordButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.getGameState().sendValidateWord();
			}
		});
		
		final GameActivity context = this;
		final Button requestLettersButton = (Button) findViewById(R.id.requestLettersButton);
		
		requestLettersButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(context, RequestActivity.class));
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		MainActivity.getGameState().setContext(this);
	}
}
