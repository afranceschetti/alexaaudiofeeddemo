package org.csi.demo.alexa.audiofeed.manager;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
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

		AudioFeed textFeed = new AudioFeed("" + (System.currentTimeMillis()-2000) + "_provaTesto");
		textFeed.setStreamUrl(null);
		textFeed.setMainText("prima notizia testo");
		audioFeedList.add(textFeed);

		AudioFeed textFeed2 = new AudioFeed("" + System.currentTimeMillis()+ "_provaTesto2");
		textFeed2.setStreamUrl(null);
		textFeed2.setMainText("Seconda Notizia di testo");
		audioFeedList.add(textFeed2);

		if (limit == null)
			limit = 5;
		// read dir
		File baseDir = new File(Config.getInstance().getAudiofeedBaseDir());
		File[] filesList = baseDir.listFiles();
		int counter = 0;
		if (filesList != null)
			for (File f : filesList) {
				if (!f.isDirectory() && f.getName().endsWith(".mp3")) {
					audioFeedList.add(new AudioFeed(f.getName()));
					counter++;
					if (counter == limit)
						break;
				}
			}

		Collections.sort(audioFeedList, new Comparator<AudioFeed>() {
			@Override
			public int compare(AudioFeed lhs, AudioFeed rhs) {
				// -1 - less than, 1 - greater than, 0 - equal, all inversed for
				// descending
				return lhs.getUpdateDate().getTime() > rhs.getUpdateDate().getTime() ? -1 : (lhs.getUpdateDate().getTime() < rhs.getUpdateDate().getTime()) ? 1 : 0;
			}
		});
		return audioFeedList;
	};

}
