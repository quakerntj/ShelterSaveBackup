package com.ntj.sheltersavebackup;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SpecialSingleView extends RelativeLayout {
	private String mName;
	public SpecialSingleView(Context context) {
		super(context);
		inflate(getContext(), R.layout.special_single_view, this);
	}

	public SpecialSingleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews(context, attrs);
	}

	public SpecialSingleView(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs);
		initViews(context, attrs);
	}

	private void initViews(Context context, AttributeSet attrs) {
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.SpecialSingleView, 0, 0);
		int imageid;
		String name = "";
		try {
			// get the text and colors specified using the names in attrs.xml
			imageid = a.getResourceId(R.styleable.SpecialSingleView_specialImage, R.drawable.s);
			mName = a.getString(R.styleable.SpecialSingleView_specialName);
		} finally {
			a.recycle();
		}

		LayoutInflater.from(context).inflate(R.layout.special_single_view, this);

		ImageView image = (ImageView) this.findViewById(R.id.image_special);
		image.setImageResource(imageid);

		TextView text = (TextView) this.findViewById(R.id.text_special_name);
		text.setText(mName);
	}
	public String getName() {
		return mName;
	}
	public void setValue(int value) {
		TextView number = (TextView) findViewById(R.id.text_special_number);
		number.setText(Integer.toString(value));
	}
}
