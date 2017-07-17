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
// NODE.VERSION 24.03.17
// further description below
////////////////////////////////////////////////////////////////////////////////

package RST;
import java.util.*;
/**
 * Node.java is a class which generates an object Node to store node specific
 * information.
 * 
 * INPUT: There are 3 different constructors for different Nodes.
 *
 * Usage: import RST; Node name = new Node(); name.prop = "Nucleus";
 *
 * \attention {All global variables are protected. There are no getter and setter functions. 
 * The Node class is meant to be similar to a struct in C++.}
*/
public class Node
{
	// Directly use without further processing
	protected String text				= "";				// Text of the span
	protected String relation			= "";				// Discourse Relation
	protected int[] eduspan				= new int[2];		// EDU span begin, end
	protected int id					= 0;				// number while parsind
	protected Set<Integer> sentenceID	= new LinkedHashSet<Integer>();
	// For the leaf nodes only
	protected int[] nucspan				= new int[2];		// Nucleus span begin, end
	protected int nucedu				= 0;				// Nucleus single EDU
	protected int pnodeID				= 0;				// parent node
		
	// rs3 data for further processing
	protected int xmlID						= 0;		// id in rs3
	protected String filepath				= "";		// id in rs3
	protected boolean fillernode			= false;
		
	// processed data from rs3 data
	protected String prop				= "";				// Property: Nucleus or Satellite
	protected Node lnode				= null;				// Left Node (for binary tree only)
	protected Node rnode				= null;				// Right Node (for binary tree only)
	protected String form				= "";				// Relation Form NN NS SN
	protected Node pnode				= null;				// Parent node
		
/// Constructor for the leaf nodes	
	Node(int id, int xmlID, int pnodeID, String relation, String text, Set<Integer> sentenceID){
		this.id			= id;
		this.pnodeID	= pnodeID;
		this.relation	= relation;
		this.xmlID		= xmlID;
		this.text		= text;
		this.sentenceID = sentenceID;
	}
		
/// Constructor for the non leaf nodes		
	Node(int id, int xmlID, int pnodeID, String relation){
		this.id			= id;
		this.pnodeID	= pnodeID;
		this.relation	= relation;
		this.xmlID		= xmlID;
	}
	
/// Constructor for the filler nodes
	Node(int id, String relation, String prop){
		this.id 		= id;
		this.relation 	= relation;
		this.prop 		= prop;
	}

/// Normal Constructor  
    Node(){    
    }
		
/// Function to set leaf node specific values
	boolean eduset(){
		eduspan[0] 	= id;
		eduspan[1] 	= id;
		nucspan[0] 	= id;
		nucspan[1]	= id;
		nucedu  	= id;
		return true;			
	}

/// Function to check, whether the node is a Nucleus or not
	boolean isnucleus(){
		return relation.matches("conjunction|contrast|disjunction|joint|list|restatement-mn|sequence|span");
	}
		
/// Function to set the property to Nucleus or Satellite
	boolean setprop(){
		if(isnucleus()){
			prop = "Nucleus";
		}
		else{
			prop = "Satellite";
		}
		return true;
	}			
} // End of Node.java
