package com.ntj.sheltersavebackup;

import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ntj.sheltersavebackup.ShelterSaveParser.Vault;

public class DwellerListActivity extends Activity {
	private LinearLayout mLinearList;
	private ShelterSaveParser mParser;
	private final static boolean DATABASE = true;
	private final static boolean LUNCHBOXES = true;
	private final static boolean CAPS = true;

	protected void editDweller(int id) {
		Intent intent = new Intent(this, DwellerEditorActivity.class);
		intent.putExtra("serialid", id);
		startActivity(intent);
	}

	protected void makeDatabase() {
		Intent intent = new Intent(this, DatabaseMakerActivity.class);
		startActivity(intent);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mParser = ShelterSaveParser.getInstance();
		if (mParser == null) {
			finish();
			return;
		}

		setContentView(R.layout.activity_dwellers_list);
		mLinearList = (LinearLayout) findViewById(R.id.linear_dwellers_list);

		if (DATABASE) {
			Button btn = new Button(this);
			btn.setText("Make item database");
			btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					makeDatabase();
				}
			});
			mLinearList.addView(btn);
		}

		if (LUNCHBOXES) {
			Vault v = mParser.getVault();
			Button btn = new Button(this);
			int count = v.getLunchBoxesCount();
			btn.setText("Add Lunch Box (" + count + ")");
			btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Button btn = (Button) v;
					Vault vault = mParser.getVault();
					vault.addLunchBox(Vault.LUNCH_BOX);
					int count = vault.getLunchBoxesCount();
					btn.setText("Add Lunch Box (" + count + ")");
				}
			});
			mLinearList.addView(btn);

			btn = new Button(this);
			count = v.getMrHandyCount();
			btn.setText("Add Mr. Handy (" + count + ")");
			btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Button btn = (Button) v;
					Vault vault = mParser.getVault();
					vault.addLunchBox(Vault.MR_HANDY);
					int count = vault.getMrHandyCount();
					btn.setText("Add Mr. Handy (" + count + ")");
				}
			});
			mLinearList.addView(btn);
		}
		
		if (CAPS) {
			Vault v = mParser.getVault();
			Button btn = new Button(this);
			int caps = v.getNuka();
			btn.setText("Add 1000 Caps (" + caps + ")");
			btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Button btn = (Button) v;
					Vault vault = mParser.getVault();
					double caps = vault.getNuka() + 1000.0;
					vault.setNuka(caps);
					btn.setText("Add 1000 Caps (" + (int)caps + ")");
				}
			});
			mLinearList.addView(btn);
		}

		ShelterSaveParser.Dwellers dwellers = mParser.getDwellers();
		List<ShelterSaveParser.Dweller> list = dwellers.getList();

		for (ShelterSaveParser.Dweller dweller : list) {
			String text = dweller.toString();
			final int id = dweller.mSerializedId;
			Button btn = new Button(this);
			btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					editDweller(id);
				}
			});
			btn.setText(text);
			mLinearList.addView(btn);
		}
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
			.setTitle("Save changes?")
			.setMessage("Do you want to save your changes?")
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							mParser.update();
						} catch (JSONException e) {
							e.printStackTrace();
						}
						mParser.close();
						mParser = null;
						finish();
					}
		
				})
			.setNegativeButton("No", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mParser.close();
					mParser = null;
					finish();
				}
	
			})
			.show();
	}
}
