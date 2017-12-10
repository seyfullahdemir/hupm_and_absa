package ca.pfv.spmf.algorithms.frequentpatterns.hui_miner;


/* This file is copyright (c) 2008-2015 Philippe Fournier-Viger
* 
* This file is part of the SPMF DATA MINING SOFTWARE
* (http://www.philippe-fournier-viger.com/spmf).
* 
* SPMF is free software: you can redistribute it and/or modify it under the
* terms of the GNU General Public License as published by the Free Software
* Foundation, either version 3 of the License, or (at your option) any later
* version.
* 
* SPMF is distributed in the hope that it will be useful, but WITHOUT ANY
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
* A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License along with
* SPMF. If not, see <http://www.gnu.org/licenses/>.
*/


import ca.pfv.spmf.tools.MemoryLogger;
import tr.edu.metu.ceng.absa.common.datatypes.ABSAConstants;
import tr.edu.metu.ceng.absa.common.dbutil.SqlConnectionHelper;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

/**
 * This is an implementation of the "FHN" algorithm for High-Utility Itemsets 
 * Mining with negative profit values
 * as described in the conference paper : <br/><br/>
 * 
 * Fournier-Viger, P. (2014). FHN: Efficient Mining of High-Utility Itemsets with 
 * Negative Unit Profits. Proc. 10th International Conference on Advanced Data 
 * Mining and Applications (ADMA 2014), Springer LNCS 8933, pp. 16-29.
 * 
 * @see UtilityListFHN
 * @see ElementFHN
 * @author Philippe Fournier-Viger
 */
public class AlgoFHN {

