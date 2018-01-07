package ca.pfv.spmf.test;

import ca.pfv.spmf.algorithms.frequentpatterns.apriori.AlgoApriori;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

/**
 * Example of how to use APRIORI algorithm from the source code.
 * @author Philippe Fournier-Viger (Copyright 2008)
 */
public class MainTestApriori_saveToFile {

	public static void main(String [] arg) throws IOException{

		String input = fileToPath("ForApprioriUsingFinalAspects_transactions_exp03.txt");
		String output = ".//outputForApprioriUsingFinalAspects_transactions_exp03_v2_notClose.txt";  // the path for saving the frequent itemsets found
		
		double minsup = 0.001; // means a minsup of 2 transaction (we used a relative support)
		
		// Applying the Apriori algorithm
		AlgoApriori apriori = new AlgoApriori();
		apriori.runAlgorithm(minsup, input, output);
		apriori.printStats();
	}
	
	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestFHN_saveToFile_Exp03.class.getClassLoader().getResource(filename);
		return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
