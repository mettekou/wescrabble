package edu.vub.at.weScrabble.activities;

import edu.vub.at.IAT;
import edu.vub.at.android.util.IATAndroid;
import edu.vub.at.weScrabble.R;
import edu.vub.at.weScrabble.WeScrabble;
import edu.vub.at.weScrabble.WeScrabbleAssetInstaller;
import edu.vub.at.weScrabble.interfaces.ATWeScrabble;
import edu.vub.at.weScrabble.interfaces.JWeScrabble;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	// TODO: The game state for a player is static, so all other activities can access it.
	// The use of the Bundle class for sharing data between activities is preferable to this,
	// but does this work for the AmbientTalk interpreter? How would we implement android.os.Parcelable?
	// Static data is preferred in the exercise session solutions, so we follow these.
	private static WeScrabble gameState;
	private static IAT iat;
	
	/**
	 * The task which starts the AmbientTalk interpreter and loads the AmbientTalk code.
	 * 
	 * @author Dylan Meysmans
	 */
	public class StartIATTask extends AsyncTask<Void, String, Void> {
		
		private ProgressDialog pd;

		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			pd.setMessage(values[0]);
		}

		protected void onPreExecute() {
			super.onPreExecute();
			pd = ProgressDialog.show(MainActivity.this, "weScrabble", "Starting AmbientTalk");
		}
		
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pd.dismiss();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				iat = IATAndroid.create(MainActivity.this);
				
				this.publishProgress("Loading weScrabble code");
				iat.evalAndPrint("import /.weScrabble.weScrabble.makeWeScrabble()", System.err);
			} catch (Exception e) {
				Log.e("AmbientTalk", "Could not start Interactive AmbientTalk", e);
			}
			return null;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (gameState != null) gameState.setContext(this);
		
		if (iat == null) {
			Intent i = new Intent(this, WeScrabbleAssetInstaller.class);
			startActivityForResult(i, 0);
		}
		
		Button joinTeamButton = (Button) findViewById(R.id.joinTeamButton);
		joinTeamButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String team = ((EditText) findViewById(R.id.teamNameEditText)).getText().toString();
				TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
				gameState.sendSetId(telephonyManager.getDeviceId());
				gameState.sendSetTeam(team);
				startActivity(new Intent(MainActivity.this, GameActivity.class));
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (gameState != null) gameState.setContext(this);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v("weScrabble", "Return of Asset Installer activity");
		switch (requestCode) {
			case 0:
				if (resultCode == Activity.RESULT_OK) {
					gameState = new WeScrabble(this);
					new StartIATTask().execute((Void)null);
				}
			break;
		}
	}
	
	public JWeScrabble registerATApp(ATWeScrabble atws){
		gameState.registerATApp(atws);
		return gameState;
	}
	
	// TODO: Static game state, see TODO comment for the field above.
	public static WeScrabble getGameState() {
		return gameState;
	}
}
