package com.ntj.sheltersavebackup;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShelterBackupActivity extends Activity {
	private static final String TAG = "ShelterBK";
	private static final String SAVE_PATH = "Android/data/com.bethsoft.falloutshelter";
	private static final String BACKUP_PATH = "Android/data/com.ntj.sheltersavebackup";
	private boolean DEBUG = false;

	private File mSavePath = null;
	private AtomicInteger mIndex = null;

	class BackupedButton {
		private int mIndex;
		private long mDate;
		private RelativeLayout mLayout;

		public BackupedButton(Context context) {
			mLayout = (RelativeLayout) View.inflate(context, R.layout.view_backuped_buttons, null);
		}

		public RelativeLayout getLayout() {
			return mLayout;
		}

		public void setDate(long date) {
			mDate = date;
			TextView text = (TextView) mLayout.findViewById(R.id.text_backup_date);
			text.setText(getDate(mDate));
		}

		public void setIndex(int index) {
			mIndex = index;
			TextView text = (TextView) mLayout.findViewById(R.id.text_index);
			text.setText(String.format("BACKUP %03d", mIndex));
		}

		public int getIndex() {
			return mIndex;
		}

		public File getFile() {
			return new File(Environment.getExternalStorageDirectory(),
	        		String.format("%s/%03d/Vault.sav", BACKUP_PATH, mIndex));
		}

		public File getJsonFile() {
			return new File(Environment.getExternalStorageDirectory(),
	        		String.format("%s/%03d/Vault.json", BACKUP_PATH, mIndex));
		}
	}

	private LinkedList<BackupedButton> mButtons = new LinkedList<BackupedButton>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shelter_backup);

		File extpath = Environment.getExternalStorageDirectory();
		if (extpath == null)
			return;
		mSavePath = new File(extpath, SAVE_PATH);
	}

	public String getDate(long date) {
		return SimpleDateFormat.getDateTimeInstance().format(new Date(date));
	}

	private void getLatestIndex() {
		File extpath = Environment.getExternalStorageDirectory();
		File bkpath = new File(extpath, BACKUP_PATH);
		File [] list = bkpath.listFiles();
		int max = 0;
		if (list != null) {
			for (File f : list) {
				if (!f.isDirectory())
					continue;
				String name = f.getName();
				try {
					Integer i = Integer.valueOf(name);
					if (i > max)
						max = i.intValue();
				} catch (NumberFormatException e) {
					continue;
				}
			}
		}
		mIndex = new AtomicInteger(max + 1);
		if (DEBUG) Log.d(TAG, "Current index is " + mIndex.get());
	}

	private BackupedButton newBB(int index, long date) {
		BackupedButton bb = new BackupedButton(this);
		bb.setIndex(index);
		bb.setDate(date);

		LinearLayout ll = (LinearLayout) findViewById(R.id.linear);
		ll.addView(bb.getLayout());
		mButtons.add(bb);
		return bb;
	}

	private void loadBackupButtons() {
		LinearLayout ll = (LinearLayout) findViewById(R.id.linear);
		ll.removeAllViews();

		File extpath = Environment.getExternalStorageDirectory();
		File bkpath = new File(extpath, BACKUP_PATH);
		File [] list = bkpath.listFiles();
		if (list != null) {
			for (File f : list) {
				if (!f.isDirectory())
					continue;
				int index = 1;
				String name = f.getName();
				try {
					Integer i = Integer.valueOf(name);
					index = i;
				} catch (NumberFormatException e) {
					continue;
				}
				long date = getFileDate(new File(f, "Vault.sav"));
				if (date == 0)
					continue;
				newBB(index, date);
			}
		}
	}
	
	private void copyDirectory(File source, File destination) {
		if (DEBUG) Log.d(TAG, "Copy directory from " + source + " to " + destination);

		try {
        	FileUtils.copyDirectory(source, destination, true);
        } catch (IOException e) {
        	Log.e(TAG, "fail on copy");
            e.printStackTrace();
        }
	}

	private void deleteTree(File f) {
		if (!f.exists())
			return;
		if (f.isDirectory()) {
			File [] list = f.listFiles();
			for (File c : list) {
				deleteTree(c);
			}
		}
		f.delete();
	}

	private void copyFile(File source, File destination) {
		if (DEBUG) Log.d(TAG, "Copy file from " + source + " to " + destination);
		if (destination.isDirectory())
			return;

		File dir = destination.getParentFile();
		if (!dir.exists())
			dir.mkdirs();
		try {
        	FileUtils.copyFile(source, destination, true);
        	if (DEBUG) Log.d(TAG, "copied");
        } catch (IOException e) {
        	Log.e(TAG, "fail on copy");
            e.printStackTrace();
        }
	}

	private void updateSaveButtons() {
		long [] dates = {0, 0, 0};
		for (int i = 1; i < 4; i++) {
			File savFile = new File(mSavePath, "/files/Vault" + i + ".sav");
			if (savFile.exists()) {
				dates[i-1] = savFile.lastModified();
			}
		}
		Button btn;
		btn = (Button)findViewById(R.id.btnSave1);
		if (dates[0] != 0) {
			btn.setText("Save 1 " + getDate(dates[0]));
			btn.setEnabled(true);
		} else {
			btn.setEnabled(false);
		}
		btn = (Button)findViewById(R.id.btnSave2);
		if (dates[1] != 0) {
			btn.setText("Save 2 " + getDate(dates[1]));
			btn.setEnabled(true);
		} else {
			btn.setEnabled(false);
		}
		btn = (Button)findViewById(R.id.btnSave3);
		if (dates[2] != 0) {
			btn.setText("Save 3 " + getDate(dates[2]));
			btn.setEnabled(true);
		} else {
			btn.setEnabled(false);
		}
	}

	protected void onResume() {
		super.onResume();
		updateSaveButtons();
		getLatestIndex();
		loadBackupButtons();
	}

	protected void onPause() {
		super.onPause();
	}

	private void doRestore(View v, int which) {
		if (DEBUG) Log.d(TAG, "doRestore");
		// Always do backup.
		File backup = new File(mSavePath.getAbsolutePath() + ".backup");
		if (backup.exists())
			deleteTree(backup);
		copyDirectory(mSavePath, backup);

		RelativeLayout rl = (RelativeLayout) v.getParent();
		if (rl == null || mButtons == null)
			return;
		BackupedButton bb = null;
		for (BackupedButton b : mButtons) {
			if (rl == b.getLayout()) {
				bb = b;
				break;
			}
		}
		if (bb == null)
			return;

		File save = new File(mSavePath, "files/Vault" + (which + 1) + ".sav");
		copyFile(bb.getFile(), save);
		updateSaveButtons();
	}
	
	public void onRestoreClick(View v) {
		final View passV = v;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("This operation will overwrite your save by this backup");
		String [] saveSlots = new String [] {"Save 1", "Save 2", "Save 3"};
		builder.setItems(saveSlots, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				doRestore(passV, which);
			}
		});
		builder.setNegativeButton("Cancel", null);
		builder.create().show();
	}

	public long getFileDate(File savFile) {
		if (savFile.exists()) {
			return savFile.lastModified();
		}
		return 0;
	}
	
	public void onBackupClick(View v) {
		if (mSavePath == null || !mSavePath.exists())
			return;
		File saveFile = new File(mSavePath, "/files/" + (String)v.getTag());
		int index = mIndex.getAndIncrement();
		long date = getFileDate(saveFile);
		if (date == 0)
			return;

		BackupedButton bb = newBB(index, date);
		copyFile(saveFile, bb.getFile());
	}

	private void doDelete(View v) {
		RelativeLayout rl = (RelativeLayout) v.getParent();
		if (rl == null || mButtons == null)
			return;
		BackupedButton bb = null;
		for (BackupedButton b : mButtons) {
			if (rl == b.getLayout()) {
				bb = b;
				break;
			}
		}
		if (bb == null)
			return;
		mButtons.remove(bb);
		deleteTree(bb.getFile().getParentFile());

		getLatestIndex();
		loadBackupButtons();
	}

	public void onDeleteClick(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Confirm");
		builder.setMessage("This operation will delete one of your save backup");
		final View passV = v;
		builder.setPositiveButton("Delete", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				doDelete(passV);
			}
		});
		builder.setNegativeButton("Cancel", null);
		builder.create().show();
	}

	public void onEditClick(View v) {
		RelativeLayout rl = (RelativeLayout) v.getParent();
		if (rl == null || mButtons == null)
			return;
		BackupedButton bb = null;
		for (BackupedButton b : mButtons) {
			if (rl == b.getLayout()) {
				bb = b;
				break;
			}
		}
		if (bb == null)
			return;
		File jsonFile = bb.getJsonFile();
		File saveFile = bb.getFile();

		try {
			ShelterSaveParser parser = ShelterSaveParser.getInstance(saveFile, jsonFile);
			Intent intent = new Intent(this, DwellerListActivity.class);
			startActivityForResult(intent, 100);
		} catch (Exception e) {
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode != 100)
			return;
		if (data == null)
			return;
		Bundle bundle = data.getExtras();
		String filename = bundle.getString("filename");
		File file = new File(filename);
		if (!file.exists())
			return;
	}
}