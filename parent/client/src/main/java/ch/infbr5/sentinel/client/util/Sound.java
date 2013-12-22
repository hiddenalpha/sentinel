package ch.infbr5.sentinel.client.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {

	public static void ok() {
		play("/sounds/ok.wav");
	}

	public static void warn() {
		play("/sounds/warn.wav");
	}

	public static void alarm() {
		play("/sounds/alarm.wav");
		
	}

	private static void play(String audiofile) {

		try {
			
			BufferedInputStream inputStream = new BufferedInputStream(Sound.class.getResourceAsStream(audiofile));
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(inputStream);

			// needed for working on GNU/Linux (openjdk) {
			AudioFormat format = audioIn.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			Clip clip = (Clip)AudioSystem.getLine(info);

			clip.open(audioIn);
			clip.start();
			
//			// From file
//			BufferedInputStream inputStream = new BufferedInputStream(Sound.class.getResourceAsStream(audiofile));
//			AudioInputStream stream = AudioSystem.getAudioInputStream(inputStream);
//
//			// At present, ALAW and ULAW encodings must be converted
//			// to PCM_SIGNED before it can be played
//			AudioFormat format = stream.getFormat();
//			if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
//				format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format.getSampleRate(),
//						format.getSampleSizeInBits() * 2, format.getChannels(), format.getFrameSize() * 2, format.getFrameRate(),
//						true); // big endian
//				stream = AudioSystem.getAudioInputStream(format, stream);
//			}
//
//			// Create the clip
//			DataLine.Info info = new DataLine.Info(Clip.class, stream.getFormat(),
//					((int) stream.getFrameLength() * format.getFrameSize()));
//			Clip clip = (Clip) AudioSystem.getLine(info);
//
//			// This method does not return until the audio file is completely
//			// loaded
//			clip.open(stream);
//
//			// Start playing
//			clip.start();
			
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		}

	}
}
