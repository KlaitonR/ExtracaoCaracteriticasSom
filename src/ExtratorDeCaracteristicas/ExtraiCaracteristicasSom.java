package ExtratorDeCaracteristicas;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class ExtraiCaracteristicasSom { 
	
	 static byte[] arrFile;  
	 static byte[] audioBytes;  
	 static double[] audioData;  
	 static ByteArrayInputStream bis;  
	 static AudioInputStream audioInputStream;  
	 static AudioFormat format;  
	 static double durationSec;  
	 static double durationMSec;
	 static File file;
	 
	 public static void getFile(File f) {
		 file = f;
	 }
     
     public static double[] extractAmplitudeFromFile(File wavFile) {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    
         try {  
              // create file input stream  
              FileInputStream fis = new FileInputStream(wavFile);  
              // create bytearray from file  
              arrFile = new byte[(int)wavFile.length()];  
              fis.read(arrFile);  
              fis.close();
         } catch (Exception e) {  
              System.out.println("SomeException : " + e.toString());  
         }  
         return extractAmplitudeFromFileByteArray(arrFile);  
    }  
     
    public static double[] extractAmplitudeFromFileByteArray(byte[] arrFile) {  
          //System.out.println("\nFile : "+file.getName()+""+arrFile.length);  
         bis = new ByteArrayInputStream(arrFile);  
         return extractAmplitudeFromFileByteArrayInputStream(bis);  
    }  

    public static double[] extractAmplitudeFromFileByteArrayInputStream(ByteArrayInputStream bis) {  
         try {  
              audioInputStream = AudioSystem.getAudioInputStream(bis);  
         } catch (UnsupportedAudioFileException e) {  
              System.out.println("unsupported file type, during extract amplitude");  
              e.printStackTrace();  
         } catch (IOException e) {  
              System.out.println("IOException during extracting amplitude");  
              e.printStackTrace();  
         }  
         // float milliseconds = (long) ((audioInputStream.getFrameLength() *  
         // 1000) / audioInputStream.getFormat().getFrameRate());  
         // durationSec = milliseconds / 1000.0;  
         return extractAmplitudeDataFromAudioInputStream(audioInputStream);  
    }  
    
    public static double[] extractAmplitudeDataFromAudioInputStream(AudioInputStream audioInputStream) {  
         format = audioInputStream.getFormat();  
         audioBytes = new byte[(int) (audioInputStream.getFrameLength() * format.getFrameSize())];  
         // calculate durations  
         durationMSec = (long) ((audioInputStream.getFrameLength() * 1000) / audioInputStream.getFormat().getFrameRate());  
         durationSec = durationMSec / 1000.0;  
         // System.out.println("The current signal has duration "+durationSec+" Sec");  
         try {  
              audioInputStream.read(audioBytes);  
         } catch (IOException e) {  
              System.out.println("IOException during reading audioBytes");  
              e.printStackTrace();  
         }  
         return extractAmplitudeDataFromAmplitudeByteArray(format, audioBytes);  
    }  
    
    public static double[] extractAmplitudeDataFromAmplitudeByteArray(AudioFormat format, byte[] audioBytes) {  
    	
    	double[] caracteristicas = new double[7];
		
		double amplitude_cachorro_1 = 0;
		double amplitude_cachorro_2 = 0;
		double amplitude_cachorro_3 = 0;
		double amplitude_gato_1 = 0;
		double amplitude_gato_2 = 0;
		double amplitude_gato_3 = 0; 
         // convert  
         // TODO: calculate duration here  
         audioData = null;  
         if (format.getSampleSizeInBits() == 16) {  
              int nlengthInSamples = audioBytes.length / 2;  
              audioData = new double[nlengthInSamples];  
              if (format.isBigEndian()) {  
                   for (int i = 0; i < nlengthInSamples; i++) {  
                        /* First byte is MSB (high order) */  
                        int MSB = audioBytes[2 * i];  
                        /* Second byte is LSB (low order) */  
                        int LSB = audioBytes[2 * i + 1];  
                        audioData[i] = MSB << 8 | (255 & LSB); 
//                        if(audioData[i] )
                   }  
              } else {  
                   for (int i = 0; i < nlengthInSamples; i++) {  
                        /* First byte is LSB (low order) */  
                        int LSB = audioBytes[2 * i];  
                        /* Second byte is MSB (high order) */  
                        int MSB = audioBytes[2 * i + 1];  
                        audioData[i] = MSB << 8 | (255 & LSB);  
                   }  
              }  
         
         } else if (format.getSampleSizeInBits() == 8) {  
              int nlengthInSamples = audioBytes.length;  
              audioData = new double[nlengthInSamples];  
              if (format.getEncoding().toString().startsWith("PCM_SIGN")) {  
                   // PCM_SIGNED  
                   for (int i = 0; i < audioBytes.length; i++) {  
                        audioData[i] = audioBytes[i];  
                   }  
              } else {  
                   // PCM_UNSIGNED  
                   for (int i = 0; i < audioBytes.length; i++) {  
                	   if (audioBytes [i] >= 0)
                        audioData[i] = audioBytes[i] + 128;  
//                	   else
//                		   audioData[i] = audioBytes[i] + 128;  
                   }  
              }  
         }
         
         caracteristicas[0] = amplitude_cachorro_1;
         caracteristicas[1] = amplitude_cachorro_2;
         caracteristicas[2] = amplitude_cachorro_3;
         caracteristicas[3] = amplitude_gato_1;
         caracteristicas[4] = amplitude_gato_2;
         caracteristicas[5] = amplitude_gato_3;
         //APRENDIZADO SUPERVISIONADO - JÁ SABE QUAL A CLASSE NAS IMAGENS DE TREINAMENTO
         //caracteristicas[6] = f.getName().charAt(0)=='e'?0:1;
         
         if(file.getName().charAt(0)=='d')
         	caracteristicas[6] = 0;
         else if(file.getName().charAt(0)=='c')
         	caracteristicas[6] = 1;
         
//         for(int i = 0; i<=audioData.length;i++) {
//        	 System.out.println("File: " + file.getName() + "- Dados:" + audioData[i]);
//         }
         
         return audioData;  
    }  
    
    public static void extrair() {
		
	    // Cabeçalho do arquivo Weka
		String exportacao = "@relation caracteristicas\n\n";
		exportacao += "@attribute amplitude_cachorro_1 real\n";
		exportacao += "@attribute amplitude_cachorro_2 real\n";
		exportacao += "@attribute amplitude_cachorro_3 real\n";
		exportacao += "@attribute amplitude_gato_1 real\n";
		exportacao += "@attribute amplitude_gato_2 real\n";
		exportacao += "@attribute amplitude_gato_3 real\n";
		exportacao += "@attribute classe {Cachorro, Gato}\n\n";
		exportacao += "@data\n";
	        
	    // Diretório onde estão armazenadas as imagens
	    File diretorio = new File("src/sonsTrain");
	    File[] arquivos = diretorio.listFiles();
	    
        // Definição do vetor de características
        double[][] caracteristicas = new double[210][7];
        
        // Percorre todas as imagens do diretório
        int cont = -1;
        for (File som : arquivos) {
        	
        	getFile(som);
        	cont++;
        	caracteristicas[cont] = extractAmplitudeFromFile(som);
        	
        	String classe = caracteristicas[cont][6] == 0 ?"Cachorro":"Gato";
        	
        	System.out.println("amplitude_cachorro_1: " + caracteristicas[cont][0] 
            		+ " - amplitude_cachorro_2: " + caracteristicas[cont][1] 
            		+ " - amplitude_cachorro_3: " + caracteristicas[cont][2] 
            		+ " - amplitude_gato_1: " + caracteristicas[cont][3] 
            		+ " - amplitude_gato_2: " + caracteristicas[cont][4] 
            		+ " - amplitude_gato_3: " + caracteristicas[cont][5] 
            		+ " - Classe: " + classe);
        	
        	exportacao += caracteristicas[cont][0] + "," 
                    + caracteristicas[cont][1] + "," 
        		    + caracteristicas[cont][2] + "," 
                    + caracteristicas[cont][3] + "," 
        		    + caracteristicas[cont][4] + "," 
                    + caracteristicas[cont][5] + "," 
                    + classe + "\n";
        }
        
     // Grava o arquivo ARFF no disco
        try {
        	File arquivo = new File("caracteristicas.arff");
        	FileOutputStream f = new FileOutputStream(arquivo);
        	f.write(exportacao.getBytes());
        	f.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

