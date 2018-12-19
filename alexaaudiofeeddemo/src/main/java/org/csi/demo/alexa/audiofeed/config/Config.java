package org.csi.demo.alexa.audiofeed.config;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class Config {
	public static final String AUDIOFEED_BASE_DIR = "AUDIOFEED_BASE_DIR";
	public static final String AUDIOFEED_BASE_URL = "AUDIOFEED_BASE_URL";

	private static Map<String, String> params = null;
	private static Config instance = null;

	public static Config getInstance() {
		if (instance == null) {
			instance = new Config();
		}
		return instance;
	}

	private Config() {

		params = new HashMap<String, String>();
		ResourceBundle rb = ResourceBundle.getBundle("AlexaAudiofeedConfig");

		params.put(AUDIOFEED_BASE_DIR, rb.getString(AUDIOFEED_BASE_DIR));
		params.put(AUDIOFEED_BASE_URL, rb.getString(AUDIOFEED_BASE_URL));
	}

	public String getAudiofeedBaseDir() {
		return params.get(AUDIOFEED_BASE_DIR);
	}

	public String getAudiofeedBaseUrl() {
		return params.get(AUDIOFEED_BASE_URL);
	}
}
