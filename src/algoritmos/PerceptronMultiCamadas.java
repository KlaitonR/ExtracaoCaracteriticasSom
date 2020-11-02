package algoritmos;

import java.io.FileReader;

import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class PerceptronMultiCamadas {
	
	public static double[] perceptronMultilayerNetwork(double[]caracteristicas) {
		double[] retorno = {0,0};
		
		try{
			
			//Reading training arff or csv file
			FileReader trainreader = new FileReader("caracteristicas.arff");
			Instances train = new Instances(trainreader);
			train.setClassIndex(train.numAttributes()-1);
			//Instance of NN
			MultilayerPerceptron mlp = new MultilayerPerceptron();
			//Setting Parameters
			mlp.setLearningRate(0.1);
			mlp.setMomentum(0.2);
			mlp.setTrainingTime(2000);
			mlp.setHiddenLayers("3");
			mlp.buildClassifier(train);
			
			weka.classifiers.functions.MultilayerPerceptron network = new weka.classifiers.functions.MultilayerPerceptron();
			network.buildClassifier(train);
			
			Instance novo = new DenseInstance(train.numAttributes());
			novo.setDataset(train);
			novo.setValue(0, caracteristicas[0]);
			novo.setValue(1, caracteristicas[1]);
			novo.setValue(2, caracteristicas[2]);
			novo.setValue(3, caracteristicas[3]);
			novo.setValue(4, caracteristicas[4]);
			novo.setValue(5, caracteristicas[5]);
			
			retorno = network.distributionForInstance(novo);
			
			}
			catch(Exception e){
				e.printStackTrace();
			}
		
		return retorno;
	}

}
