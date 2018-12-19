package org.csi.demo.alexa.audiofeed.util.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSonHelper {
	private static Gson gson;

	public static Gson getInstance() {
		if (gson == null)
			gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'.0Z'").create();
		return gson;
	}


}
