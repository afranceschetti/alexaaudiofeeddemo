package org.csi.demo.alexa.audiofeed.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import org.csi.demo.alexa.audiofeed.config.Config;

public class AudioNewsHelper {

	private AudioNewsHelper() {

	}

	public static String getNameFromFilename(String filename) {
		String[] title_date = filename.replace(".mp3", "").replace(".txt", "").split("_");
		return title_date[1].replaceAll("-", " ");
	}

	public static String getTypeFromFilename(String filename) {
		return filename.endsWith(".mp3") ? "audio" : "text";
	}

	public static Date getDateFromFilename(String filename) {
		String[] title_date = filename.replace(".mp3", "").split("_");
		return new Date(Long.parseLong(title_date[0]));
	}

	public static String getAudioStreamUrlFromFilename(String filename) {

		return Config.getInstance().getAudioStreamBaseUrl() + (AudioNewsHelper.getTypeFromFilename(filename).equals("audio") ? "audionews/" : "textnews/") + filename;
	}

	public static String createFilenameFromTitle(String title) {
		return System.currentTimeMillis() + "_" + title.replaceAll(" ", "-");
	}

	public static String getUUIDFromFilename(String filename) {
		return UUID.nameUUIDFromBytes(filename.getBytes()).toString();
	}

	public static String getTextNewsContent(String filename) {
		String news = "";
		File baseDir = new File(Config.getInstance().getAudiofeedBaseDir());
		File textfile = new File(baseDir, filename);
		try (BufferedReader br = new BufferedReader(new FileReader(textfile))) {

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				news += sCurrentLine;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return news;
	}
	
	
}
