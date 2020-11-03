package View;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import javax.sound.sampled.UnsupportedAudioFileException;
import ExtratorDeCaracteristicas.ExtraiCaracteristicasSom;
import algoritmos.PerceptronMultiCamadas;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import util.Player;

public class PrincipalController {
	
	@FXML Label classe;
	@FXML Label carCachorro;
	@FXML Label carGato;
	@FXML Label info;
	
	File f;
	private double [] c = {0,0};
	private DecimalFormat df = new DecimalFormat("##0.0000");
	
	//extrair arff
	@FXML
	public void extrairCaracteristicas() {
		ExtraiCaracteristicasSom.extrair();;
	}
	
	@FXML 
	public void perceptronMultiCamdas() {
		
		double [] pmn = PerceptronMultiCamadas.perceptronMultilayerNetwork(c);
		classe.setText("Probabilidade de ser Cachorro: "+ df.format(pmn[0]*100) + "%" +
				"  -  Probabilidade de ser gato: "+ df.format(pmn[1]*100) + "%");
	}
	
	@FXML
	public void selecionaSom() {
		
		f = buscaSom();
		if(f != null) {
			double[] caracteristicas = ExtraiCaracteristicasSom.extractAmplitudeFromFile(f);
			carCachorro.setText(String.valueOf(df.format(caracteristicas[0])) + " (amplitude média)");
			carGato.setText(String.valueOf(df.format(caracteristicas[1])) + " (amplitude média)");
			info.setText(ExtraiCaracteristicasSom.inf);
			c = caracteristicas;
		}
	}
	
	@FXML
	public void play() throws UnsupportedAudioFileException, IOException {
		Player pl = new Player(f);  
        pl.start();  
	}
	
	private File buscaSom() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new 
				   FileChooser.ExtensionFilter(
						   "Sons", "*.wav", "*.WAV")); 	
		 fileChooser.setInitialDirectory(new File("src/sonsTest"));
		 File somSelec = fileChooser.showOpenDialog(null);
		 try {
			 if (somSelec != null) {
			    return somSelec;
			 }
		 } catch (Exception e) {
			e.printStackTrace();
		 }
		 return null;
	}
	
}