	static SqlConnectionHelper sqlConn = new SqlConnectionHelper("localhost", ABSAConstants.DB_NAME,
			"root", "123456");
	static Map<Integer, String> aspects = new HashMap();
	static {
		ResultSet resultSet = sqlConn.executeSelectQuery("select * from aspect");

		try {
			while (resultSet.next()) {
				int id = resultSet.getInt("ID");
				String aspect = resultSet.getString("name");

				aspects.put(id, aspect);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}


	}
	/** the time at which the algorithm started */
	public long startTimestamp = 0;  
	
	/** the time at which the algorithm ended */
	public long endTimestamp = 0; 
	
	/** the number of high-utility itemsets generated */
	public int huiCount =0; 
	
	/** the number of generated candidates (join operations) */
	public int candidateCount =0;
	
	/** Map to remember the TWU of each item */
	Map<Integer, Integer> mapItemToTWU;
	
	/** writer to write the output file  */
	BufferedWriter writer = null;  
	
	/** The eucs structure:  key: item   key: another item   value: twu */
	Map<Integer, Map<Integer, Long>> mapFMAP;  

	/** enable LA-prune strategy  */
	boolean ENABLE_LA_PRUNE = true;
	
	/** variable for debug mode */
	boolean DEBUG = false;
	
	/** buffer for storing the current itemset that is mined when performing mining
	* the idea is to always reuse the same buffer to reduce memory usage. */
	final int BUFFERS_SIZE = 200;
	private int[] itemsetBuffer = null;


	//===================== FHN ===========================
	Set<Integer> negativeItems = null;
	//====================================================
	
	/**
	 * This class represent an item and its utility in a transaction
	 * @author Philippe Fournier-Viger
	 */
	class Pair{
		int item = 0;
		int utility = 0;
		
		public String toString() {
			return "[" + item + "," + utility + "]";
		}
	}
	
	/**
	 * Default constructor
	 */
	public AlgoFHN() {
		
	}

	/**
	 * Run the algorithm
	 * @param input the input file path
	 * @param output the output file path
	 * @param minUtility the minimum utility threshold
	 * @throws IOException exception if error while writing the file
	 */
	public void runAlgorithm(String input, String output, int minUtility) throws IOException {
		// reset maximum
		MemoryLogger.getInstance().reset();
		
		// initialize the buffer for storing the current itemset
		itemsetBuffer = new int[BUFFERS_SIZE];
		
		// Create the EUCP structure as described in the FHM and FHN papers
		mapFMAP =  new HashMap<Integer, Map<Integer, Long>>();
		
		// record the start time of the algorithm
		startTimestamp = System.currentTimeMillis();
		
		//===================== FHN ===========================
		negativeItems = new HashSet<Integer>();
		//====================================================
		
		writer = new BufferedWriter(new FileWriter(output));

		//  We create a  map to store the TWU of each item
		mapItemToTWU = new HashMap<Integer, Integer>();

		// We scan the database a first time to calculate the TWU of each item.
		BufferedReader myInput = null;
		String thisLine;
		try {
			// prepare the object for reading the file
			myInput = new BufferedReader(new InputStreamReader( new FileInputStream(new File(input))));
			// for each line (transaction) until the end of file
			while ((thisLine = myInput.readLine()) != null) {
				// if the line is  a comment, is  empty or is a
				// kind of metadata
				if (thisLine.isEmpty() == true ||
						thisLine.charAt(0) == '#' || thisLine.charAt(0) == '%'
								|| thisLine.charAt(0) == '@') {
					continue;
				}
				
				// split the transaction according to the : separator
				String split[] = thisLine.split(":"); 
				// the first part is the list of items
				String items[] = split[0].split(" "); 
				//===================== FHN ===========================
				// get the list of utility values corresponding to each item
				// for that transaction
				String utilityValues[] = split[2].split(" ");
				//===============================================
				// the second part is the transaction utility
				int transactionUtility = Integer.parseInt(split[1]);  
				// for each item, we add the transaction utility to its TWU
				for(int i=0; i <items.length; i++){
					// convert item to integer
					Integer item = Integer.parseInt(items[i]);
					
					//===================== FHN ===========================
					Integer itemUtility =Integer.parseInt(utilityValues[i]);
					if(itemUtility < 0) {
						negativeItems.add(item);
					}
					//==================================
					
					// get the current TWU of that item
					Integer twu = mapItemToTWU.get(item);
					// update the twu of that item
					twu = (twu == null)? 
							transactionUtility : twu + transactionUtility;
					mapItemToTWU.put(item, twu);
				}
			}
		} catch (Exception e) {
			// catches exception if error while reading the input file
			e.printStackTrace();
		}finally {
			if(myInput != null){
				myInput.close();
			}
	    }
		
		// CREATE A LIST TO STORE THE UTILITY LIST OF ITEMS WITH TWU  >= MIN_UTILITY.
		List<UtilityListFHN> listOfUtilityLists = new ArrayList<UtilityListFHN>();
		// CREATE A MAP TO STORE THE UTILITY LIST FOR EACH ITEM.
		// Key : item    Value :  utility list associated to that item
		Map<Integer, UtilityListFHN> mapItemToUtilityList = new HashMap<Integer, UtilityListFHN>();

		// For each item
		for(Integer item: mapItemToTWU.keySet()){
			// if the item is promising  (TWU >= minutility)
			if(mapItemToTWU.get(item) >= minUtility){
				// create an empty Utility List that we will fill later.
				UtilityListFHN uList = new UtilityListFHN(item);
				mapItemToUtilityList.put(item, uList);
				// add the item to the list of high TWU items
				listOfUtilityLists.add(uList); 
				
			}
		}
		// SORT THE LIST OF HIGH TWU ITEMS IN ASCENDING ORDER
		Collections.sort(listOfUtilityLists, new Comparator<UtilityListFHN>(){
			public int compare(UtilityListFHN o1, UtilityListFHN o2) {
				// compare the TWU of the items
				return compareItems(o1.item, o2.item);
			}
			} );
		
		// SECOND DATABASE PASS TO CONSTRUCT THE UTILITY LISTS 
		// OF 1-ITEMSETS  HAVING TWU  >= minutil (promising items)
		try {
			// prepare object for reading the file
			myInput = new BufferedReader(new InputStreamReader(new FileInputStream(new File(input))));
			// variable to count the number of transaction
			int tid =0;
			// for each line (transaction) until the end of file
			while ((thisLine = myInput.readLine()) != null) {
				// if the line is  a comment, is  empty or is a
				// kind of metadata
				if (thisLine.isEmpty() == true ||
						thisLine.charAt(0) == '#' || thisLine.charAt(0) == '%'
								|| thisLine.charAt(0) == '@') {
					continue;
				}
				
				// split the line according to the separator
				String split[] = thisLine.split(":");
				// get the list of items
				String items[] = split[0].split(" ");
				// get the list of utility values corresponding to each item
				// for that transaction
				String utilityValues[] = split[2].split(" ");
				
				// Copy the transaction into lists but 
				// without items with TWU < minutility
				
				int remainingUtility =0;
				

				long newTWU = 0;  // NEW OPTIMIZATION 
				
				// Create a list to store items
				List<Pair> revisedTransaction = new ArrayList<Pair>();
				// for each item
				for(int i=0; i <items.length; i++){
					/// convert values to integers
					Pair pair = new Pair();
					pair.item = Integer.parseInt(items[i]);
					pair.utility = Integer.parseInt(utilityValues[i]);
					// if the item has enough utility
					if(mapItemToTWU.get(pair.item) >= minUtility){
						// add it
						revisedTransaction.add(pair);
						// ======= FHN (MODIF) ===========================
						if(!negativeItems.contains(pair.item)) {
							remainingUtility += pair.utility;
							newTWU += pair.utility; // NEW OPTIMIZATION
						}
						//================================================
					}
				}
				
				// sort the transaction
				Collections.sort(revisedTransaction, new Comparator<Pair>(){
					public int compare(Pair o1, Pair o2) {
						return compareItems(o1.item, o2.item);
					}});

								
				// for each item left in the transaction
				for(int i = 0; i< revisedTransaction.size(); i++){
					Pair pair =  revisedTransaction.get(i);
					
					// subtract the utility of this item from the remaining utility
					// ======= FHN (MODIF) ===========================
					// if not a negative item
					if(remainingUtility != 0) {
					//=======================================
						remainingUtility = remainingUtility - pair.utility;
					}
					
					// get the utility list of this item
					UtilityListFHN utilityListOfItem = mapItemToUtilityList.get(pair.item);
					
					// Add a new Element to the utility list of this item corresponding to this transaction
					if(pair.utility > 0) {
						ElementFHN element = new ElementFHN(tid, pair.utility, 0, remainingUtility);
						utilityListOfItem.addElement(element);
					}else {
						ElementFHN element = new ElementFHN(tid, 0, pair.utility, remainingUtility);
						utilityListOfItem.addElement(element);
					}
					
										
					// BEGIN NEW OPTIMIZATION for FHM
					// ======= FHN (MODIF) ===========================
					// if not a negative item
					if(remainingUtility != 0) {
					// =============================================
						Map<Integer, Long> mapFMAPItem = mapFMAP.get(pair.item);
						if(mapFMAPItem == null) {
							mapFMAPItem = new HashMap<Integer, Long>();
							mapFMAP.put(pair.item, mapFMAPItem);
						}
	
						for(int j = i+1; j< revisedTransaction.size(); j++){
							Pair pairAfter = revisedTransaction.get(j);
							Long twuSum = mapFMAPItem.get(pairAfter.item);
							if(twuSum == null) {
								mapFMAPItem.put(pairAfter.item, newTWU);
							}else {
								mapFMAPItem.put(pairAfter.item, twuSum + newTWU);
							}
						}
					}
					// END OPTIMIZATION of FHM
				}
				tid++; // increase tid number for next transaction

			}
		} catch (Exception e) {
			// to catch error while reading the input file
			e.printStackTrace();
		}finally {
			if(myInput != null){
				myInput.close();
			}
	    }
		
		// check the memory usage
		MemoryLogger.getInstance().checkMemory();

		// Mine the database recursively
		fhn(itemsetBuffer, 0, null, listOfUtilityLists, minUtility);
		
		// check the memory usage again and close the file.
		MemoryLogger.getInstance().checkMemory();
		// close output file
		writer.close();
		// record end time
		endTimestamp = System.currentTimeMillis();
	}
	
	/**
	 * Method to compare items by their TWU
	 * @param item1 an item
	 * @param item2 another item
	 * @return 0 if the same item, >0 if item1 is larger than item2,  <0 otherwise
	 */
	private int compareItems(int item1, int item2) {
		//====================== FHN =======================
		Boolean item1IsNegative = negativeItems.contains(item1);
		Boolean item2IsNegative = negativeItems.contains(item2);
		if(!item1IsNegative && item2IsNegative) {
			return -1;
		}else if (item1IsNegative && !item2IsNegative)  {
			return 1;
		}
		//=============================================
		
		int compare = mapItemToTWU.get(item1) - mapItemToTWU.get(item2);
		// if the same, use the lexical order otherwise use the TWU
		return (compare == 0)? item1 - item2 :  compare;
	}
	
	/**
	 * This is the recursive method to find all high utility itemsets. It writes
	 * the itemsets to the output file.
	 * @param prefix  This is the current prefix. Initially, it is empty.
	 * @param pUL This is the Utility List of the prefix. Initially, it is empty.
	 * @param ULs The utility lists corresponding to each extension of the prefix.
	 * @param minUtility The minUtility threshold.
	 * @param prefixLength The current prefix length
	 * @throws IOException
	 */
	private void fhn(int [] prefix, 
			int prefixLength, UtilityListFHN pUL, List<UtilityListFHN> ULs, int minUtility)
			throws IOException {
		
		// For each extension X of prefix P
		for(int i=0; i< ULs.size(); i++){
			UtilityListFHN X = ULs.get(i);
			
			// If pX is a high utility itemset.
			// we save the itemset:  pX 
			if(X.sumIutils + X.sumINutils >= minUtility){
				// save to file
				writeOut(prefix, prefixLength, X.item, X.sumIutils + X.sumINutils);
			}
			
			// If the sum of the remaining utilities for pX
			// is higher than minUtility, we explore extensions of pX.
			// (this is the pruning condition)
			if(X.sumIutils + X.sumRutils >= minUtility){
				// This list will contain the utility lists of pX extensions.
				List<UtilityListFHN> exULs = new ArrayList<UtilityListFHN>();
				// For each extension of p appearing
				// after X according to the ascending order
				for(int j=i+1; j < ULs.size(); j++){
					UtilityListFHN Y = ULs.get(j);
					
					// ======================== NEW OPTIMIZATION USED IN FHM
					Map<Integer, Long> mapTWUF = mapFMAP.get(X.item);
					if(mapTWUF != null) {
						Long twuF = mapTWUF.get(Y.item);
						if(twuF == null || twuF < minUtility) {
							continue;
						}
					}
					candidateCount++;
					// =========================== END OF NEW OPTIMIZATION
					
					// we construct the extension pXY 
					// and add it to the list of extensions of pX
					UtilityListFHN temp = construct(pUL, X, Y, minUtility);
					
					if(temp != null) {
						exULs.add(temp);
					}
				}
				// We create new prefix pX
				itemsetBuffer[prefixLength] = X.item;
				
				// We make a recursive call to discover all itemsets with the prefix pXY
				fhn(itemsetBuffer, prefixLength+1, X, exULs, minUtility); 
			}
		}
		MemoryLogger.getInstance().checkMemory();
	}
	
	/**
	 * This method constructs the utility list of pXY
	 * @param P :  the utility list of prefix P.
	 * @param px : the utility list of pX
	 * @param py : the utility list of pY
	 * @param minUtility : the minimum utility threshold
	 * @return the utility list of pXY
	 */
	private UtilityListFHN construct(UtilityListFHN P, UtilityListFHN px, UtilityListFHN py, int minUtility) {
		// create an empy utility list for pXY
		UtilityListFHN pxyUL = new UtilityListFHN(py.item);
		
		//== new optimization - LA-prune  == /
		// Initialize the sum of total utility
		long totalUtility = px.sumIutils + px.sumRutils;
		// ================================================
		
		// for each element in the utility list of pX
		for(ElementFHN ex : px.elements){
			// do a binary search to find element ey in py with tid = ex.tid
			ElementFHN ey = findElementWithTID(py, ex.tid);
			if(ey == null){
				//== new optimization - LA-prune == /
				if(ENABLE_LA_PRUNE) {
					totalUtility -= (ex.iutils+ex.rutils);
					if(totalUtility < minUtility) {
						return null;
					}
				}
				// =============================================== /
				continue;
			}
			// if the prefix p is null
			if(P == null){
				// Create the new element
				ElementFHN eXY = new ElementFHN(ex.tid, ex.iutils + ey.iutils, ex.inutils + ey.inutils, ey.rutils);
				// add the new element to the utility list of pXY
				pxyUL.addElement(eXY);
				
			}else{
				// find the element in the utility list of p wih the same tid
				ElementFHN e = findElementWithTID(P, ex.tid);
				if(e != null){
					// Create new element
					ElementFHN eXY = new ElementFHN(ex.tid, ex.iutils + ey.iutils - e.iutils,
							ex.inutils + ey.inutils - e.inutils,
								ey.rutils);
					// add the new element to the utility list of pXY
					pxyUL.addElement(eXY);
				}
			}	
		}
		// return the utility list of pXY.
		return pxyUL;
	}
	
	/**
	 * Do a binary search to find the element with a given tid in a utility list
	 * @param ulist the utility list
	 * @param tid  the tid
	 * @return  the element or null if none has the tid.
	 */
	private ElementFHN findElementWithTID(UtilityListFHN ulist, int tid){
		List<ElementFHN> list = ulist.elements;
		
		// perform a binary search to check if  the subset appears in  level k-1.
        int first = 0;
        int last = list.size() - 1;
       
        // the binary search
        while( first <= last )
        {
        	int middle = ( first + last ) >>> 1; // divide by 2

            if(list.get(middle).tid < tid){
            	first = middle + 1;  //  the itemset compared is larger than the subset according to the lexical order
            }
            else if(list.get(middle).tid > tid){
            	last = middle - 1; //  the itemset compared is smaller than the subset  is smaller according to the lexical order
            }
            else{
            	return list.get(middle);
            }
        }
		return null;
	}

	/**
	 * Method to write a high utility itemset to the output file.
	 * @param the prefix to be writent o the output file
	 * @param an item to be appended to the prefix
	 * @param utility the utility of the prefix concatenated with the item
	 * @param prefixLength the prefix length
	 */
	private void writeOut(int[] prefix, int prefixLength, int item, long utility) throws IOException {
		huiCount++; // increase the number of high utility itemsets found
		
		//Create a string buffer
		StringBuilder buffer = new StringBuilder();
		// append the prefix
		for (int i = 0; i < prefixLength; i++) {
			buffer.append(aspects.get(prefix[i]));
			buffer.append(", ");
		}
		// append the last item
		buffer.append(aspects.get(item));
		// append the utility value
		buffer.append(": ");
		//buffer.append(" #UTIL: ");
		buffer.append(utility);
		// write to file
		writer.write(buffer.toString());
		writer.newLine();
	}
	
	/**
	 * Print statistics about the latest execution to System.out.
	 * @throws IOException 
	 */
	public void printStats() throws IOException {
		System.out.println("=============  FHN ALGORITHM v0.96r18 - STATS =============");
		System.out.println(" Total time ~ "                  + (endTimestamp - startTimestamp) + " ms");
		System.out.println(" Memory ~ "                      + MemoryLogger.getInstance().getMaxMemory() + " MB");
		System.out.println(" High-utility itemsets count : " + huiCount); 
		System.out.println(" Candidate count : "             + candidateCount);
		
		if(DEBUG) {
			int pairCount = 0;
			double maxMemory = getObjectSize(mapFMAP);
			for(Entry<Integer, Map<Integer, Long>> entry : mapFMAP.entrySet()) {
				maxMemory += getObjectSize(entry.getKey());
				for(Entry<Integer, Long> entry2 :entry.getValue().entrySet()) {
					pairCount++;
					maxMemory += getObjectSize(entry2.getKey()) + getObjectSize(entry2.getValue());
				}
			}
			System.out.println("CMAP size " + maxMemory + " MB");
			System.out.println("PAIR COUNT " + pairCount);
		}
		System.out.println("===================================================");
	}
	
	/**
	 * This method is used to calculate the size of a Java object
	 * @param object an object
	 * @return the size in Megabytes (MB)
	 * @throws IOException
	 */
    private double getObjectSize(
            Object object)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(object);
        oos.close();
        double maxMemory = baos.size() / 1024d / 1024d;
        return maxMemory;
    }
}