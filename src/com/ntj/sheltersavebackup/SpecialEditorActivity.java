package com.ntj.sheltersavebackup;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class SpecialEditorActivity extends Activity implements OnClickListener {
	List<SpecialSingleView> mList = new ArrayList<SpecialSingleView>();
	int [] mSpecial = new int [7];
	int [] mOriginalSpecial = new int [7];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		for (int i = 0; i < 7; i++)
			mSpecial[i] = 1;

		setContentView(R.layout.dweller_detail);

		Intent intent = getIntent();
		Bundle bundle = null;
		boolean setValue = true;
		if (intent == null) {
			setValue = false;
		} else {
			bundle = intent.getExtras();
			if (bundle == null || bundle.isEmpty())
				setValue = false;
		}

		SpecialSingleView view;
		view = (SpecialSingleView) findViewById(R.id.special_s);
		if (setValue) view.setValue(bundle.getInt("s"));
		view.setTag(0);
		view.setClickable(true);
		view.setOnClickListener(this);
		mList.add(view);
		view = (SpecialSingleView) findViewById(R.id.special_p);
		if (setValue) view.setValue(bundle.getInt("p"));
		view.setTag(1);
		view.setClickable(true);
		view.setOnClickListener(this);
		mList.add(view);
		view = (SpecialSingleView) findViewById(R.id.special_e);
		if (setValue) view.setValue(bundle.getInt("e"));
		view.setTag(2);
		view.setClickable(true);
		view.setOnClickListener(this);
		mList.add(view);
		view = (SpecialSingleView) findViewById(R.id.special_c);
		if (setValue) view.setValue(bundle.getInt("c"));
		view.setTag(3);
		view.setClickable(true);
		view.setOnClickListener(this);
		mList.add(view);
		view = (SpecialSingleView) findViewById(R.id.special_i);
		if (setValue) view.setValue(bundle.getInt("i"));
		view.setTag(4);
		view.setClickable(true);
		view.setOnClickListener(this);
		mList.add(view);
		view = (SpecialSingleView) findViewById(R.id.special_a);
		if (setValue) view.setValue(bundle.getInt("a"));
		view.setTag(5);
		view.setClickable(true);
		view.setOnClickListener(this);
		mList.add(view);
		view = (SpecialSingleView) findViewById(R.id.special_l);
		if (setValue) view.setValue(bundle.getInt("l"));
		view.setTag(6);
		view.setClickable(true);
		view.setOnClickListener(this);
		mList.add(view);
	}

	@Override
	public void onClick(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final SpecialSingleView view = (SpecialSingleView) v;
		builder.setTitle(view.getName());
		final int id = ((Integer) view.getTag()).intValue();
		

		// Set up the input
		final EditText input = new EditText(this);
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		input.requestFocus();
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	try {
		    		 int value = Integer.valueOf(input.getText().toString());
		    		 if (value < 1 || value >10)
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
		
	}
}
