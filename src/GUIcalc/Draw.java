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
// DRAW.VERSION 25.03.17
// further description below
////////////////////////////////////////////////////////////////////////////////
package GUIcalc;
import RST.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingWorker;

/// Draw Class - for tree drawing
/// Running in the Background, so it doesn't freeze the GUI
/// Input:	1 nodeList		should containing (root) nodes
///
/// Usage: import GUIcalc; Draw name = new Draw(1); name.execute();
public class Draw  extends SwingWorker<Integer, String>
{

	List<Node> nodeList = new ArrayList<Node>();
	
/// Constructor
	public Draw(List<Node> nodeList){
		this.nodeList 	= nodeList;
	}



    @Override
    protected Integer doInBackground(){
		Usefull.print(nodeList);
		
		return 1;
    }
    
	@Override
    protected void process(List< String> chunks){
    }

}