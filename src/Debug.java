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
//
// DEBUG.VERSION 26.03.17
// further description below
////////////////////////////////////////////////////////////////////////////////

import java.io.*;
import java.util.*;
import java.nio.file.Files;
import RST.*;
/**
 * Debug.java is a program which uses the files in the debug folder for training,
 * testing and evaluation.
 *
 * Usage: 	- uncomment the print functions in Evaluate.java  report()
 *			- compile with: javac Debug.java 
 *			- execute with: java Debug
*/
public class Debug
{
	public static void main(String[] args) throws IOException
	{
		List<Node> predtrees = new ArrayList<Node>();
		List<Node> goldtrees = new ArrayList<Node>();
		
			// Delete all files
			try{
    			File fileM 	= new File("matrix.txt");
    			File fileL 	= new File("labelIDMap.ser");
    			File fileV 	= new File("vocab.ser");
    			Files.deleteIfExists(fileM.toPath());
    			Files.deleteIfExists(fileL.toPath());
    			Files.deleteIfExists(fileV.toPath());	
    				
    		} catch(IOException ex){
      			System.err.println(ex);	
   			}

		String rstPath = "../data/rst/1/";		// Data Path
		String matrixPath = "matrix.txt";		// Path, where matrix will be saved later
					
		System.out.println(">> 1 BUILD DATA: " + rstPath + " <<");
		
		// Parse the data and build the trees
		Data trainData = new Data();
		trainData.builddata(rstPath, matrixPath);

		// Save the labelIDMap and vocab aka Feature Map
		trainData.serialize("labelIDMap", "labelIDMap.ser");
        System.out.print("\t Save labelIDMap to file");
		trainData.serialize("vocab", "vocab.ser");
        System.out.println("\t Save vocab to file");
        System.out.println("\t finished");
                	
        // Start training
        System.out.println(">> 2 TRAIN <<");
		SVM model = new SVM();
		model.train(matrixPath);
		System.out.println("\t finished");
			
		// Start prediction
		String testPath = "../data/edu/1/";
		System.out.println(">> 3 PREDICT: " + testPath + " <<");
		predtrees = model.predict(testPath, "labelIDMap.ser", "vocab.ser");
		
		// Build gold trees
		System.out.println("\t Build gold trees");
		goldtrees = Usefull.buildgold("../data/rst/1/");

		// Compare
 		Evaluate ev = new Evaluate();
		// Initialization
 		double span 	= 0.0;
 		double nuc 		= 0.0;
 		double rel 		= 0.0;
					// Initialization
		double spanAll 	= 0.0;
		double nucAll 	= 0.0;
		double relAll 	= 0.0;
			
 		// Add the values from the trees together
		for(int jj = 0; jj < predtrees.size(); jj++){
			double[] values = ev.eval(goldtrees.get(jj), predtrees.get(jj));
			span 	= span 	+ values[0];
			nuc 	= nuc 	+ values[1];
			rel 	= rel 	+ values[2];
		}
		// Calculate the mean of the values
		spanAll = span/predtrees.size();
		nucAll  = nuc/predtrees.size();
		relAll  = rel/predtrees.size();
			
		System.out.println(String.format("%.4f",spanAll));
		System.out.println(String.format("%.4f",nucAll));
		System.out.println(String.format("%.4f",relAll));
		System.out.println("\t finished");
	}
	
} // End of class Test