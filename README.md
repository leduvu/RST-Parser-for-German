# Parser-for-German
A reimplementation of Ji and Eisenstein's (2014) Parser in order to use .rs3 files as training data.

////////////////////////////////////////////////////////////////////////////////
// Author:      Le Duyen Sandra Vu 
// Matr.Nr.:    768693 - University of Potsdam B.Sc. Student
// Compiler:    javac 1.8.0_66
// OS:          MacOS 10.12.3
// Subject:     Computational Lingustics Bachelor Thesis
//              Automatic parsing of rhetorical structures in German text
//
// The code is based on Ji, Eisensteins paper and code!
// Differences and similarities are discussed below.
//
// Code with model:     https://github.com/jiyfeng/DPLP
// Code without model:  https://github.com/jiyfeng/RSTParser
// Paper:               http://www.cc.gatech.edu/~jeisenst/papers/ji-acl-2014.pdf
//
// 23.06.2017
////////////////////////////////////////////////////////////////////////////////

TABLE OF CONTENTS
==================================
 | - Note -
 | 1 Copyright
 | 2 Tested On
 | 3 Introduction
 | 4 Prepare
 | 5 Compile
 | 6 Run
 | 7 Data structure
 | 8 Folder structure
 | 9 Comparison to DPLP of Ji and Eisenstein

NOTE:
==================================
0. If you want to know how to use libsvm as a library:
   SVM.java reads a matrix, trains a model and predicts the labels using libsvm.
	

1. Conversion to .rs3 is not possible yet, as the predicted tree is a binary tree.
   To get the .rs3 file, the filler nodes need to be predicted.
   Important: The parentID in the .rs3 format is not the parentID of a node!
              It is more a "next node" ID.
               
2. The title (first line) of an .edus file will be skipped!
   Important: If there is no title in your testing file, please add a "title".
    
3. Syntax features are not implemented.

4. Depending on the editor the code may look different, as it contains tabulators
   instead of spaces.
   
 
1 COPYRIGHT:
==================================
libsvm has a copyright. The file is included in the Code Folder.


2 TESTED ON:
==================================
Windows 10             Java Version 1.8u121
MacOS Sierra 10.12.3   Java Version 1.8.0_66

3 INTRODUCTION
==================================
A RST (Rhetorical Structure Theory) Parser using a linear support vector machine and 
.rs3 files for training. The .rs3 files are usually generated from the RSTTool or from 
the rstWeb tool.
The .edus Test files contain raw text, where each line represents an EDU.


The src folder contains 4 programs.
    1 Debug.java        Used for debugging.
    
    2 Test.java         Performs a 5-fold cross validation/evaluation on the PCC corpus
                        using the RST Parser. - The directories are hard coded.
                        So it will only work, if the folder structure stays the same.
                      
    3 TestGUI.java      Test.java + GUI. Terminal is not needed.
    
    4 Program.java      A program to train, predict, evaluate and draw.
                        (See also: Code/doc/example.png)
                        Tip: You can predict a tree, without choosing a gold Folder.
                  

4 PREPARE:
==================================
Java8 JDK    to compile and run the code.
                - set the classpaths, so Java can run from every directory in the terminal
                - open terminal and go to the src directory 
                
                alternative:
                - just use an IDE
                
Java8 JRE    to run the programs (.jar files) WITHOUT compiling.
                no Terminal or IDE needed - just double click

5 COMPILE:
==================================
javac Debug.java
javac Test.java
javac TestGUI.java
javac Program.java

jar cfm Program.jar MANIFEST.MF *.class RST/*.class GUIcalc/*.class
	with MANIFEST.MF containing:
Manifest-Version: 1.0
Class-Path: .
Main-Class: Program

jar cfm TestGUI.jar MANIFEST.MF *.class RST/*.class
	with MANIFEST.MF containing:
Manifest-Version: 1.0
Class-Path: .
Main-Class: TestGUI



6 RUN:
==================================
java Debug
java Test
java TestGUI
java Program

or

Double click:
    TestGUI.jar
    Program.jar


7 DATA STRUCTURE:
==================================
The data will be converted to a „binary tree“, but the tree itself, will be represented as
a (root) node! 
In order to get the full tree you have to iterate over all the nodes of the tree (there are already Depth-first search and Breadth-first search functions in the Tree.class).


8 FOLDER STRUCTURE:
==================================

.
├── data                    Data for testing, training, predicting and evaluating
│   │                       from The Potsdam Commentary Corpus 2.0 (PCC 2.0)
│   │						
│   ├── debug               Some files for testing with Debug.java
│   ├── edu                 Test Data:      Contains .edus files (each line one EDU
│   │                                       Generated from The PCC 2.0	
│   ├── rst                 Training Data:  Contains .rs3 files				
│   └── unused syntax       Contains Syntax information,
│                           maybe useful for further feature implementations
├── doc
│   └── html                Documentation of the Code generated by Doxygen
│
└── src                     Programs and libraries
    ├──	GUIcalc             GUI Background Calculation Code for program.java
    ├── libsvm              Library for the Support Vector Machines
    └── RST                 RST Parser Code
	
	
	
9 COMPARISON TO DPLP of Ji and Eisenstein
==================================

                            Vu                              Ji and Eisenstein
--------------------------------------------------------------------------------------
Input:                 |  .rs3 files  (training)       |  .dis files    (training)
                       |  .edus files (testing)        |  .merge files  (testing)
                       |  .edus: raw text, each line   |  .merge: generated from Stanford 
                       |  one EDU                      |  CoreNLP
--------------------------------------------------------------------------------------
Output:                |  .txt                         |  .ps 
                       |  (drawn tree without relation)|  (drawn tree using ntlk.draw) 
                       |                               |  
                       |  - brackets function in       |  .brackets
                       |   Tree.java -                 | 
--------------------------------------------------------------------------------------
Features:              |  - status features            |  - status features 
                       |  - structural features        |  - structural features
                       |  - edu features               |  - edu features 
                       |                               |  - lexical features
                       |                               |  - distributional features
                       |                               |  - brown corpus features
--------------------------------------------------------------------------------------
Feature Select         |  no                           |  yes
                       |                               |
                       |  - FeatureSelect.java in      |
                       |  RST/Unused - Folder -        |
--------------------------------------------------------------------------------------
Learn algorithm:       |  libsvm   C.SVC               |  sklearn.svm   LinearSVC
                       |  Kernel type        = linear  |  Kernel type        = linear 
                       |  C                  = 0.8     |  C                  = 1.0
                       |  Stopping criterion = 0.001   |  Stopping criterion = 1e-7
                       |  Penalty            = l1      |  Penalty            = l1
--------------------------------------------------------------------------------------
Coding language:       |  Java                         |  Python
--------------------------------------------------------------------------------------
GUI:                   |  yes                          |  no
--------------------------------------------------------------------------------------                                  


