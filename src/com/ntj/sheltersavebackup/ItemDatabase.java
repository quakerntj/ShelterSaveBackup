package com.ntj.sheltersavebackup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileSystemUtils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.ntj.sheltersavebackup.ShelterSaveParser.Item;

public class ItemDatabase {
	private final static String TAG = "FOSItemDatabase";
	private final static String DB_FILE = "fos_item_db.json";
	public final static String TYPE_WEAPON = "Weapon";
	public final static String TYPE_OUTFIT = "Outfit";

	class DBItem {
		public String type;
		public String id;
		public String showname;
		public String category;
		public String image;
		public void dump(StringBuilder sb) {
			sb.append("type:").append(type);
			sb.append(";id:").append(id);
			sb.append(";showname:").append(showname);
			sb.append(";category:").append(category);
			sb.append(";image:").append(image);
			sb.append(";resid:").append(getDrawable(this));
		}

		public boolean isWeapon() {
			return TYPE_WEAPON.equals(type);
		}
	}

	class DBItems {
		public List<DBItem> mWeaponList;
		public List<DBItem> mOutfitList;
	}

	private static ItemDatabase sItemDatabase;
	public static ItemDatabase getInstance(Context c) {
		if (sItemDatabase == null) {
			sItemDatabase = new ItemDatabase(c);
		}
		return sItemDatabase;
	}

	private DBItems mData;
	private Gson mGson;
	private File mFile;

	public List<DBItem> getWeaponList() {
		return mData.mWeaponList;
	}

	public List<DBItem> getOutfitList() {
		return mData.mOutfitList;
	}

	static public int getDrawable(DBItem item) {
		return getDrawable(item.image);
	}

	static private int getDrawable(String image) {
		int drawable = R.drawable.fist;
		try {
			drawable = (int) R.drawable.class.getField(image).get(null);
		} catch (IllegalAccessException | IllegalArgumentException
				| NoSuchFieldException e) {
			Log.d(TAG, "Can't find drawable:" + image);
		}
		return drawable;
	}
	
	private ItemDatabase(Context context) {
		mGson = new Gson();
		File extpath = Environment.getExternalStorageDirectory();
		if (extpath == null)
			return;
		File folder = new File(extpath, ShelterBackupActivity.BACKUP_PATH);

		mFile = new File(folder, DB_FILE);
		try {
			if (mFile.exists()) {
				Reader reader = new FileReader(mFile);
				read(reader);
				reader.close();
			} else {
				InputStream is = context.getAssets().open(DB_FILE);
				Reader reader = new InputStreamReader(is);
				read(reader);
				reader.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		if (mData == null) {
			mData = new DBItems();
			mData.mWeaponList = new ArrayList<DBItem>(120);
			mData.mOutfitList = new ArrayList<DBItem>(80);
		}
	}

	private void read(Reader reader) {
		Type type = new TypeToken<DBItems>() {}.getType();
		try {
			mData = mGson.fromJson(reader, type);
			reader.close();
		} catch (JsonIOException | JsonSyntaxException | IOException e) {
			e.printStackTrace();
		}
	}

	public DBItem find(Item item) {
		if (TYPE_WEAPON.equals(item.mType)) {
			for (DBItem dbitem : mData.mWeaponList) {
				if (item.mId.equals(dbitem.id))
					return dbitem;
			}
		} else if (TYPE_OUTFIT.equals(item.mType)) {
			for (DBItem dbitem : mData.mOutfitList) {
				if (item.mId.equals(dbitem.id))
					return dbitem;
			}
		}
		return null;
	}

	public void add(Item item) {
		if (find(item) != null) {
			return;
		}

		DBItem dbitem = new DBItem();
		dbitem.id = new String(item.mId);
		dbitem.showname = new String(item.mId);
		dbitem.type = new String(item.mType);
		dbitem.category = "unknown";

		if (TYPE_WEAPON.equals(item.mType)) {
			mData.mWeaponList.add(dbitem);
			dbitem.image = "fist";
		} else if (TYPE_OUTFIT.equals(item.mType)) {
			mData.mOutfitList.add(dbitem);
			dbitem.image = "vaultsuit";
		}
	}

	public void save() {
		try {
			FileWriter writer = new FileWriter(mFile);
			mGson.toJson(mData, writer);
			writer.close();
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		final int outfits = mData.mOutfitList.size();
		final int weapons = mData.mWeaponList.size();
		sb.append("Total size: ").append(outfits + weapons);
		sb.append("\nWeapons:").append(weapons).append("\n");
		for (DBItem i : mData.mWeaponList) {
			i.dump(sb);
			sb.append("\n");
		}
		sb.append("\nOutfits:").append(weapons).append("\n");
		for (DBItem i : mData.mOutfitList) {
			i.dump(sb);
			sb.append("\n");
		}
		return sb.toString();
	}
}