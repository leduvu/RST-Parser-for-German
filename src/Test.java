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
// TEST.VERSION 24.03.17
// further description below
////////////////////////////////////////////////////////////////////////////////

import java.io.*;
import java.util.*;
import java.nio.file.Files;
import RST.*;
/**
 * Test.java is a program which performs training, testing and evaluation using 
 * The Potsdam Commentary Corpus (PCC).
 * The evaluation Method is a 5-fold crossvalidation.
 *
 * Everything is hard coded, so the directories have to stay the same
 *
 * If you want to generate your own .edus files from your .rs3 files use:
 * Usefull.generate_rawdata(FOLDER PATH);
 * The files will be saved in the ./data/ folder
 *
 * Usage: 	- compile with: javac Test.java 
 *			- execute with: java Test
*/
public class Test
{

	public static void main(String[] args) throws IOException
	{
		// Initialization
		double spanAll 	= 0.0;
		double nucAll 	= 0.0;
		double relAll 	= 0.0;
		
			// Delete all files
			try{
				for(int kk = 1; kk <=5; kk++){
    				File fileM 	= new File("matrix" + kk + ".txt");
    				File fileL 	= new File("labelIDMap" + kk + ".ser");
    				File fileV 	= new File("vocab" + kk +".ser");
    				Files.deleteIfExists(fileM.toPath());
    				Files.deleteIfExists(fileL.toPath());
    				Files.deleteIfExists(fileV.toPath());	
    			}
    				
    		} catch(IOException ex){
      			System.err.println(ex);	
   			}
		
		for(int ii = 1; ii <=5; ii++){
			// Initialization
			List<Node> predtrees 	= new ArrayList<Node>();
			List<Node> goldtrees 	= new ArrayList<Node>();
			String rstPath 			= "";
			String matrixPath		= "";
			Data trainData 			= null;
			
			System.out.println(">> 1 BUILD DATA <<");
			// Test always the files in the folders, which are not the train folders
			for(int jj = 1; jj <=5; jj++){
				if(jj != ii){
					rstPath 	= "../data/rst/" + jj;
					matrixPath 	= "matrix" + ii + ".txt"; 
					
					trainData 	= new Data();
					trainData.builddata(rstPath, matrixPath);
					System.out.println("\t" + rstPath);
				}
			}
			// Save the labelIDMap and vocab aka Feature Map
			if(trainData.serialize("labelIDMap", "labelIDMap" + ii + ".ser")){
            	System.out.print("\t Save labelIDMap to file");
            }
            if(trainData.serialize("vocab", "vocab" + ii + ".ser")){
            	System.out.println("\t Save vocab to file");
            }
        	System.out.println("\t finished");
        	
        	// Start training
        	System.out.println(">> 2 TRAIN <<");
			SVM model = new SVM();
			model.train(matrixPath);
			System.out.println("\t finished");
			
			// Start prediction
			System.out.println(">> 3 PREDICT <<");
			// Every Folder will be the test folder once
			String testPath = "../data/edu/" + ii;
			System.out.println("\t"  + testPath);
			predtrees = model.predict(testPath, "labelIDMap" + ii + ".ser", "vocab" + ii + ".ser");
			
			// Build gold trees
			System.out.println("\t Build gold trees");
			goldtrees = Usefull.buildgold("../data/rst/" + ii);
 
 			// Initialization
 			double span 	= 0.0;
 			double nuc 		= 0.0;
 			double rel 		= 0.0;
 			Evaluate ev 	= new Evaluate();
 			
 			// Add the values from the trees together
			for(int jj = 0; jj < predtrees.size(); jj++){
				double[] values = ev.eval(goldtrees.get(jj), predtrees.get(jj));
				span 	= span 	+ values[0];
				nuc 	= nuc 	+ values[1];
				rel 	= rel 	+ values[2];
			}
			// Calculate the mean of the values and add them together
			spanAll = spanAll + (span/predtrees.size());
			nucAll = nucAll + (nuc/predtrees.size());
			relAll = relAll + (rel/predtrees.size());
			System.out.println("\t finished");
		}
		
		// Printing the mean of each value
		System.out.println("F1 score on span level is " + String.format("%.4f", spanAll/5));
        System.out.println("F1 score on nuc level is " 	+ String.format("%.4f", nucAll/5));
        System.out.println("F1 score on rel level is " 	+ String.format("%.4f", relAll/5));
	}
	
} // End of class Test