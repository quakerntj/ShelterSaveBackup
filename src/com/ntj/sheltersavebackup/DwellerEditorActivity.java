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

import com.ntj.sheltersavebackup.ItemDatabase.DBItem;
import com.ntj.sheltersavebackup.ShelterSaveParser.Item;

public class DwellerEditorActivity extends Activity {
	private final int ACT_RESULT_CHOOSE_OUTFIT = 0x01010;
	private final int ACT_RESULT_CHOOSE_WEAPON = 0x01020;
	private final int ACT_RESULT_CHOOSE_EQUIPMENT = 0x01030;

	List<SpecialSingleView> mList = new ArrayList<SpecialSingleView>();
	int [] mSpecial = new int [7];
	private ShelterSaveParser mParser;
	private ShelterSaveParser.Dweller mDweller;
	private ItemDatabase mDatabase;
	private EquipmentButtonView mEditingView = null;  

	// TODO process gender special outfit
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null || resultCode != RESULT_OK)
			return;
		String type = "Weapon";
		String id = "";
		Bundle bundle = data.getExtras();
		if (bundle != null) {
			type = bundle.getString("type");
			id = bundle.getString("id");
		}
		if (id.isEmpty())
			return;
		Item newitem = null;
		switch (requestCode) {
		case ACT_RESULT_CHOOSE_EQUIPMENT:
			if (mEditingView == null)
				return;
			String oldid = mEditingView.getEquipmentId();

			// Find first item with same id and replace it.
			List<Item> list = mDweller.mEquipment.getList();
			if (list != null) {
				for (Item i : list) {
					if (!i.mId.equals(oldid))
						continue;
					i.mId = id;
					i.mType = type;
					newitem = i;
					configView(mEditingView, i, -1);
					break;
				}
			}
			mEditingView = null;
			break;
		case ACT_RESULT_CHOOSE_WEAPON:
			EquipmentButtonView weaponView = (EquipmentButtonView) findViewById(R.id.equiped_weapon);
			if (type.equals("Weapon")) {
				mDweller.mEquipedWeapon.mId = id;
				configView(weaponView, mDweller.mEquipedWeapon, 1);
				newitem = mDweller.mEquipedWeapon;
			}
			break;
		case ACT_RESULT_CHOOSE_OUTFIT:
			EquipmentButtonView outfitView = (EquipmentButtonView) findViewById(R.id.equiped_outfit);
			if (type.equals("Outfit")) {
				mDweller.mEquipedOutfit.mId = id;
				configView(outfitView, mDweller.mEquipedOutfit, 0);
				newitem = mDweller.mEquipedOutfit;
			}
			break;
		}

		if (newitem == null)
			return;
		try {
			newitem.update();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private OnClickListener mClicklistenerWeapon = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getApplicationContext(), ItemChooserActivity.class);
			intent.putExtra("type", "Weapon");
			startActivityForResult(intent, ACT_RESULT_CHOOSE_WEAPON);
		}
	};
	
	private OnClickListener mClicklistenerOutfit = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getApplicationContext(), ItemChooserActivity.class);
			intent.putExtra("type", "Outfit");
			startActivityForResult(intent, ACT_RESULT_CHOOSE_OUTFIT);
		}
	};
	
	private OnClickListener mClicklistenerEquipment = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mEditingView = (EquipmentButtonView) v;
			Intent intent = new Intent(getApplicationContext(), ItemChooserActivity.class);
			intent.putExtra("type", "Equipment");
			startActivityForResult(intent, ACT_RESULT_CHOOSE_EQUIPMENT);
		}
	};

	/* As inventory if set forceType = -1 */
	private void configView(EquipmentButtonView view, Item item, int forceType) {
		if (forceType == 1) {  // Weapon
			view.setOnClickListener(mClicklistenerWeapon);
		} else if (forceType == 0) {  // Outfit
			view.setOnClickListener(mClicklistenerOutfit);
		} else if (forceType == -1) {  // Any Equipment
			view.setOnClickListener(mClicklistenerEquipment);
		}

		boolean isWeapon = (forceType == 1);
		if (forceType == -1) {
			if (item.mType.equals(ItemDatabase.TYPE_WEAPON))
				isWeapon = true;
		}
		DBItem dbitem = mDatabase.find(item);
		if (dbitem != null) {
			view.setWeapon(isWeapon);
			view.setName(dbitem.showname);
			view.setEquipmentId(dbitem.id);
			view.setImage(ItemDatabase.getDrawable(dbitem));
		} else {
			view.setWeapon(isWeapon);
			view.setName(item.mId);
			view.setEquipmentId(item.mId);
			view.setImage(isWeapon ? R.drawable.fist : R.drawable.vault_suit);
		}
	}
	
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
			int special = mSpecial[i] = mDweller.mSpecial.special[i];
			view.setValue(special);
			view.setTag(i);
			view.setClickable(true);
			view.setOnClickListener(listener);
			mList.add(view);
		}

		EquipmentButtonView outfit = (EquipmentButtonView) findViewById(R.id.equiped_outfit);
		configView(outfit, mDweller.mEquipedOutfit, 0);

		EquipmentButtonView weapon = (EquipmentButtonView) findViewById(R.id.equiped_weapon);
		configView(weapon, mDweller.mEquipedWeapon, 1);

		List<Item> list = mDweller.mEquipment.getList();
		if (list != null) {
			LinearLayout viewList = (LinearLayout) findViewById(R.id.linear_equipment_list);
			for (Item i : list) {
				EquipmentButtonView btn = new EquipmentButtonView(this);
				configView(btn, i, -1);
				viewList.addView(btn);
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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

		mDatabase = ItemDatabase.getInstance(this);
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
		finish();
	}
}