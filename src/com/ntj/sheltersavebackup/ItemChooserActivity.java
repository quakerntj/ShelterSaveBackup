package com.ntj.sheltersavebackup;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.ntj.sheltersavebackup.ItemDatabase.DBItem;

public class ItemChooserActivity extends Activity implements OnClickListener {
	private ItemDatabase mDatabase;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_chooser);
		LinearLayout linearCategory = (LinearLayout) findViewById(R.id.linear_catagory);

		mDatabase = ItemDatabase.getInstance();

		String type = "Weapon";
		Intent intent = getIntent();
		if (intent != null) {
			Bundle extras = intent.getExtras();
			if (extras != null) {
				type = extras.getString("type");
			}
		}

		if (type.equals("Weapon")) {
			HashMap<String, LinearLayout> map = new HashMap<String, LinearLayout>();
			List<DBItem> weapons = mDatabase.getWeaponList();
			for (DBItem i : weapons) {
				LinearLayout linear = map.get(i.category);
				if (linear == null) {
					HorizontalScrollView scroll = new HorizontalScrollView(this);
					scroll.setLayoutParams(new LayoutParams(
							LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					linear = new LinearLayout(this);
					linear.setOrientation(LinearLayout.HORIZONTAL);
					linear.setLayoutParams(new LayoutParams(
							LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					map.put(i.category, linear);
					scroll.addView(linear);
					linearCategory.addView(scroll);
				}
				int id = ItemDatabase.getDrawable(i);
				EquipmentButtonView view = new EquipmentButtonView(this);
				view.setOnClickListener(this);
				view.setImage(id);
				view.setEquipmentId(i.id);
				view.setWeapon(true);
				view.setName(i.showname);
				linear.addView(view);
			}
		} else if (type.equals("Outfit")) {
			HashMap<String, LinearLayout> map = new HashMap<String, LinearLayout>();
			List<DBItem> outfits = mDatabase.getOutfitList();
			for (DBItem i : outfits) {
				LinearLayout linear = map.get(i.category);
				if (linear == null) {
					HorizontalScrollView scroll = new HorizontalScrollView(this);
					scroll.setLayoutParams(new LayoutParams(
							LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					linear = new LinearLayout(this);
					linear.setOrientation(LinearLayout.HORIZONTAL);
					linear.setLayoutParams(new LayoutParams(
							LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					map.put(i.category, linear);
					scroll.addView(linear);
					linearCategory.addView(scroll);
				}
				int id = ItemDatabase.getDrawable(i);
				EquipmentButtonView view = new EquipmentButtonView(this);
				view.setOnClickListener(this);
				view.setImage(id);
				view.setEquipmentId(i.id);
				view.setWeapon(false);
				view.setName(i.showname);
				linear.addView(view);
			}
		}
	}

	@Override
	public void onClick(View v) {
		EquipmentButtonView view = (EquipmentButtonView) v;
		String id = view.getEquipmentId();
		int type = view.getType();
		Intent intent = new Intent();
		intent.putExtra("type", type == 0 ? "Outfit" : "Weapon");
		intent.putExtra("id", id);
		setResult(RESULT_OK, intent);
		finish();
	}
}
