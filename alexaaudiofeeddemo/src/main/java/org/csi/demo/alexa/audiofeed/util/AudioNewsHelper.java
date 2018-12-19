package org.csi.demo.alexa.audiofeed.util;

import java.util.Date;
import java.util.UUID;

import org.csi.demo.alexa.audiofeed.config.Config;

public class AudioNewsHelper {

	private AudioNewsHelper() {

	}

	public static String getNameFromFilename(String filename) {
		String[] title_date = filename.replace(".mp3", "").split("_");
		return title_date[1].replaceAll("-", " ");
	}

	public static Date getDateFromFilename(String filename) {
		String[] title_date = filename.replace(".mp3", "").split("_");
		return new Date(Long.parseLong(title_date[0]));
	}

	public static String getAudioStreamUrlFromFilename(String filename) {
		return Config.getInstance().getAudiofeedBaseUrl() + filename;
	}

	public static String createFilenameFromTitle(String title) {
		return System.currentTimeMillis() + "_" + title.replaceAll(" ", "-");
	}

	public static String getUUIDFromFilename(String filename) {
		return UUID.nameUUIDFromBytes(filename.getBytes()).toString();
	}
}
