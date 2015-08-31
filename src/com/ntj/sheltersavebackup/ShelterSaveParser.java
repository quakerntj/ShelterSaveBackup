package com.ntj.sheltersavebackup;

import java.io.File;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.crypto.NoSuchPaddingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShelterSaveParser extends JsonRoot {
	static ShelterSaveParser sShelterSaveParser;
	private Crypter mDecrypter;
	private File mSaveFile;
	private File mJsonFile;

	public final static class Level1 {
		public static final String TIME_MGR = "timeMgr";
		public static final String LOCAL_NOTIFICATION_MGR = "localNotificationMgr";
		public static final String TASK_MGR = "taskMgr";
		public static final String RATING_MGR = "ratingMgr";
		public static final String DWELLERS = "dwellers";
		public static final String CONSTRUCT_MGR = "constructMgr";
		public static final String VAULT = "vault";
		public static final String DWELLER_SPAWNER = "dwellerSpawner";
		public static final String DEVICE_NAME = "deviceName";
		public static final String TUTORIAL_MANAGER = "tutorialManager";
		public static final String OBJECTIVE_MGR = "objectiveMgr";
		public static final String UNLOCKABLE_MGR = "unlockableMgr";
		public static final String SURVIVAL_W = "survivalW";
		public static final String HAPPINESS_MANAGER = "happinessManager";
		public static final String REFUGEE_SPAWNER = "refugeeSpawner";
		public static final String LUNCH_BOX_COLLECT_WINDOW = "LunchBoxCollectWindow";
		public static final String DEATH_CLAW_MANAGER = "DeathclawManager";
		public static final String PROMO_CODES_WINDOW = "PromoCodesWindow";
		public static final String SWREV_EVENTS_MANAGER= "swrveEventsManager";
	}

	public final class Dweller extends JsonRoot {
		public static final String SERIALIZED_ID = "serializeId";
		public static final String NAME = "name";
		public static final String LAST_NAME = "lastName";
		public static final String HAPPINESS = "happiness"; //object
		public static final String HEALLTH = "health"; //object
		public static final String EXPERIENCE = "experience"; //object
		public static final String RELATIONS = "relations"; //object
		public static final String EQUIPMENT = "equipment"; //object
		public static final String GENDER = "gender";
		public static final String STATS = "stats"; //object
		public static final String PREGNANT = "pregnant"; //object
		public static final String BABY_READY = "babyReady"; //object
		public static final String EQUIPED_OUTFIT = "equipedOutfit"; //object
		public static final String EQUIPED_WEAPON = "equipedWeapon"; //object

		public int mSerializedId;
		public String mName;
		public String mLastName;
		public int mGender;
		public Experience mExperience;
		public Special mSpecial;
		public Item mEquipedOutfit;
		public Item mEquipedWeapon;
		public Equipment mEquipment;
		
		class Experience extends JsonRoot {
			int mExperienceValue;
			int mCurrentLevel;

			public Experience(JSONObject root) throws JSONException {
				super(root);
				mExperienceValue = root.getInt("experienceValue");
				mCurrentLevel = root.getInt("currentLevel");
			}

			@Override
			void update() throws JSONException {
			}
		}

		class Special extends JsonRoot {
			public final int [] special = new int [7];
			public final int [] specialMod = new int [7];

			Special (JSONArray root) throws JSONException {
				super(root);
				// special
				JSONObject obj;
				for (int i = 0; i < 7; i++) {
					obj = root.getJSONObject(i+1);
					special[i] = obj.getInt("value");
					specialMod[i] = obj.getInt("mod");
				}
			}

			@Override
			public String toString() {
				StringBuilder sb = new StringBuilder("special=");
				for (int i = 0; i < 7; i++) {
					sb.append(special[i]);
				}
				sb.append("(");
				for (int i = 0; i < 7; i++) {
					sb.append(specialMod[i]);
				}
				sb.append(")");
				return sb.toString();
			}

			@Override
			void update() throws JSONException {
				JSONObject obj;
				for (int i = 0; i < 7; i++) {
					obj = mRootArray.getJSONObject(i+1);
					obj.put("value", special[i]);
				}
			}
		}

		public class Item extends JsonRoot {
			public String mId;
			public String mType;

			Item(JSONObject root) throws JSONException {
				super(root);
				mId = mRoot.getString("id");
				mType = mRoot.getString("type");
			}

			@Override
			void update() throws JSONException {
				mRoot.put("id", mId);
				mRoot.put("type", mType);
			}
		}

		public class Equipment extends JsonRoot {
			private static final String INVENTORY = "inventory";
			private static final String ITEMS = "items";
			private List<Item> mItems = new LinkedList<Item>();
			private JSONArray mItemsRoot;

			Equipment(JSONObject root) throws JSONException {
				super(root);
				JSONObject inventory = mRoot.getJSONObject(INVENTORY);
				mItemsRoot = inventory.getJSONArray(ITEMS);
				final int N = mItemsRoot.length();
				for (int i = 0; i < N; i++) {
					Item item = new Item(mItemsRoot.getJSONObject(i));
					mItems.add(item);
				}
			}

			public List<Item> getList() {
				return mItems;
			}

			@Override
			void update() throws JSONException {
				for (Item i : mItems) {
					i.update();
				}
			}
		}

		public Dweller(JSONObject root) throws JSONException {
			super(root);
			mName = root.getString(NAME);
			mLastName = root.getString(LAST_NAME);
			mSerializedId = root.getInt(SERIALIZED_ID);
			mGender = root.getInt(GENDER);
			mExperience = new Experience(root.getJSONObject(EXPERIENCE));
			mSpecial = new Special(root.getJSONObject(STATS).getJSONArray(STATS));
			mEquipedOutfit = new Item(root.getJSONObject(EQUIPED_OUTFIT));
			mEquipedWeapon = new Item(root.getJSONObject(EQUIPED_WEAPON));
			mEquipment = new Equipment(root.getJSONObject(EQUIPMENT));
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(mName).append(" ").append(mLastName).append("\n");
			sb.append("lv ").append(mExperience.mCurrentLevel).append(" ");
			sb.append("exp ").append(mExperience.mExperienceValue).append("\n");
			sb.append(mSpecial.toString());
			return sb.toString();
		}

		@Override
		void update() throws JSONException {
			mSpecial.update();
			mEquipedOutfit.update();
			mEquipedWeapon.update();
			mEquipment.update();
		}
	}

	private final JSONObject mSaveRoot;
	public Dwellers mDwellers;

	public static ShelterSaveParser getInstance(File saveFile, File jsonFile) throws JSONException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException {
		Crypter decrypter = null;
		decrypter = new Crypter(Crypter.hexStringToByteArray(Crypter.HEX_KEY),
				Crypter.hexStringToByteArray(Crypter.HEX_IV));
		String jsonString = decrypter.decrypt(saveFile, jsonFile);
		sShelterSaveParser = new ShelterSaveParser(jsonString);
		sShelterSaveParser.mSaveFile = saveFile;
		sShelterSaveParser.mJsonFile = jsonFile;
		sShelterSaveParser.mDecrypter = decrypter;
		return sShelterSaveParser;
	}

	public static ShelterSaveParser getInstance() {
		return sShelterSaveParser;
	}

	private ShelterSaveParser(String jsonString) throws JSONException {
		super(new JSONObject(jsonString));
		mSaveRoot = mRoot;
		parse();
	}

	class Dwellers extends JsonRoot {
		public static final String DWELLERS = "dwellers";
		private List<Dweller> mList;

		public Dwellers(JSONObject dwellersRoot) throws JSONException {
			super(dwellersRoot);
			if (dwellersRoot.has(DWELLERS)) {
				JSONArray dwellersArray = dwellersRoot.getJSONArray(Dwellers.DWELLERS);
				final int N = dwellersArray.length();
				List<Dweller> list = new ArrayList<Dweller>();
				for (int i = 0; i < N; i++) {
					JSONObject obj = dwellersArray.getJSONObject(i);
					Dweller dweller = new Dweller(obj);
					list.add(dweller);
				}
				mList = list;
			}
		}

		public List<Dweller> getList() {
			return mList;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (Dweller d : mList) {
				sb.append(d.toString()).append("\n");
			}
			return sb.toString();
		}

		@Override
		void update() throws JSONException {
		}
	}

	public Dwellers getDwellers() {
		return mDwellers;
	}

	public void parse() throws JSONException {
		if (mSaveRoot.has(Level1.DWELLERS)) {
			JSONObject dwellersRoot = mSaveRoot.getJSONObject(Level1.DWELLERS);
			mDwellers = new Dwellers(dwellersRoot);
		}
	}

	@Override
	void update() throws JSONException {
		String big = mSaveRoot.toString();
		try {
			mDecrypter.encrypt(big, mJsonFile, mSaveFile);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
	}

	void close() {
		sShelterSaveParser = null;
	}
}
