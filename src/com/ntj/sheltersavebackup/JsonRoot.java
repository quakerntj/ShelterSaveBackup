package com.ntj.sheltersavebackup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

abstract class JsonRoot {
	protected JSONObject mRoot;
	protected JSONArray mRootArray;

	private JsonRoot() {
	}

	public JsonRoot(JSONObject root) {
		mRoot = root;
		mRootArray = null;
	}

	public JsonRoot(JSONArray root) {
		mRoot = null;
		mRootArray = root;
	}

	protected JSONObject getRoot() {
		return mRoot;
	}

	protected JSONArray getRootArray() {
		return mRootArray;
	}

	abstract void update() throws JSONException;
}

