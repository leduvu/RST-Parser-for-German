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
// TRAIN.VERSION 22.03.17
// further description below
////////////////////////////////////////////////////////////////////////////////
package GUIcalc;
import RST.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import javax.swing.*;
import javax.swing.SwingWorker;

/// Training Class - for realtime Updating of the JTextArea
/// Running in the Background, so it doesn't freeze the GUI
/// Usage: import GUIcalc; Train name = new Train(1, 2); name.execute(); name.getmodel();
public class Train extends SwingWorker<Integer, String>{
	private List<File> trainFolders = new ArrayList<File>();
	private String matrixPath 	= "matrix_.txt";
	private JTextArea textArea  = new JTextArea();
	public SVM model			= new SVM();
	
	public Train(List<File> trainFolders, JTextArea textArea){
		this.trainFolders = trainFolders;
		this.textArea 	= textArea;
	}
	
	public Train(){
	}
	
    @Override
    protected Integer doInBackground(){
    	for(File rstFolder: trainFolders){
  			String rstPath = rstFolder.getPath(); 
					   				
    		// Parse the data and build the trees
    		publish(" 1 Build Data " + rstPath);
    		setProgress(1);
			Data trainData = new Data();
			trainData.builddata(rstPath, matrixPath);
			publish(" 2 Start Training");		
			setProgress(2);
			// Save the labelIDMap and vocab aka Feature Map
			trainData.serialize("labelIDMap", "labelIDMap_.ser");
			trainData.serialize("vocab", "vocab_.ser");
			model.train(matrixPath);
			publish("- Training Finished - \n");
			setProgress(3);
		}
		return 1;
    }

    @Override
    protected void process(List< String> chunks){
    	for(String text : chunks){
            textArea.append(text + "\n");
         } 
    }
} // End of class Train