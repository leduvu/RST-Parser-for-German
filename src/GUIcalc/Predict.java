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
// PREDICT.VERSION 04.04.17
// further description below
////////////////////////////////////////////////////////////////////////////////
package GUIcalc;
import RST.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import javax.swing.*;
import javax.swing.SwingWorker;

/// Predict Class - for realtime Updating of the JTextArea
/// Running in the Background, so it doesn't freeze the GUI
/// Usage: import GUIcalc; Predict name = new Predict(1, 2, 3, 4, 5); name.execute();
public class Predict extends SwingWorker<Integer, String>{
	private List<File> predictFolders 		= new ArrayList<File>();
	private List<File> goldFolders 			= new ArrayList<File>();
	private JTextArea textArea  			= new JTextArea();
	private SVM model						= new SVM();
	private String modelname				= "";
	public List<Node> predtrees 			= new ArrayList<Node>();
	public List<List<Node>> predtreesList	= new ArrayList<List<Node>>();
	public boolean no_eval					= false;	
	
/// Constructor
	public Predict(List<File> predictFolders, List<File> goldFolders, SVM model, JTextArea textArea, String modelname){
		this.predictFolders = predictFolders;
		this.goldFolders 	= goldFolders;
		this.model			= model;
		this.textArea 		= textArea;
		this.modelname		= modelname;
	}
	public Predict(){
	}
	
    @Override
    protected Integer doInBackground(){
		
		for(int ii = 0; ii < predictFolders.size(); ii++){
			// Initialization
			double spanAll 	= 0.0;
			double nucAll 	= 0.0;
			double relAll 	= 0.0;
			double span 	= 0.0;
 			double nuc 		= 0.0;
 			double rel 		= 0.0;
			List<Node> goldtrees 	= new ArrayList<Node>();
		
			File predictFolder 	= predictFolders.get(ii);
			
			publish(" 3 Start Prediction");
			setProgress(1);
			if(modelname.isEmpty()){
				predtrees = model.predict(predictFolder.getPath(), "labelIDMap_.ser", "vocab_.ser");
				predtreesList.add(predtrees);
			}
			// If a model is loaded
			else {
				predtrees = model.predict(predictFolder.getPath(), modelname + ".labelIDMap", modelname + ".vocab");
				predtreesList.add(predtrees);
			}
			// If there is a goldfolder and there are generated pred trees
			if(goldFolders != null && predtrees.size() != 0){
				File goldFolder 	= goldFolders.get(ii);
				publish(" 4 Build goldTrees");
				setProgress(2);
    			goldtrees = Usefull.buildgold(goldFolder.getPath());
    	
    			// Evaluate
    			publish(" 5 Start Evaluation");
    			setProgress(3);
    	
    			Evaluate ev = new Evaluate();
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
				publish("\n" + predictFolder + "\n F1 score on span level is " + String.format("%.4f", spanAll));
				setProgress(4);
				publish(" F1 score on nuc level is " + String.format("%.4f", nucAll));
				setProgress(5);
				publish(" F1 score on relation level is " + String.format("%.4f", relAll));
				setProgress(6);
			}
			publish("- Prediction Finished - \n");
    		setProgress(7);
    	}
		return 1;
    }

    @Override
    protected void process(List< String> chunks){
    	for(String text : chunks){
            textArea.append(text + "\n");
         } 
    }
    
    
} // End of class Predict