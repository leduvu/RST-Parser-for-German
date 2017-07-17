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
// TOKEN.VERSION 22.03.17
// further description below
////////////////////////////////////////////////////////////////////////////////

package RST;
/**
 * Token.java is a class which generates an object Token to store token specific
 * information.
 * The information usually comes from the tigerSearch tool .xml files, which contains
 * syntactical information.
 * \sa GenerateToken.java
 * 
 * INPUT: sentenceID, tokenID, word, lemma, pos, morph
 *
 * Usage: import RST; Token name = new Token(); name.sentenceID = 1;
 *
 * \attention {All global variables are protected. There are no getter and setter functions. 
 * The Token class is meant to be similar to a struct in C++.}
*/
protected class Token
{
	// Initialization
	protected int sentenceID	= 0;
	protected int tokenID		= 0;
	protected int eduID			= 0;
	
	protected String word		= "";
	protected String lemma		= "";
	protected String pos		= "";
	protected String morph		= "";
	
/// Constructor
	Token(int sentenceID, int tokenID, String word, String lemma, String pos, String morph){
		this.sentenceID = sentenceID;
		this.tokenID 	= tokenID;
		this.word 		= word;
		this.lemma 		= lemma;
		this.pos 		= pos;
		this.morph 		= morph;
	}
}