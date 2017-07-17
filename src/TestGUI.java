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
// OS:			MacOS 10.12.3	Windows
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
// TESTGUI.VERSION 22.03.17
// further description below
////////////////////////////////////////////////////////////////////////////////
import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
/**
 * TestGUI.java is a GUI for the Test.java Program.
 *
 * Usage: 	- compile with: javac TestGUI.java
 *			- execute with: java TestGUI
 * 							or just make a .jar file, so you can doubleklick to open the program
*/
public class TestGUI extends JPanel {
	private static final long serialVersionUID = 1L;

/// Main Function  	
  public static void main(String[] args) {
  	Locale.setDefault(Locale.ENGLISH);
  	new TestGUI().build();
  	try{
  		String[] arguments = {};
  		Test.main(arguments);			// Start the Test Program
  	} catch(IOException ex){
      	System.err.println(ex);	
   	}
  	
  }
  
  /// Build Frame
  public void build(){
  	// Set everything to english NOTE: locale should be set in the main!
	JComponent.setDefaultLocale(Locale.ENGLISH);
    JFrame frame 			= new JFrame("5-fold Cross Evaluation");
    JPanel consolePanel 	= new JPanel(new FlowLayout(FlowLayout.LEFT));
    JTextArea textArea    	= new JTextArea(22, 40);
    consolePanel.add(new JScrollPane(textArea));
    
    // Scroll to the bottom after each append
	DefaultCaret caret = (DefaultCaret)textArea.getCaret();
	caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    
    frame.getContentPane().add(consolePanel, "Center");
    
    // Redirect the System.err and out Stream to the textArea
    OutputStream out = new OutputStream() {
    	@Override
    	public void write(final int b) throws IOException {
     		textArea.append(String.valueOf((char) b));
    	}
    };
    System.setOut(new PrintStream(out, true));
  	System.setErr(new PrintStream(out, true));
  	
    // Stopping all running Threads
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	// For the close button
    frame.setSize(500,400);		 							// Size of the window
    frame.setLocationRelativeTo(null);                      // Put the window in the middle of the screen

    frame.setVisible(true);
    
  }

}