package ch.infbr5.sentinel.client.util;

import java.io.BufferedInputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

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

   private static void play(final String audiofile) {
      try {
         final BufferedInputStream inputStream = new BufferedInputStream(Sound.class.getResourceAsStream(audiofile));
         final AudioInputStream audioIn = AudioSystem.getAudioInputStream(inputStream);

         // needed for working on GNU/Linux (openjdk) {
         final AudioFormat format = audioIn.getFormat();
         final DataLine.Info info = new DataLine.Info(Clip.class, format);
         final Clip clip = (Clip) AudioSystem.getLine(info);

         clip.open(audioIn);
         clip.start();
      } catch (final Exception e) {
         e.printStackTrace();
      }
   }

}
