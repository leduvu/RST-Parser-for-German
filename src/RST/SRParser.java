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
// SRPARSER.VERSION 25.03.17
// further description below
////////////////////////////////////////////////////////////////////////////////
package RST;
import java.util.*;
/**
 * SRParser.java is a class which generates an object SRParser, which can use functions
 * related to shift reduce parsing
 *
 * Input: 	1 queue		with EDUs/node of a file
 *			2 stack		empty
 *			3 edusize	number of EDUs in the file
 *
 * Usage: import RST; SRParser name = new SRParser(1, 2, 3); name.function();
*/
class SRParser
{
	protected List<Node> stack 				= new ArrayList<Node>();
	protected List<Node> queue				= new ArrayList<Node>();
	private int id							= 0;
	
/// Constructor
	SRParser(List<Node> stack, List<Node> queue, int edusize){
		this.stack = stack;
		this.queue = queue;
		this.id	   = edusize;		// Start from the last EDU ID
	}
	
/** Shift Reduce Algorithm
 *
 * \param actionList List of Shift Reduce actions calculated for all EDUs/nodes
*/
	protected boolean operate(List<String> actionList){
		String action 	= actionList.get(0);
		String form 	= actionList.get(1);
		String relation = actionList.get(2);
		if(action.equals("Shift")){
			if(queue.size() == 0){
				System.err.println("Shift action with an empty queue!");
				return false;
			}
			Node node = queue.remove(0);	// Remove first element
			stack.add(node);				// add to stack
		}
		if(action.equals("Reduce")){
			if(stack.size() < 2){
				System.err.println("Reduce action with stack which has less than 2 spans");
				return false;
			}
			Node rnode = stack.remove(stack.size()-1);	// Remove last and
			Node lnode = stack.remove(stack.size()-1);	// second last element
		
			Node node  		 = new Node();
			node.filepath	 = lnode.filepath;
			node.id			 = id;
			node.lnode 		 = lnode;
			node.rnode 		 = rnode;
			node.lnode.pnode = node;
			node.rnode.pnode = node;
			node.text		 = node.lnode.text + node.rnode.text;
			node.eduspan[0]	 = lnode.eduspan[0];
			node.eduspan[1]  = rnode.eduspan[1];
			node.form 		 = form;
			// adds all sentenceIDs of the rnode and lnode to the parentnode
			for(int jj : lnode.sentenceID){
				node.sentenceID.add(jj);
			}
			for(int jj : rnode.sentenceID){
				node.sentenceID.add(jj);
			}
		
			if(form.equals("NN")){
				node.filepath	= lnode.filepath;
			    node.nucspan[0] = lnode.eduspan[0];
			    node.nucspan[1] = rnode.eduspan[1];
                node.nucedu 		= lnode.nucedu;
                node.lnode.prop 	= "Nucleus";
                node.lnode.relation = relation;
                node.rnode.prop 	= "Nucleus";
                node.rnode.relation = relation;
                for(int jj : lnode.sentenceID){
					node.sentenceID.add(jj);
				}
			}
			if(form.equals("NS")){
				node.filepath	 	= lnode.filepath;
			    node.nucspan 		= lnode.eduspan;
                node.nucedu 		= lnode.nucedu;
                node.lnode.prop 	= "Nucleus";
                node.lnode.relation = "span";
                node.rnode.prop 	= "Satellite";
                node.rnode.relation = relation;
                for(int jj : lnode.sentenceID){
					node.sentenceID.add(jj);
				}
			}
			if(form.equals("SN")){
				node.filepath	 	= rnode.filepath;
			    node.nucspan 		= rnode.eduspan;
                node.nucedu 		= rnode.nucedu;
                node.lnode.prop 	= "Satellite";
                node.lnode.relation = relation;
                node.rnode.prop 	= "Nucleus";
                node.rnode.relation = "span";
                for(int jj : lnode.sentenceID){
					node.sentenceID.add(jj);
				}
			}
			id++;
			stack.add(node);
		}
		return true;
	}

/// returns true if parsing is finished
	protected boolean endparsing(){
		if(stack.size() == 1 && queue.size() == 0){
			return true;
		}
		return false;
	}

/// returns the root node
	protected Node gettree(){
		if(endparsing()){
			return stack.get(0);
		}
		return null;
	}
}