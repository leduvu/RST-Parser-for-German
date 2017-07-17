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
// FEATURESELECT.VERSION 22.03.17
// further description below
////////////////////////////////////////////////////////////////////////////////

import java.util.*;
/**
 * FeatureSelect.java is a class which generates an object FeatureSelect
 * 
 * Input: 	1 topn 		minimum number of existing features
 * 			2 method	"Frequency"
 *
 * Usage: import RST; FeatureSelect name = new FeatureSelect(1, 2); name.function();
 *		  You also need to add some functions of loss.java into Data.java	
 *
*/
public class FeatureSelect
{
	int topn 		= 0;
	String method 	= "";
	
/// Constructor
	FeatureSelect(int topn, String method){
		this.topn 	= topn;
		this.method = method;
	}
	
	 LinkedHashMap<Integer, Double> select(LinkedHashMap<List<String>, Integer> vocab, double[][] freqTable){
		LinkedHashMap<Integer, Double> valvocab = new LinkedHashMap<Integer, Double>();
		if(method.equals("Frequency")){
			valvocab = frequency(vocab, freqTable);
		}
		LinkedHashMap<Integer, Double> newvocab = rank(valvocab);
		return newvocab;
	}
	
	LinkedHashMap<Integer, Double> rank(LinkedHashMap<Integer, Double> vocab){
		LinkedHashMap<Integer, Double> valvocab = new LinkedHashMap<Integer, Double>();
		for(int featureID : vocab.keySet()){
			if(!(vocab.get(featureID) >= topn)){
				valvocab.put(featureID, vocab.get(featureID));
			}
		}
        
		return valvocab;	
	}
	LinkedHashMap<Integer, Double> frequency(LinkedHashMap<List<String>, Integer> vocab, double[][] freqTable){
		LinkedHashMap<Integer, Double> valvocab = new LinkedHashMap<Integer,Double>();
		int ii = 0;
		for(List<String> features : vocab.keySet()){
			
			double sum = 0.0;
			for(int jj = 0; jj < freqTable[ii].length; jj++){
				sum = sum + freqTable[ii][jj];
			}
			ii++;
			valvocab.put(vocab.get(features), sum);
		}
            
        return valvocab;
	}
}