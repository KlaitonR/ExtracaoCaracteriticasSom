package ExtratorDeCaracteristicas;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;


import util.WaveData;

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
	 
	 public static Color getColor(double power) {
	        double H = power * 0.4; // Hue (note 0.4 = Green, see huge chart below)
	        double S = 1.0; // Saturation
	        double B = 1.0; // Brightness

	        return Color.getHSBColor((float)H, (float)S, (float)B);
	    }
	 
	 public static void plotGrafico(int nX, int nY, double maxAmp, double minAmp, double plotData[][]) {
		 
		 try {
		//Normalization
         double diff = maxAmp - minAmp;
         for (int i = 0; i < nX; i++){
             for (int j = 0; j < nY; j++){
                 plotData[i][j] = (plotData[i][j]-minAmp)/diff;
             }
         }

         //plot image
         BufferedImage theImage = new BufferedImage(nX, nY, BufferedImage.TYPE_INT_RGB);
         double ratio;
         for(int x = 0; x<nX; x++){
             for(int y = 0; y<nY; y++){
                 ratio = plotData[x][y];

                 //theImage.setRGB(x, y, new Color(red, green, 0).getRGB());
                 Color newColor = getColor(1.0-ratio);
                 theImage.setRGB(x, y, newColor.getRGB());
             }
         }
         
         File outputfile = new File(file.getName()+".png");
         ImageIO.write(theImage, "png", outputfile);

     } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
     }
	 }
     
     public static double[] extractAmplitudeFromFile(File wavFile) {   
    	 
    	 double[] caracteristicas = new double[3];
    	 double amplitude_cachorro = 0;
 		 double amplitude_gato = 0;
    	 
    	 try{
	    	  //get raw double array containing .WAV data
	         WaveData audioTest = new WaveData(file.getPath(), false); //true para exibir informações do arquivo
	         double[] rawData = audioTest.getByteArray();
	         int length = rawData.length;
	
	         //initialize parameters for FFT
	         int WS = 2048; //WS = window size
	         int OF = 8;    //OF = overlap factor
	         int windowStep = WS/OF;
	
	         //calculate FFT parameters
//	         double SR = audioTest.getSR();
//	         double time_resolution = WS/SR;
//	         double frequency_resolution = SR/WS;
//	         double highest_detectable_frequency = SR/2.0;
//	         double lowest_detectable_frequency = 5.0*SR/WS;
//	
//	         System.out.println("time_resolution:              " + time_resolution*1000 + " ms");
//	         System.out.println("frequency_resolution:         " + frequency_resolution + " Hz");
//	         System.out.println("highest_detectable_frequency: " + highest_detectable_frequency + " Hz");
//	         System.out.println("lowest_detectable_frequency:  " + lowest_detectable_frequency + " Hz");
	
	         //initialize plotData array
	         int nX = (length-WS)/windowStep;
	         int nY = WS/2 + 1;
	         double[][] plotData = new double[nX][nY]; 
	
	         //apply FFT and find MAX and MIN amplitudes
	
	         double maxAmp = Double.MIN_VALUE;
	         double minAmp = Double.MAX_VALUE;
	         
	         double amplitudeMedia = 0;
	
	         double amp_square;
	
	         double[] inputImag = new double[length];
	         double threshold = 1.0;
	
	         for (int i = 0; i < nX; i++){
	             Arrays.fill(inputImag, 0.0);
	             double[] WS_array = FFT.fft(Arrays.copyOfRange(rawData, i*windowStep, i*windowStep+WS), inputImag, true);
	             for (int j = 0; j < nY; j++){
	                 amp_square = (WS_array[2*j]*WS_array[2*j]) + (WS_array[2*j+1]*WS_array[2*j+1]);
	                 if (amp_square == 0.0){
	                     plotData[i][j] = amp_square;
	                 }
	                 else{
	                	 plotData[i][nY-j-1] = 10 * Math.log10(Math.max(amp_square,threshold));
//	                	 plotData[i][nY-j-1] = 10 * Math.log10(amp_square);
	                 }
	
	                 //find MAX and MIN amplitude
	                 if (plotData[i][j] > maxAmp)
	                     maxAmp = plotData[i][j];
	                 else if (plotData[i][j] < minAmp)
	                     minAmp = plotData[i][j];
	                 
	                 //find media amplitude
	                 if (plotData[i][j] > maxAmp)
	                     maxAmp = plotData[i][j];
	                 else if (plotData[i][j] < minAmp)
	                     minAmp = plotData[i][j];
	                 
	                 amplitudeMedia += plotData[i][j];
	
	             }
	         }
	
	         System.out.println("---------------------------------------------------");
	         System.out.println("Maximum amplitude: " + maxAmp);
	         System.out.println("Minimum amplitude: " + minAmp);
	         System.out.println("---------------------------------------------------");
	
	         amplitudeMedia = amplitudeMedia/plotData.length;
	         
	        
	         System.out.println("Media Amplitude: " + amplitudeMedia);
	         
	         if(amplitudeMedia>28000 && file.getName().charAt(0)=='d') {
	        	 amplitude_cachorro = amplitudeMedia; 
	         }else if (amplitudeMedia>28000 && file.getName().charAt(0)=='c'){
	        	 amplitude_cachorro = amplitudeMedia;
	        	 amplitude_gato = amplitudeMedia;
	         }
	         
	        if(amplitudeMedia<28000 && file.getName().charAt(0)=='c') {
	         	amplitude_gato = amplitudeMedia;
    	 }else if (amplitudeMedia<28000 && file.getName().charAt(0)=='d'){
        	 amplitude_cachorro = amplitudeMedia;
        	 amplitude_gato = amplitudeMedia;
         }
	         
	          
	          caracteristicas[0] = amplitude_cachorro;
	          caracteristicas[1] = amplitude_gato;
	          
	          if(file.getName().charAt(0)=='d')
	          	caracteristicas[2] = 0;
	          else if(file.getName().charAt(0)=='c')
	          	caracteristicas[2] = 1;
	          
//	          for(int i = 0; i<audioData.length;i++) {
//	         	 System.out.println("File: " + file.getName() + "- Dados:" + audioData[i]);
//	          }
	          
	         plotGrafico(nX, nY, maxAmp, minAmp, plotData);
	
	     } catch (IOException e) {
	         e.printStackTrace();
	     }
         
         return caracteristicas;  
   
    }  
    
    public static void extrair() {
		
	    // Cabeçalho do arquivo Weka
		String exportacao = "@relation caracteristicas\n\n";
		exportacao += "@attribute amplitude_cachorro real\n";
		exportacao += "@attribute amplitude_gato real\n";
		exportacao += "@attribute classe {Cachorro, Gato}\n\n";
		exportacao += "@data\n";
	        
	    // Diretório onde estão armazenadas as imagens
	    File diretorio = new File("src/sonsTrain");
	    File[] arquivos = diretorio.listFiles();
	    
        // Definição do vetor de características
        double[][] caracteristicas = new double[210][3];
        
        // Percorre todas as imagens do diretório
        int cont = -1;
        for (File som : arquivos) {
        	
        	getFile(som);
        	cont++;
        	caracteristicas[cont] = extractAmplitudeFromFile(som);
        	
        	String classe = caracteristicas[cont][2] == 0 ?"Cachorro":"Gato";
        	
        	System.out.println(
        			"amplitude_cachorro: " + caracteristicas[cont][0] 
            		+ " - amplitude_gato: " + caracteristicas[cont][1] +
            		 " - Classe: " + classe);
        	
        	exportacao += caracteristicas[cont][0] + "," 
                    + caracteristicas[cont][1] + "," 
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

