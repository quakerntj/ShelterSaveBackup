package com.ntj.sheltersavebackup;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ntj.sheltersavebackup.ShelterSaveParser.Item;

public class DatabaseMakerActivity extends Activity {
	private ShelterSaveParser mParser;
	private ItemDatabase mDatabase;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_database_maker);
		LinearLayout linear = (LinearLayout) findViewById(R.id.linear_database);
		Button btn = new Button(this);
		btn.setText("scan");
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doScan();
			}
		});
		linear.addView(btn);

		mDatabase = ItemDatabase.getInstance(this);
	}

	private void doScan() {
		mParser = ShelterSaveParser.getInstance();
		if (mParser == null) {
			finish();
			return;
		}

		ShelterSaveParser.Dwellers dwellers = mParser.getDwellers();
		List<ShelterSaveParser.Dweller> list = dwellers.getList();

		for (ShelterSaveParser.Dweller dweller : list) {
			mDatabase.add(dweller.mEquipedOutfit);
			mDatabase.add(dweller.mEquipedWeapon);
			for (Item item : dweller.mEquipment.getList()) {
				mDatabase.add(item);
			}
		}

		ShelterSaveParser.Vault vault = mParser.getVault();
		for (Item item : vault.mInventory.getList()) {
			mDatabase.add(item);
		}
		Log.d("FOSDB", "save");
		mDatabase.save();
	}
}
