package com.ntj.sheltersavebackup;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EquipmentButtonView extends RelativeLayout {
	private String mName = "";
	public EquipmentButtonView(Context context) {
		super(context);
		inflate(getContext(), R.layout.equipment_button, this);
	}

	public EquipmentButtonView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews(context, attrs);
	}

	public EquipmentButtonView(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs);
		initViews(context, attrs);
	}
	private void initViews(Context context, AttributeSet attrs) {
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.EquipmentButtonView, 0, 0);
		int imageid;
		try {
			// get the text and colors specified using the names in attrs.xml
			imageid = a.getResourceId(R.styleable.EquipmentButtonView_equipmentImage, R.drawable.fist);
			mName = a.getString(R.styleable.EquipmentButtonView_equipmentName);
		} finally {
			a.recycle();
		}

		LayoutInflater.from(context).inflate(R.layout.equipment_button, this);
		setImage(imageid);
		setName(mName);
	}

	public void setImage(int id) {
		ImageView image = (ImageView) this.findViewById(R.id.img_equipment);
		image.setImageResource(id);
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
		TextView number = (TextView) findViewById(R.id.text_equipment_name);
		number.setText(name);
	}
}
