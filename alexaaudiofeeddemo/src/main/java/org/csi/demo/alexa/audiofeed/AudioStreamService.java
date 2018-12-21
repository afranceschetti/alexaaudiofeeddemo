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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.csi.demo.alexa.audiofeed.config.Config;
import org.csi.demo.alexa.audiofeed.util.AudioNewsHelper;
import org.csi.demo.alexa.audiofeed.util.MediaStreamer;

@Path("/stream")
public class AudioStreamService {
	final int chunk_size = 1024 * 1024; // 1MB chunks

	@GET
	@Path("/audionews/{filename}")
	@Produces("audio/mp3")
	public Response streamAudio(@HeaderParam("Range") String range, @PathParam("filename") String filename) throws Exception {
		File baseDir = new File(Config.getInstance().getAudiofeedBaseDir());
		File audio = new File(baseDir, filename);
		if (!audio.getParent().equals(baseDir.getPath()))
			return Response.status(Status.FORBIDDEN).build();
		return buildStream(audio, range);
	}

	private Response buildStream(final File asset, final String range) throws Exception {
		// range not requested : Firefox, Opera, IE do not send range headers
		if (range == null) {
			StreamingOutput streamer = new StreamingOutput() {
				@Override
				public void write(final OutputStream output) throws IOException, WebApplicationException {

					final FileChannel inputChannel = new FileInputStream(asset).getChannel();
					final WritableByteChannel outputChannel = Channels.newChannel(output);
					try {
						inputChannel.transferTo(0, inputChannel.size(), outputChannel);
					} finally {
						// closing the channels
						inputChannel.close();
						outputChannel.close();
					}
				}
			};
			return Response.ok(streamer).header(HttpHeaders.CONTENT_LENGTH, asset.length()).build();
		}

		String[] ranges = range.split("=")[1].split("-");
		final int from = Integer.parseInt(ranges[0]);
		/**
		 * Chunk media if the range upper bound is unspecified. Chrome sends
		 * "bytes=0-"
		 */
		int to = chunk_size + from;
		if (to >= asset.length()) {
			to = (int) (asset.length() - 1);
		}
		if (ranges.length == 2) {
			to = Integer.parseInt(ranges[1]);
		}

		final String responseRange = String.format("bytes %d-%d/%d", from, to, asset.length());
		final RandomAccessFile raf = new RandomAccessFile(asset, "r");
		raf.seek(from);

		final int len = to - from + 1;
		final MediaStreamer streamer = new MediaStreamer(len, raf);
		Response.ResponseBuilder res = Response.ok(streamer).header("Accept-Ranges", "bytes").header("Content-Range", responseRange)
				.header(HttpHeaders.CONTENT_LENGTH, streamer.getLenth()).header(HttpHeaders.LAST_MODIFIED, new Date(asset.lastModified()));
		return res.build();
	}

	@GET
	@Path("/textnews/{filename}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response streamText(@PathParam("filename") String filename) throws Exception {
		File baseDir = new File(Config.getInstance().getAudiofeedBaseDir());
		File textfile = new File(baseDir, filename);
		if (!textfile.getParent().equals(baseDir.getPath()))
			return Response.status(Status.FORBIDDEN).build();

		String news = AudioNewsHelper.getTextNewsContent(filename);
//		try (BufferedReader br = new BufferedReader(new FileReader(textfile))) {
//
//			String sCurrentLine;
//
//			while ((sCurrentLine = br.readLine()) != null) {
//				news += sCurrentLine;
//			}
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		return Response.ok(news).build();
	}

}
