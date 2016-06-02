package edu.vub.at.weScrabble;

import edu.vub.at.android.util.AssetInstaller;

public class WeScrabbleAssetInstaller extends AssetInstaller {
	// Overrides the default constructor to always copy the assets to the sdcard.
	public WeScrabbleAssetInstaller() {
      super();
      development = true;
	}
}
