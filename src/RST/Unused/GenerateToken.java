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
// GENERATETOKEN.VERSION 22.03.17
// further description below
////////////////////////////////////////////////////////////////////////////////

package RST;
import java.util.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import javax.xml.stream.*;
import javax.xml.namespace.QName;
import java.io.FileReader;
/**
 * GenerateToken.java is a class which generates an object GenerateToken, which can use the function
 * read() to generate the tokenMap from tigerSearch tool .xml files.
 *
 * Usage: import RST; GenerateToken name = new GenerateToken(); name.read();
 *
 * \attention {All global variables are protected. There are no getter and setter functions. 
 * The Token class is meant to be similar to a struct in C++.}
*/
public class GenerateToken
{
	// Initialization
	public TreeMap<Integer,Token> tokenMap 	= new TreeMap<Integer,Token>();
	int tokenID = 1;
	
/** reads a tigerSearch tool .xml file to get all information and saves it to tokenMap
 * 
 * \param filename 	 name of the .xml file including the ending .xml
 * \param syntaxpath directory of the folder with the .xml files
*/
	TreeMap<Integer,Token> read(String filename, String syntaxPath){
		// Variables
		if (filename.indexOf(".") > 0) {
    		filename = filename.substring(0, filename.lastIndexOf("."));
		}
		File filePath = new File(syntaxPath + "/" + filename + ".xml");
		XMLInputFactory factory = XMLInputFactory.newInstance();
		
		// Read .xml file and generate all tokens
		try {
			XMLStreamReader streamReader = factory.createXMLStreamReader(new FileReader(filePath));
		
			while (streamReader.hasNext()) {
            	int event = streamReader.next();

        		if(event == XMLStreamConstants.START_ELEMENT){
        			if(streamReader.getLocalName().equals("t")){
        					String tempID	= streamReader.getAttributeValue(null, "id");
        					String word		= streamReader.getAttributeValue(null, "word");
        					String lemma 	= streamReader.getAttributeValue(null, "lemma");
        					String pos 		= streamReader.getAttributeValue(null, "pos");
        					String morph	= streamReader.getAttributeValue(null, "morph");
        					String sentence = tempID.substring(0, tempID.indexOf("_"));
        					// Delete all non numeric characters
        					int sentenceID 	= Integer.parseInt( sentence.replaceAll("[^\\d.]", ""));
							Token token = new Token(sentenceID, tokenID, word, lemma, pos, morph);
							tokenMap.put(tokenID, token);
							tokenID++;
            		}
				}	
			} 
		} catch (Exception e) {
            e.printStackTrace();
    	}
    	return tokenMap;
	}
}