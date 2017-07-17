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
// TESTDATA.VERSION 22.03.17
// further description below
////////////////////////////////////////////////////////////////////////////////
package RST;
import java.util.*;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
/**
 * TestData.java is a class which generates an object TestData which can use the getedus()
 * function only.
 *
 * Usage: import RST; TestData name = new TestData(); name.getedus();

*/
public class TestData
{
	List<String> edus			= new ArrayList<String>();
	protected int id				= 1;
	private   int sentenceID 		= 1;
	
/** gets the EDUs from the .edus file
 * 
 * \param filePath 	directory of a .edus file
*/
	List<Node> getedus(String filePath){
		List<Node> eduList		= new ArrayList<Node>();

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

			String line;
			br.readLine();	// Skip title
			id++;
			while ((line = br.readLine()) != null) {
				Node node 		= new Node();
            	node.text 		= line;
            	node.id			= id;
            	node.filepath 	= filePath;
            	node.sentenceID.add(sentenceID);
            	node.eduspan[0]	= id;
            	node.eduspan[1]	= id;
            	node.nucspan[0]	= id;
            	node.nucspan[1]	= id;
            	node.nucedu 	= id;
            	eduList.add(node);
            	id++;
            	if(line.contains(" .")){
            		sentenceID++;
            	}
			}
			sentenceID = 1;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return eduList;	
	}
} // End of TestData.java