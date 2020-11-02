package util;

import java.io.File;  
import java.io.IOException;  
import javax.sound.sampled.AudioFormat;  
import javax.sound.sampled.AudioInputStream;  
import javax.sound.sampled.AudioSystem;  
import javax.sound.sampled.DataLine;  
import javax.sound.sampled.LineUnavailableException;  
import javax.sound.sampled.SourceDataLine;  
import javax.sound.sampled.UnsupportedAudioFileException;  

public class Player implements Runnable {  
     static final int BUF_SIZE = 16384;  
     private AudioInputStream audioInputStream;  
     private AudioFormat format;  
     SourceDataLine line;  
     Thread thread;  
     public Player(AudioInputStream audioInputStream) {  
          this.audioInputStream = audioInputStream;  
          format = audioInputStream.getFormat();  
     }  
     public Player(File wavFile) throws UnsupportedAudioFileException, IOException {  
          this.audioInputStream = AudioSystem.getAudioInputStream(wavFile);  
          format = audioInputStream.getFormat();  
     }  
     public void start() {  
          thread = new Thread(this);  
          thread.setName("Playback");  
          thread.start();  
     }  
     public void stop() {  
          thread = null;  
     }  
     private void shutDown(final String message) {  
          if (thread != null) {  
               thread = null;  
          }  
          System.out.println(message);  
     }  
     @Override  
     public void run() {  
          // make sure we have something to play  
          if (audioInputStream == null) {  
               shutDown("No loaded audio to play back");  
               return;  
          }  
          // get an AudioInputStream of the desired format for playback  
          final AudioInputStream playbackInputStream = AudioSystem.getAudioInputStream(format, audioInputStream);  
          if (playbackInputStream == null) {  
               shutDown("Unable to convert stream of format " + audioInputStream + " to format " + format);  
               return;  
          }  
          line = getSourceDataLineForPlayback();  
          // play back the captured audio data  
          final int frameSizeInBytes = format.getFrameSize();  
          final int bufferLengthInFrames = line.getBufferSize() / 8;  
          final int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;  
          final byte[] audioBuffer = new byte[bufferLengthInBytes];  
          int numBytesRead = 0;  
          // start the source data line  
          line.start();  
          while (thread != null) {  
               try {  
                    if ((numBytesRead = playbackInputStream.read(audioBuffer)) == -1) {  
                         break;  
                    }  
                    int numBytesRemaining = numBytesRead;  
                    while (numBytesRemaining > 0) {  
                         numBytesRemaining -= line.write(audioBuffer, 0, numBytesRemaining);  
                    }  
               } catch (final Exception e) {  
                    shutDown("Error during playback: " + e);  
                    break;  
               }  
          }  
          // stop and close the line.  
          if (thread != null) {  
               line.drain();  
          } 
          
          line.stop();  
          line.close();  
          line = null;  
          thread = null;  
     }  
     private SourceDataLine getSourceDataLineForPlayback() {  
          SourceDataLine line;  
          final DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);  
          if (!AudioSystem.isLineSupported(info)) {  
               return null;  
          }  
          // get and open the source data line for playback.  
          try {  
               line = (SourceDataLine) AudioSystem.getLine(info);  
               line.open(format, BUF_SIZE);  
          } catch (final LineUnavailableException ex) {  
               return null;  
          }  
          return line;  
     }  
}  