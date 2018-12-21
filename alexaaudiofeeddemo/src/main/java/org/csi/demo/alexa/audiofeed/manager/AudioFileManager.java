package org.csi.demo.alexa.audiofeed.manager;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.csi.demo.alexa.audiofeed.config.Config;
import org.csi.demo.alexa.audiofeed.model.AudioFile;

public class AudioFileManager {

	private static AudioFileManager instance = null;

	public static AudioFileManager getInstance() {
		if (instance == null) {
			instance = new AudioFileManager();
		}
		return instance;
	}

	public List<AudioFile> loadAudioFile(Integer limit) {
		List<AudioFile> audiofileList = new LinkedList<AudioFile>();

		if(limit==null)
			limit=50;
		// read dir
		File baseDir = new File(Config.getInstance().getAudiofeedBaseDir());
		File[] filesList = baseDir.listFiles();
		int counter= 0;
		if (filesList != null)
			for (File f : filesList) {
				if (!f.isDirectory() && (f.getName().endsWith(".mp3") || f.getName().endsWith(".txt"))) {
					audiofileList.add(new AudioFile(f));
					counter++;
					if(counter == limit)
						break;
				}
			}
		return audiofileList;
	};

}
