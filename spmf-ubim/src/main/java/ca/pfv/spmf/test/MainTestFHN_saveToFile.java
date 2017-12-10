package ca.pfv.spmf.test;

import ca.pfv.spmf.algorithms.frequentpatterns.hui_miner.AlgoFHN_Exp02;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

/**
 * Example of how to use the FHN algorithm 
 * from the source code.
 * @author Philippe Fournier-Viger, 2014
 */

//@denenen algoritma
public class MainTestFHN_saveToFile {

	public static void main(String [] arg) throws IOException{
		

		String input = fileToPath("transactions_exp02.txt");
		//String input = fileToPath("NegatedUtilityTransactions.txt");
		String output = ".//output_transactions_exp02.txt";
		//String output = ".//NegatedUtilityTransactionsOutput.txt";

		int min_utility = 80;

		// Applying the FHN algorithm
		AlgoFHN_Exp02 algo = new AlgoFHN_Exp02();
		algo.runAlgorithm(input, output, min_utility);
		algo.printStats();

	}

	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestFHN_saveToFile.class.getClassLoader().getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
