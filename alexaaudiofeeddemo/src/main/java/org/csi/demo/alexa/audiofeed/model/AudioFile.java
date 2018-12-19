package org.csi.demo.alexa.audiofeed.model;

import java.io.File;
import java.util.Date;

import org.csi.demo.alexa.audiofeed.util.AudioNewsHelper;

public class AudioFile {
	private String name;
	private String filename;
	private Date date;
	private String streamUrl;
	private Long size;

	public AudioFile() {
		super();
	}

	public AudioFile(File audiofile) {
		super();

		this.setName(AudioNewsHelper.getNameFromFilename(audiofile.getName()));
		this.setFilename(audiofile.getName());
		this.setDate(new Date(audiofile.lastModified()));
		this.setStreamUrl(AudioNewsHelper.getAudioStreamUrlFromFilename(audiofile.getName()));
		this.setSize(audiofile.length());

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getStreamUrl() {
		return streamUrl;
	}

	public void setStreamUrl(String streamUrl) {
		this.streamUrl = streamUrl;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

}
