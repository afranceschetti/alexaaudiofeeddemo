/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.csi.demo.alexa.audiofeed;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.csi.demo.alexa.audiofeed.config.Config;
import org.csi.demo.alexa.audiofeed.manager.AudioFileManager;
import org.csi.demo.alexa.audiofeed.model.AudioFile;
import org.csi.demo.alexa.audiofeed.util.AudioNewsHelper;
import org.csi.demo.alexa.audiofeed.util.json.JSonHelper;
import org.glassfish.jersey.media.multipart.FormDataParam;

import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;

@Path("/manage")
public class AudioFileService {

	@GET
	@Path("/news")
	@Produces({ "application/json" })
	public String getAudioFeedJSON() {

		List<AudioFile> audiofiles = AudioFileManager.getInstance().loadAudioFile(null);

		return JSonHelper.getInstance().toJson(audiofiles);
	}

	@POST
	@Path("/news/audio/{newstitle}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ "application/json" })
	public String uploadAudioNews(@FormDataParam("file") InputStream uploadedInputStream, @PathParam("newstitle") String newstitle) {
		String filename = AudioNewsHelper.createFilenameFromTitle(newstitle) + ".mp3";
		String uploadedFileLocation = Config.getInstance().getAudiofeedBaseDir() + filename;
		System.out.println(uploadedFileLocation);
		// save it
		File objFile = new File(uploadedFileLocation);
		if (objFile.exists()) {
			objFile.delete();

		}
		saveToFile(uploadedInputStream, uploadedFileLocation);

		// String output = "{\"result\":\"ok\", \"detail\": \"File uploaded via
		// Jersey based RESTFul Webservice to: " + uploadedFileLocation +"\"}";

		FFprobe ffprobe;
		try {
			ffprobe = new FFprobe("/appserv/jboss/ajb620/alexa/audio/ffmpeg/ffmpeg-4.1-32bit-static/ffprobe");
		
			FFmpegProbeResult probeResult = ffprobe.probe(uploadedFileLocation);

			FFmpegFormat format = probeResult.getFormat();
			System.out.format("%nFile: '%s' ; Format: '%s' ; Duration: %.3fs", 
				format.filename, 
				format.format_long_name,
				format.duration
			);
	
			FFmpegStream stream = probeResult.getStreams().get(0);
			System.out.format("%nCodec: '%s' ; Width: %dpx ; Height: %dpx",
				stream.codec_long_name,
				stream.width,
				stream.height
			);
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		
		return "";

	}

	private void saveToFile(InputStream uploadedInputStream, String uploadedFileLocation) {

		try {
			OutputStream out = null;
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	
	@POST
	@Path("/news/text/{newstitle}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({ "application/json" })
	public String uploadTextNews(@PathParam("newstitle") String newstitle, @FormParam("mainText") String mainText) throws IOException {
		String filename = AudioNewsHelper.createFilenameFromTitle(newstitle) + ".txt";
		String uploadedFileLocation = Config.getInstance().getAudiofeedBaseDir() + filename;
		System.out.println(uploadedFileLocation);
		// save it
		File objFile = new File(uploadedFileLocation);
		if (objFile.exists()) {
			objFile.delete();

		}
		
		List<String> lines = new ArrayList<>();
		lines.add(mainText);
		Files.write(objFile.toPath(), lines, Charset.forName("UTF-8"));

		return "";

	}
	
	@DELETE
	@Path("/news/{filename}")
	@Produces({ "application/json" })
	public Response deleteFile(@PathParam("filename") String filename) {
		File baseDir = new File(Config.getInstance().getAudiofeedBaseDir());
		File audio = new File(baseDir, filename);
		if (!audio.getParent().equals(baseDir.getPath()))
			return Response.status(Status.FORBIDDEN).build();

		boolean ok = audio.delete();
		//System.out.println("delete ok " + ok);

		

		return Response.ok().build();
	}

}
