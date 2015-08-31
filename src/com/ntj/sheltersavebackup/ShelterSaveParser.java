package com.ntj.sheltersavebackup;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShelterSaveParser {
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

	public final static class Dweller {
		public static final String SERIALIZED_ID = "serializeId";
		public static final String NAME = "name";
		public static final String LAST_NAME = "lastName";
		public static final String HAPPINESS = "happiness"; //object
		public static final String HEALLTH = "health"; //object
		public static final String EXPERIENCE = "experience"; //object
		public static final String RELATIONS = "relations"; //object
		public static final String EQUIPMENT = "equipment"; //object
		public final static class equipment {
		/*
		5.1.8.1 "storage" object
		5.1.8.1.1 "resources" object
		5.1.8.1.2 "bonus" object
		5.1.8.2 "inventory" 
		5.1.8.2.1 "items" []
		*/
		}
		public static final String GENDER = "gender";
		public static final String STATS = "stats"; //object
		//"stats" array
		public static final String PREGNANT = "pregnant"; //object
		public static final String BABY_READY = "babyReady"; //object

		private int mSerializedId;
		private String mName;
		private String mLastName;
		private int mGender;
		private Experience mExperience;
		private Special mSpecial;
		
		class Experience {
			public Experience(JSONObject root) throws JSONException {
				mExperienceValue = root.getInt("experienceValue");
				mCurrentLevel = root.getInt("currentLevel");
			}
			int mExperienceValue;
			int mCurrentLevel;
		}

		class Special {
			int s;
			int p;
			int e;
			int c;
			int i;
			int a;
			int l;
			int sm;
			int pm;
			int em;
			int cm;
			int im;
			int am;
			int lm;

			Special (JSONArray root) throws JSONException {
				// special
				JSONObject obj;
				obj = root.getJSONObject(1);
				s = obj.getInt("value");
				sm = obj.getInt("mod");
				obj = root.getJSONObject(2);
				p = obj.getInt("value");
				pm = obj.getInt("mod");
				obj = root.getJSONObject(3);
				e = obj.getInt("value");
				em = obj.getInt("mod");
				obj = root.getJSONObject(4);
				c = obj.getInt("value");
				cm = obj.getInt("mod");
				obj = root.getJSONObject(5);
				i = obj.getInt("value");
				im = obj.getInt("mod");
				obj = root.getJSONObject(6);
				a = obj.getInt("value");
				am = obj.getInt("mod");
				obj = root.getJSONObject(7);
				l = obj.getInt("value");
				lm = obj.getInt("mod");
			}

			@Override
			public String toString() {
				return String.format("special=%d%d%d%d%d%d%d(%d%d%d%d%d%d%d)",
						s, p, e, c, i, a, l, sm, pm, em, cm, im, am, lm);
			}
		}
		
		public Dweller(JSONObject root) throws JSONException {
			mName = root.getString(NAME);
			mLastName = root.getString(LAST_NAME);
			mSerializedId = root.getInt(SERIALIZED_ID);
			mGender = root.getInt(GENDER);
			mExperience = new Experience(root.getJSONObject(EXPERIENCE));
			mSpecial = new Special(root.getJSONObject(STATS).getJSONArray(STATS));
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
	}

	final JSONObject mRoot;

	public ShelterSaveParser(String jsonString) throws JSONException {
		mRoot = new JSONObject(jsonString);
	}

	static class Dwellers {
		public static final String DWELLERS = "dwellers";
		private List<Dweller> mList;

		public Dwellers(JSONObject dwellersRoot) throws JSONException {
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

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (Dweller d : mList) {
				sb.append(d.toString()).append("\n");
			}
			return sb.toString();
		}
	}

	public Dwellers parse() throws JSONException {
		if (mRoot.has(Level1.DWELLERS)) {
			JSONObject dwellersRoot = mRoot.getJSONObject(Level1.DWELLERS);
			return new Dwellers(dwellersRoot);
		}
		return null;
	}
}
