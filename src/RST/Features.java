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
// FEATURES.VERSION 21.03.17
// further description below
////////////////////////////////////////////////////////////////////////////////

package RST;
import java.util.*;
/**
 * Features.java is a class which generates an object Features, which uses the function getfeatures
 * to generate all kinds of features for every node/EDU
 *
 * "initially the stack is empty and the first elementary discourse unit (EDU) in the document 
 * is at the front of the queue. The parser can then choose either to shift the front of the 
 * queue onto the top of the stack, or to reduce the top two elements on the stack in a discourse 
 * relation." Ji and Eisenstein
 *
 * Input: 	1 queue		Changes - contains nodes
 *			2 stack		Changes - contains nodes
 *			3 edusize	number of EDUs in the file
 *
 * Usage: import RST; Features name = new Features(1, 2, 3); name.function();
*/
class Features
{
	private List<List<String>> status_feature 			= new ArrayList<List<String>>(); 
	private List<List<String>> structural_feature 		= new ArrayList<List<String>>(); 
	private List<List<String>> edu_feature 				= new ArrayList<List<String>>(); 
	private List<List<String>> distributional_feature 	= new ArrayList<List<String>>(); 
	private Node top1span  	= new Node();
	private Node top2span  	= new Node();
	private Node firstspan 	= new Node();
	private int edulen1 	= 0;
	private int edulen2		= 0;
	private int edusize 	= 0;
	
/// Constructor
	Features(List<Node> stack, List<Node> queue, int edusize){
		// Stack with 2 elements
        if(stack.size() >= 2){
            top1span = stack.get(stack.size()-1);
            top2span = stack.get(stack.size()-2);
        }
        // Stack with 1 element
        else if(stack.size() == 1){
        	top1span = stack.get(stack.size()-1);
        	top2span = null;
        }
        // Empty stack
        else{
        	top1span = null;
        	top2span = null;
        }

		// Queue with at least 1 element
		if(queue.size() > 0){
			firstspan = queue.get(0);			
		}
		// Empty queue
		else{
			firstspan = null;
		}
		
		this.edusize 	= edusize;
	}
	
/// adds all generated features to a list
	List<List<String>> getfeatures(){
		List<List<String>> features = new ArrayList<List<String>>();    
		status_features();   
		structural_features();
		edu_features();
		
		for(List<String> ff : status_feature){
			features.add(ff);			
		}
		
		for(List<String> ff : structural_feature){
			features.add(ff);
		}
		
		for(List<String> ff : edu_feature){
			features.add(ff);
		}
		
		for(List<String> ff : distributional_feature){
			features.add(ff);
		}
		return features;
	}
	
/// generates the stack and queue related features
	void status_features(){
		// Stack
		if(top1span == null && top2span == null){
			status_feature.add(addfeature("Stack", "Empty"));
		}
		
		if(top1span != null && top2span == null){
			status_feature.add(addfeature("Stack", "OneElem"));
		}
		
		if(top1span != null && top2span != null){
			status_feature.add(addfeature("Stack", "MoreElem"));
		}

		// Queue
		if(firstspan == null){
			status_feature.add(addfeature("Queue", "Empty"));
		}
		else{
			status_feature.add(addfeature("Queue", "NonEmpty"));
		}
	}
	
/// generates EDU length and distance features
	void structural_features(){
	
		if(top1span != null){ 
			edulen1 = top1span.eduspan[1] - top1span.eduspan[0] + 1; 
			structural_feature.add(addfeature("Top1-Stack", "Length-EDU", edulen1));
			structural_feature.add(addfeature("Top1-Stack", "Dist-To-Begin", top1span.eduspan[0]));
			structural_feature.add(addfeature("Top1-Stack", "Dist-To-End", edusize-top1span.eduspan[1]));
		}
		
		if(top2span != null){ 
			edulen2 = top2span.eduspan[1] - top2span.eduspan[0] + 1; 
			structural_feature.add(addfeature("Top2-Stack", "Length-EDU", edulen2));
			structural_feature.add(addfeature("Top2-Stack", "Dist-To-Begin", top2span.eduspan[0]));
			structural_feature.add(addfeature("Top2-Stack", "Dist-To-End", edusize-top2span.eduspan[1]));
		}
	}
	
/// generates EDU text related features
	void edu_features(){
		if(top1span != null){ 
			String[] wordList = top1span.text.split("\\s+");
			edu_feature.add(addfeature("Top1-Stack", "nEdu", top1span.eduspan[1]-top1span.eduspan[0]+1));
			edu_feature.add(addfeature("Top1-Stack", "first-word", wordList[0]));
			edu_feature.add(addfeature("Top1-Stack", "last-word", wordList[wordList.length-1]));
			if(wordList[wordList.length-1].equals("?")){
				edu_feature.add(addfeature("Top1-Stack", "question", wordList[wordList.length-1]));
			}
			if(wordList[wordList.length-1].equals(".")){
				edu_feature.add(addfeature("Top1-Stack", "statement", wordList[wordList.length-1]));
			}
			if(wordList[wordList.length-1].equals("!")){
				edu_feature.add(addfeature("Top1-Stack", "exclamation", wordList[wordList.length-1]));
			}
			
			String sentenceIDs = "";
    		for(int ii : top1span.sentenceID){
    			sentenceIDs = sentenceIDs + " " + Integer.toString(ii);
    		}
    		edu_feature.add(addfeature("Top1-Stack", "sentenceIDs", sentenceIDs));			
		}
		// Its the same with top2span - maybe it would be nicer to put the code into a separate function, 
		// so it doesn't has to be copy pasted
		if(top2span != null){
			String[] wordList = top2span.text.split("\\s+");
			edu_feature.add(addfeature("Top2-Stack", "nEdu", top2span.eduspan[1]-top2span.eduspan[0]+1));
			edu_feature.add(addfeature("Top2-Stack", "first-word", wordList[0]));
			edu_feature.add(addfeature("Top2-Stack", "last-word", wordList[wordList.length-1]));
			if(wordList[wordList.length-1].equals("?")){
				edu_feature.add(addfeature("Top2-Stack", "question", wordList[wordList.length-1]));
			}
			if(wordList[wordList.length-1].equals(".")){
				edu_feature.add(addfeature("Top2-Stack", "statement", wordList[wordList.length-1]));
			}
			if(wordList[wordList.length-1].equals("!")){
				edu_feature.add(addfeature("Top2-Stack", "exclamation", wordList[wordList.length-1]));
			}
			
			
			String sentenceIDs = "";
    		for(int ii : top2span.sentenceID){
    			sentenceIDs = sentenceIDs + " " + Integer.toString(ii);
    		}
    		edu_feature.add(addfeature("Top2-Stack", "senteceIDs", sentenceIDs));
		}
	}
	
/** gets the information and puts it in a List
 * 
 * \param status 	Top1-Stack or Top2-Stack
 * \param specific 	Length-EDU|Dist-To-Begin|Dist-To-End
 * \param value the value of the specific
*/
	private List<String> addfeature(String status, String specific, int value){
		List<String> elementList		= new ArrayList<String>();
		elementList.add(status);
		elementList.add(specific);
		elementList.add(Integer.toString(value));
		
		return elementList;
	}
	
/** gets the information and puts it in a List
 * 
 * \param status Top1-Stack or Top2-Stack
 * \param disrep first-word|last-word|question|statement|exclamation
 * \param word 	 the word of the disrep
*/
	private List<String> addfeature(String status, String disrep, String word){
		List<String> elementList		= new ArrayList<String>();
		elementList.add(status);
		elementList.add(disrep);
		elementList.add(word);
		
		return elementList;
	}

/** gets the information and puts it in a List
 * 
 * \param type	 Queue or Stack
 * \param status Empty|nonEmpty|OneElem|MoreElem
*/
	private List<String> addfeature(String type, String status){
		List<String> elementList		= new ArrayList<String>();
		elementList.add(type);
		elementList.add(status);
		
		return elementList;
	}
		
} // End of class Features