/*
Copyright 2017 Le Duyen Sandra Vu

This file is part of ForestForGe.
ForestForGe is free software: you can redistribute it and/or modify it under the terms of 
the GNU General Public License as published by the Free Software Foundation, either 
version 3 of the License, or (at your option) any later version.
ForestForGe is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with ForestForGe.  
If not, see <http://www.gnu.org/licenses/>.
*/

////////////////////////////////////////////////////////////////////////////////
// Author:		Le Duyen Sandra Vu 
// Matr.Nr.: 	768693 - University of Potsdam B.Sc. Student
// Compiler: 	javac 1.8.0_66
// OS:			MacOS 10.12.3
// Subject:		Computational Lingustics Bachelor Thesis
//				Automatic parsing of rhetorical structures in German text
//
// The code is based on Ji, Eisensteins paper and code!
// Differences and similarities are discussed in the README.
//
// Code with model:		https://github.com/jiyfeng/DPLP
// Code without model:	https://github.com/jiyfeng/RSTParser
// Paper:				http://www.cc.gatech.edu/~jeisenst/papers/ji-acl-2014.pdf
//
//
// DATA.VERSION 21.03.17
// further description below
////////////////////////////////////////////////////////////////////////////////

package RST;
import java.util.*;
import java.io.*;
/**
 * Data.java is a class which generates an object Data, which can use functions to build the data(trees)
 * and also to build and save the vocab (aka feature map) and labelID map.
 *
 * Usage: import RST; Data name = new Data(); name.function();
*/
public class Data
{	
	// Initialization
	private LinkedHashMap<List<String>, Integer> vocab	= new LinkedHashMap<List<String>,Integer>();	// Map: features - ID
	private LinkedHashMap<Double, String> labelIDMap 	= new LinkedHashMap<Double, String>();			// Map: ID 	- label(action)
	private LinkedHashMap<String, Integer> labelMap 	= new LinkedHashMap<String,Integer>();			// Map: label - ID
	private List<List<List<String>>> sampleList 		= new ArrayList<List<List<String>>>();			// List: for every action one sample
	private List<List<String>> actionList				= new ArrayList<List<String>>();				// List: for every node one action
	private int vocabID = 0;	// Counter
	private int labelID = 0;	// Counter

/** generates for every .rs3 file in the folder rstPath a tree, adds its Features to vocab
 * and its generated Shift Reduce actions to labelIDMap
 * 
 * \param rstPath directory of a folder, with .rs3 files
 * \param savePath directory, where the matrix should be stored later
*/
	public boolean builddata(String rstPath, String savePath){
  		File dir 			= new File(rstPath);
  		boolean no_rs3file 	= true;
  		
  		// for every .rs3 file: build RSTTree
  		for(File file : dir.listFiles()) {
    		if(file.getName().endsWith(".rs3")) {
    			no_rs3file 		= false;
  				Tree rst 		= new Tree();
  				try {
					rst.build(file.getPath());			// Build the trees
					this.actionList = rst.actionList;	// Get the actionList
					this.sampleList = rst.sampleList;	// Get the sampleList
					buildvocab();						// Add Features to vocab
					buildlabelIDMap();					// Order ID as key and label as value
					savematrix(savePath);				// Safe to file
				} catch (Exception e) {
            		System.err.println(e);
        		}
 			}	
  		}
  		if(no_rs3file){
  			System.err.println("No .rs3 file in the folder: " + rstPath);
  		}
  		return true;
	}

/** serializes vocab or labelIDMap and saves the result to a file
 *
 * \param object is vocab or labelIDMap
 * \param path directory, where to serialized data should be saved
*/
	public boolean serialize(String object, String path){
		if(vocab.isEmpty() || labelIDMap.isEmpty()){
			System.err.println("ERROR data.java: builddata() was no success");
			return false;
		}
		
		if(object.equals("vocab")){
			Usefull.serialize(vocab, path);
		}
		else if(object.equals("labelIDMap")){
			Usefull.serialize(labelIDMap, path);
		}
		else{
			System.err.println("ERROR data.java: Serialized Object unknown");
		}
        return true;
	}
	
///	Every Feature [x, y, z] gets an ID and will be saved in vocab
/// Every Shift Reduce action will be converted to a label, which will be saved with an ID to labelMap
	private boolean buildvocab(){
		for(int ii = 0; ii < actionList.size(); ii++){
			List<String> action 		= actionList.get(ii);
			List<List<String>> sample 	= sampleList.get(ii);
			
			// Convert commands to labels
			String label = Usefull.action2label(action);
			
			for(List<String> feature : sample){
				
				// Generate vocab alias featureMap
				if(vocab.get(feature) == null){
					vocab.put(feature, vocabID);
					vocabID++;
				}
				
				// Generate labelMap 
				if(labelMap.get(label) == null){
					labelMap.put(label, labelID);
					labelID++;
				}
			}
		}
		return true;
	}

/// Generates the labelIDMap
	private boolean buildlabelIDMap(){		
		// Switch label with labelID / Generate labelIDMap
		for(String l : labelMap.keySet()){
			labelIDMap.put((double) labelMap.get(l), l);
		}
		return true;
	}
	
/** gets all information in the right order for SVM learning with libsvm
 * label 		= Shift Reduce action
 * featureID 	= ID of a feature
 * feature 		= [x, y, z]		
 * Every node has a List of features
 * \sa Feature.java
 *
 * label featureID:1 featureID:1		ID in ascending Order
 * 1 means "existing"
 *
 * \param savePath directory, where the matrix should be stored later
*/
	private boolean savematrix(String savePath){	
		for(int ii = 0; ii < actionList.size(); ii++){
			List<String> action 		= actionList.get(ii);
			List<List<String>> sample 	= sampleList.get(ii);
			
			// Convert commands to labels
			String label = Usefull.action2label(action);
			
			// Save FeatureIDs into a map
			List<Integer> featureIDList=new ArrayList<Integer>();
			for(List<String> features : sample){
				featureIDList.add(vocab.get(features));
			}
        
        	// Sort them - smallest first
			Collections.sort(featureIDList);
			
			Locale.setDefault(Locale.ENGLISH); // Set language to english for dot instead of comma
			try(
    			BufferedWriter bw = new BufferedWriter(new FileWriter(savePath, true));
    			PrintWriter out = new PrintWriter(bw);)
			{
				// Save matrix to file
            	out.print(labelMap.get(label) + " ");
				for(int featureID : featureIDList){
					out.print(featureID + ":"+ "1 ");
				}
				out.println();
			} catch (IOException e) {
   				e.printStackTrace();
			}
		}
		return true;
	}

} // End of class Data