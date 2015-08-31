package com.ntj.sheltersavebackup;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ntj.sheltersavebackup.ShelterSaveParser.Dweller.Item;

public class DwellerEditorActivity extends Activity {
	List<SpecialSingleView> mList = new ArrayList<SpecialSingleView>();
	int [] mSpecial = new int [7];
	int [] mOriginalSpecial = new int [7];
	private ShelterSaveParser mParser;
	private ShelterSaveParser.Dweller mDweller;

	private void updateDweller() {
		TextView dwellerNameText = (TextView) findViewById(R.id.text_dweller_name);
		dwellerNameText.setText(mDweller.mName + " " + mDweller.mLastName);

		SpecialSingleView view;
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				doSpecialUpdate(v);
			}
		};

		final int [] idlist = new int [] {
			R.id.special_s,
			R.id.special_p,
			R.id.special_e,
			R.id.special_c,
			R.id.special_i,
			R.id.special_a,
			R.id.special_l,
		};

		for (int i = 0; i < 7; i++) {
			view = (SpecialSingleView) findViewById(idlist[i]);
			view.setValue(mDweller.mSpecial.special[i]);
			view.setTag(i);
			view.setClickable(true);
			view.setOnClickListener(listener);
			mList.add(view);
		}

		EquipmentButtonView outfit = (EquipmentButtonView) findViewById(R.id.equiped_outfit);
		outfit.setName(mDweller.mEquipedOutfit.mId);

		EquipmentButtonView weapon = (EquipmentButtonView) findViewById(R.id.equiped_weapon);
		weapon.setName(mDweller.mEquipedWeapon.mId);

		List<Item> list = mDweller.mEquipment.getList();
		if (list != null) {
			LinearLayout viewList = (LinearLayout) findViewById(R.id.linear_equipment_list);
			for (Item i : list) {
				EquipmentButtonView btn = new EquipmentButtonView(this);
				btn.setName(i.mId);
				viewList.addView(btn);
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		for (int i = 0; i < 7; i++)
			mSpecial[i] = 1;

		setContentView(R.layout.dweller_detail);

		boolean verified = false;
		Intent intent = getIntent();
		Bundle bundle = null;
		if (intent != null) {
			bundle = intent.getExtras();
			if (bundle != null && !bundle.isEmpty()) {
				mParser = ShelterSaveParser.getInstance();
				if (mParser != null)
					verified = true;
			}
		}
		if (!verified) {
			finish();
			return;
		}

		int serialid = bundle.getInt("serialid");
		ShelterSaveParser.Dwellers dwellers = mParser.getDwellers();
		List<ShelterSaveParser.Dweller> list = dwellers.getList();
		mDweller = null;
		for (ShelterSaveParser.Dweller dweller : list) {
			if (dweller.mSerializedId == serialid) {
				mDweller = dweller;
				break;
			}
		}
		if (mDweller == null) {
			finish();
			return;
		}

		updateDweller();
	}

	public void doSpecialUpdate(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final SpecialSingleView view = (SpecialSingleView) v;
		builder.setTitle(view.getName());
		final int id = ((Integer) view.getTag()).intValue();
		
		// Set up the input
		final EditText input = new EditText(this);
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		input.requestFocus();
		input.setText("10");
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					int value = Integer.valueOf(input.getText().toString());
					if (value < 1 || value > 10)
						return;
					mSpecial[id] = value;
				 	view.setValue(value);
				} catch (NumberFormatException e) {
				}
			}
		});
		builder.setNegativeButton("Cancel", null);
		builder.create().show();
	}

	public void onOk(View v) {
		// Update SPECIAL
		int [] ref = mDweller.mSpecial.special;
		for (int i = 0; i < 7; i++) {
			final int mod = mSpecial[i];
			if (mod != 0)
				ref[i] = mod;
		}
		try {
			mDweller.update();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}