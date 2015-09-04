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

public class DwellerListActivity extends Activity {
	private LinearLayout mLinearList;
	private ShelterSaveParser mParser;
	private final static boolean DATABASE = true;

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

		mParser = ShelterSaveParser.getInstance();
		if (mParser == null) {
			finish();
			return;
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
							// TODO Auto-generated catch block
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
