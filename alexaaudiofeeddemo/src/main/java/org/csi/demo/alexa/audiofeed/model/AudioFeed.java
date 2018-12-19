package org.csi.demo.alexa.audiofeed.model;

import java.util.Date;

import org.csi.demo.alexa.audiofeed.util.AudioNewsHelper;
import org.csi.demo.alexa.audiofeed.util.json.JSonHelper;

public class AudioFeed {
	private String uid;
	private Date updateDate;
	private String titleText;
	private String mainText;
	private String streamUrl;
	private String redirectionUrl;

	public AudioFeed() {
		super();
	}

	public AudioFeed(String filename) {
		super();
		this.setUid(AudioNewsHelper.getUUIDFromFilename(filename));
		this.setTitleText(AudioNewsHelper.getNameFromFilename(filename));
		this.setUpdateDate(AudioNewsHelper.getDateFromFilename(filename));
//		
//		String[] title_date = filename.replace(".mp3", "").split("_");
//		this.setTitleText(title_date[1]);
//		
//		
//		Date d = new Date(Long.parseLong(title_date[0]));
//		this.setUpdateDate(d);
		this.setStreamUrl(AudioNewsHelper.getAudioStreamUrlFromFilename(filename));
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getTitleText() {
		return titleText;
	}

	public void setTitleText(String titleText) {
		this.titleText = titleText;
	}

	public String getMainText() {
		return mainText;
	}

	public void setMainText(String mainText) {
		this.mainText = mainText;
	}

	public String getStreamUrl() {
		return streamUrl;
	}

	public void setStreamUrl(String streamUrl) {
		this.streamUrl = streamUrl;
	}

	public String getRedirectionUrl() {
		return redirectionUrl;
	}

	public void setRedirectionUrl(String redirectionUrl) {
		this.redirectionUrl = redirectionUrl;
	}
	
	public String toJson() {
		String json = JSonHelper.getInstance().toJson(this);
		return json;
	}

}
