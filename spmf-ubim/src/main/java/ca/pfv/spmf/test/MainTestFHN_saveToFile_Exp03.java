package ca.pfv.spmf.test;

import ca.pfv.spmf.algorithms.frequentpatterns.hui_miner.AlgoFHN_Exp03;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

/**
 * Example of how to use the FHN algorithm 
 * from the source code.
 * @author Philippe Fournier-Viger, 2014
 */

//@denenen algoritma
public class MainTestFHN_saveToFile_Exp03 {

	public static void main(String [] arg) throws IOException{
		

		String input = fileToPath("negated_transactions_exp03.txt");
		//String input = fileToPath("NegatedUtilityTransactions.txt");
		String output = ".//output_negated_transactions_exp03.txt";
		//String output = ".//NegatedUtilityTransactionsOutput.txt";

		int min_utility = 1;

		// Applying the FHN algorithm
		AlgoFHN_Exp03 algo = new AlgoFHN_Exp03();
		algo.runAlgorithm(input, output, min_utility);
		algo.printStats();

	}

	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestFHN_saveToFile_Exp03.class.getClassLoader().getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
