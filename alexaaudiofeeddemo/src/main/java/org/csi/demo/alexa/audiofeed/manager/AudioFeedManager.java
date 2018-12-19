package org.csi.demo.alexa.audiofeed.manager;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.csi.demo.alexa.audiofeed.config.Config;
import org.csi.demo.alexa.audiofeed.model.AudioFeed;

public class AudioFeedManager {

	private static AudioFeedManager instance = null;

	public static AudioFeedManager getInstance() {
		if (instance == null) {
			instance = new AudioFeedManager();
		}
		return instance;
	}

	public List<AudioFeed> loadFeedMetadata(Integer limit) {
		List<AudioFeed> audioFeedList = new LinkedList<AudioFeed>();

		if(limit==null)
			limit=5;
		// read dir
		File baseDir = new File(Config.getInstance().getAudiofeedBaseDir());
		File[] filesList = baseDir.listFiles();
		int counter= 0;
		if (filesList != null)
			for (File f : filesList) {
				if (!f.isDirectory() && f.getName().endsWith(".mp3")) {
					audioFeedList.add(new AudioFeed(f.getName()));
					counter++;
					if(counter == limit)
						break;
				}
			}
		return audioFeedList;
	};

}
