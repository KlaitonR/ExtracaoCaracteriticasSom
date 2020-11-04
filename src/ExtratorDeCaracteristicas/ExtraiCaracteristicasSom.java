package ExtratorDeCaracteristicas;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import util.WaveData;

public class ExtraiCaracteristicasSom { 
	 
	 static double maxAmp = Double.MIN_VALUE;
     static double minAmp = Double.MAX_VALUE;
     static double f_max = 0;
	 
	 static File file;
	 public static String inf;
	 
	 private static DecimalFormat df = new DecimalFormat("##0.0000");
	 
	 public static void getFile(File f) {
		 file = f;
	 }
	 
	 public static void getInfo(String info) {
		 info += "Amplidute máxima: " + df.format(maxAmp) + " dB \n" +
				 "Freqência: " + f_max +" Hz \n";
		 inf = info;
	 }
	 
	 public static Color getColor(double power) {
	        double H = power * 0.4; // Hue (note 0.4 = Green, see huge chart below)
	        double S = 1.0; // Saturation
	        double B = 1.0; // Brightness

	        return Color.getHSBColor((float)H, (float)S, (float)B);
	    }
     
     public static double[] extractAmplitudeFromFile(File wavFile) {   
    	 
    	 getFile(wavFile);
    	 double[] caracteristicas = new double[5];
    	 double amplitude_cachorro = 0;
    	 double frequencia_cachorro = 0;
 		 double amplitude_gato = 0;
 		 double frequencia_gato = 0;
    	 
    	 try{
	    	  //get raw double array containing .WAV data
	         WaveData audioTest = new WaveData(file.getPath(), true); //true para exibir informações do arquivo
	         double[] rawData = audioTest.getByteArray();
	         int length = rawData.length;
	
	         //initialize parameters for FFT
	         int WS = 2048; //WS = window size
	         int OF = 8;    //OF = overlap factor
	         int windowStep = WS/OF;

	         //initialize plotData array
	         int nX = (length-WS)/windowStep;
	         int nY = WS/2 + 1;
	         double[][] plotData = new double[nX][nY]; 
	
	         //apply FFT and find MAX and MIN amplitude
	          maxAmp = Double.MIN_VALUE;
	          minAmp = Double.MAX_VALUE;
	         int i_max = 0;
	         int n = 0;

	         f_max = 0;
	         double amp_square;
	
	         double[] inputImag = new double[length];
	         double threshold = 1.0;
	
	         for (int i = 0; i < nX; i++){
	             Arrays.fill(inputImag, 0.0);
	             double[] WS_array = FFT.fft(Arrays.copyOfRange(rawData, i*windowStep, i*windowStep+WS), inputImag, true);
	             n = WS_array.length;
	             for (int j = 0; j < nY; j++){
	                 amp_square = (WS_array[2*j]*WS_array[2*j]) + (WS_array[2*j+1]*WS_array[2*j+1]);
	                 if (amp_square == 0.0){
	                     plotData[i][j] = amp_square;
	                 }
	                 else{
	                	 plotData[i][nY-j-1] = 10 * Math.log10(Math.max(amp_square,threshold));
	                 }
	
	                 //find MAX and MIN amplitude
	                 if (plotData[i][j] > maxAmp) {
	                    maxAmp = plotData[i][j];
	                 	i_max = j;
	                 	
	                 }else if (plotData[i][j] < minAmp) {
	                    minAmp = plotData[i][j];
	                 }
	             }
	         }
	
	         //calcular frequência
	         f_max = i_max * audioTest.getSR()/n;
	         
//	         System.out.println("Amplitude máxima: " + maxAmp);
//	         System.out.println("Amplitude mínima: " + minAmp);
//	         System.out.println("Frequencia máxima "+ f_max);
	
	         //Caracteristicas Amplitude
	         if(maxAmp >= 100) {
	        	 amplitude_gato = maxAmp; 
	         }else if (maxAmp>=94 && maxAmp < 100) {
	        	 amplitude_cachorro = maxAmp; 
	        	 amplitude_gato = maxAmp; 
	         }else if (maxAmp < 94) {
	        	 amplitude_cachorro = maxAmp; 
	         }
	         
	       //Caracteristicas frequencia
	         if(f_max >= 3200 && f_max <= 3700) {
	        	 frequencia_cachorro = f_max;
	         }else {
	        	 frequencia_gato = f_max;
	         }
 
	         caracteristicas[0] = amplitude_cachorro;
	         caracteristicas[1] = frequencia_cachorro;
	         caracteristicas[2] = amplitude_gato;
	         caracteristicas[3] = frequencia_gato;
	          
	         if(file.getName().charAt(0)=='d')
	        	 caracteristicas[4] = 0;
	         else if(file.getName().charAt(0)=='c')
	        	 caracteristicas[4] = 1;
	         
	         getInfo(audioTest.info);
	
	     } catch (IOException e) {
	         e.printStackTrace();
	     }
         
         return caracteristicas;  
   
    }  
    
    public static void extrair() {
		
	    // Cabeçalho do arquivo Weka
		String exportacao = "@relation caracteristicas\n\n";
		exportacao += "@attribute amplitude_cachorro real\n";
		exportacao += "@attribute frequencia_cachorro real\n";
		exportacao += "@attribute amplitude_gato real\n";
		exportacao += "@attribute frequencia_gato real\n";
		exportacao += "@attribute classe {Cachorro, Gato}\n\n";
		exportacao += "@data\n";
	        
	    // Diretório onde estão armazenadas as imagens
	    File diretorio = new File("src/sonsTrain");
	    File[] arquivos = diretorio.listFiles();
	    
        // Definição do vetor de características
        double[][] caracteristicas = new double[210][5];
        
        // Percorre todas as imagens do diretório
        int cont = -1;
        for (File som : arquivos) {
        	
        	getFile(som);
        	cont++;
        	caracteristicas[cont] = extractAmplitudeFromFile(som);
        	
        	String classe = caracteristicas[cont][4] == 0 ?"Cachorro":"Gato";
        	
        	System.out.println(
        			"amplitude_cachorro: " + caracteristicas[cont][0] +
        			" - frequencia_cachorro: " + caracteristicas[cont][1] +
        			" - amplitude_gato: " + caracteristicas[cont][2] +
        			" - frequencia_gato: " + caracteristicas[cont][3] +
            		" - Classe: " + classe + "\n");
        	
        	exportacao += caracteristicas[cont][0] + "," 
                    + caracteristicas[cont][1] + "," 
                    + caracteristicas[cont][2] + "," 
                    + caracteristicas[cont][3] + "," 
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

