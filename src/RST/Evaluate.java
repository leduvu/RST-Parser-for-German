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
// EVALUATE.VERSION 23.06.17
// further description below
////////////////////////////////////////////////////////////////////////////////

package RST;
import java.util.*;
/**
 * Evaluate.java is a class which generates an object Evaluate, which can use functions to
 * calculate F1 (and precision and recall).
 *
 * Usage: import RST; Evaluate name = new Evaluate(); Evaluate.function();
*/
public class Evaluate
{
	// Initialization
	private List<Double> span_percision		= new ArrayList<Double>();
	private List<Double> span_recall		= new ArrayList<Double>();
	private List<Double> nuc_percision		= new ArrayList<Double>();
	private List<Double> nuc_recall			= new ArrayList<Double>();
	private List<Double> rela_percision		= new ArrayList<Double>();
	private List<Double> rela_recall		= new ArrayList<Double>();

/// Constructor
	public Evaluate(){
	 	span_percision.clear();
	 	span_recall.clear();
	 	nuc_percision.clear();
	 	nuc_recall.clear();
	 	rela_percision.clear();
	 	rela_recall.clear();
	}
	
/** gets the bracket form of a tree [nucpan1, nucpan2, property, relation],
 * than calculates the F1 score for span, nuclearity and relation
 *
 * \param goldroot root node of the gold tree
 * \param predroot root node of the pred tree
*/	
	public double[] eval(Node goldroot, Node predroot){
		

		if(predroot == null){
			double[] bad = new double[] { 0, 0, 0 };
			return bad;
		}
		
		// PRINTING TREES HERE
		//Usefull.print(goldroot, goldroot.text);
		//Usefull.print(predroot, predroot.text);

		Tree goldtree = new Tree();
		Tree predtree = new Tree();
		// Note: Bracketing changes the spanorder info
		// so eduspan[0] is always smaller than eduspan[1]
		// This is not corrected in the tree, so the nuclearity stays the same
		List<List<String>> goldbrackets = goldtree.bracketing(goldroot);
		List<List<String>> predbrackets = predtree.bracketing(predroot);
        
        List<String> levels = Arrays.asList("span", "nuclearity", "relation");
        for(String level : levels){
            if(level.equals("span")){
                eval(goldbrackets, predbrackets, 0);
            }
            if(level.equals("nuclearity")){
                eval(goldbrackets, predbrackets, 1);
            }
            if(level.equals("relation")){
                eval(goldbrackets, predbrackets, 2);
            }
        }
		return report();
	}

/** gets the information of the brackets and calculates precision and recall
 *
 * \param golbrackets 	list with information of every gold node
 * \param predbrackets 	list with information of every pred node
*/
	// List<String> eduspan[0] eduspan[1] nucleus relation ---- of predbrackets
	private boolean eval(List<List<String>> goldbrackets, List<List<String>> predbrackets, int idx){
	    List<String> goldspan = new ArrayList<String>();
	    List<String> predspan = new ArrayList<String>();
	    
	    List<String> intersection = new ArrayList<String>();
	    
	    // Get the specific information span or property or relation from gold brackets
	    for(int ii = 0; ii < goldbrackets.size(); ii++){
	    	String joined = "";
			for(int aa = 0; aa <= idx; aa++){
				joined += goldbrackets.get(ii).get(aa);
			}
			goldspan.add(joined);
		}
		// Get the specific information span or property or relation from pred brackets
		for(int ii = 0; ii < predbrackets.size(); ii++){
			String joined = "";
			for(int aa = 0; aa <= idx; aa++){
				joined += predbrackets.get(ii).get(aa);
			}
			predspan.add(joined);
		}
		
		
		// Save the size of predspan
		int predspanSize = predspan.size();
        
        // Generate the intersection
        for(String span : goldspan){
        	if(predspan.contains(span)){
        		// Keep track of the amount of the elements of both sets
        		// If pred set has an element only once, but gold span has it twice
        		// it will not counted twice!
        		intersection.add(span);
        		predspan.remove(predspan.indexOf(span));
        	}
        }
                
        double p = 0.0;
        double r = 0.0;
        
        // Calculate precision p and recall r
        p = (double) intersection.size() / goldspan.size();
        r = (double) intersection.size() / predspanSize;
        
        // Add all precisions and recalls of every tree to a list
        if(idx == 0){
            span_percision.add(p);
            span_recall.add(r);
        }	
        if(idx == 1){
            nuc_percision.add(p);
            nuc_recall.add(r);
        }
        if(idx == 2){
            rela_percision.add(p);
            rela_recall.add(r);
        }

        return true;
	}

/** Summarize all numbers of the list and divide it with the size auf the list to get the mean
 *
 * \param numberList List with numbers of type double
*/
	private double getmean(List<Double> numberList) {
    	double total = 0.0;
    	for(double d: numberList) {
        	total = total + d;
    	}
    	return total / (numberList.size());
	}
	
/// Calculates F1 score for span, nuclearity and relation
	private double[] report(){
		double[] values = new double[3];
	   	List<String> levels = Arrays.asList("span", "nuclearity", "relation");
    	double p = 0.0;
    	double r = 0.0;
    	double f1 = 0.0;
    	
    	for(String level : levels){    
        	if(level.equals("span")){
        		// Calculate the mean of all precisions and recalls of all the trees
                p = getmean(span_percision);
                r = getmean(span_recall);
                // Calculate F1
                f1 = (2 * p * r) / (p + r);
                //System.out.println("F1 score on span level is " + String.format("%.2f", f1));
                values[0] = f1;
            }
            if(level.equals("nuclearity")){
                p = getmean(nuc_percision);
                r = getmean(nuc_recall);
                f1 = (2 * p * r) / (p + r);
                //System.out.println("F1 score on nuclearity level is " + String.format("%.2f", f1));
                values[1] = f1;
            }
            if(level.equals("relation")){
                p = getmean(rela_percision);
                r = getmean(rela_recall);
                f1 = (2 * p * r) / (p + r);
                //System.out.println("F1 score on relation level is " + String.format("%.2f", f1));
                values[2] = f1;
            }
		}
		return values;
	}
}