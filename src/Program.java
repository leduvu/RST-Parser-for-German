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
// PROGRAMM.VERSION 24.03.17
// further description below
////////////////////////////////////////////////////////////////////////////////
import RST.*;
import GUIcalc.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.filechooser.FileNameExtensionFilter;


/// PROGRAM and GUI
public class Program extends JPanel implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	// TEXT PARTS
	final static String PROGRAM_TITLE	= "DPLP: A RST Parser using libsvm and .rs3 files";
	// Button borders
	final static String TRAIN_B_BORDER 	= "Train (.rs3)";
	final static String TEST_B_BORDER  	= "Test (.edus)";
	final static String GOLD_B_BORDER  	= "Gold (.rs3)";
	
	// Panel borders
	final static String TRAIN_BORDER	= "Train";
	final static String PREDICT_BORDER	= "Predict";
	final static String EVAL_BORDER		= "Process...";
	// Error panel borders
	final static String E_TRAIN_SELECT	= "Train - NO FOLDER SELECTED";
	final static String E_PRED_SELECT_T	= "Predict - NO TEST FOLDER SELECTED";
	final static String E_PRED_SELECT_G	= "Predict - NO GOLD FOLDER SELECTED";
	final static String E_PRED_FOLDER	= "Predict - NUMBER OF PRED AND GOLD FOLDERS HAS TO BE THE SAME";
	final static String E_NO_MODEL_P	= "Predict - NO EXISTING TRAINING MODEL";
	final static String E_NO_MODEL_T	= "Train - NO EXISTING TRAINING MODEL";
	final static String E_NO_PREDICTION = "Predict - NO PREDICTION YET";
	
	private JButton chooseFolderT = new JButton("Choose Folder");
	private JButton chooseFolderP = new JButton("Choose Folder");
	private JButton chooseFolderG = new JButton("Choose Folder");
	private JButton removeT		  = new JButton("Delete All");
	private JButton removeP		  = new JButton("Delete All");
	private JButton train		  = new JButton("Train");
	private JButton predict		  = new JButton("Predict");
	private JButton saveModel	  = new JButton("Save Model");
	private JButton loadModel	  = new JButton("Load Model");
	private JButton draw 			= new JButton("Draw Trees");
	private JTextArea trainArea   = new JTextArea(6, 50);
	private JTextArea predictArea = new JTextArea(6, 50);
	private JTextArea evalArea    = new JTextArea(10, 50);
	private JPanel trainPanel 	  = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private JPanel predictPanel   = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private JPanel evalPanel 	  = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private String modelname	  = "";
    private Predict p			  = null;
    private Train t				  = null;		
	
	
	List<File>	trainFolders   	= new ArrayList<File>();
	List<File>	predictFolders 	= new ArrayList<File>();
	List<File>	goldFolders    	= new ArrayList<File>();
	
/// Main Function  	
  public static void main(String[] args) {
  	Locale.setDefault(Locale.ENGLISH);
  	new Program().build();
  }
	
/// Button Actions
	// The casting of the serialization object to a SVM throws a compiler warning
    public void actionPerformed(ActionEvent e)
  	{
  		// Choose Folder train, predict and gold button
  		if(e.getSource() == chooseFolderT | e.getSource() == chooseFolderP | e.getSource() == chooseFolderG){
  			JFileChooser chooser;
  			chooser = new JFileChooser(); 
    		chooser.setCurrentDirectory(new java.io.File("."));
    		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    		// Disable the "All files" option.
    		chooser.setAcceptAllFileFilterUsed(false);    		
    		p = null;
    		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
    			// Train folder button
    			if(e.getSource() == chooseFolderT){
      				trainArea.append(chooser.getSelectedFile() + "\n");
      				trainFolders.add(chooser.getSelectedFile());
      				modelname = ""; // Reset modelname for prediction in case there was a model chosen before
      				t = null;
      			}
      			// Predict test folder button
      			else if(e.getSource() == chooseFolderP){
      				predictArea.append("T: " + chooser.getSelectedFile() + "\n");
      				predictFolders.add(chooser.getSelectedFile());
      			}
      			// Predict gold folder button
      			else{
      				predictArea.append("G: " + chooser.getSelectedFile() + "\n");
      				goldFolders.add(chooser.getSelectedFile());
      			}
      		}
    		else {
      			//System.out.println("No Selection");
      		}
    	}
    	// ------------------------------------------------------
    	// Train button
    	if(e.getSource() == train){
    		t = null; // Reset model
    		p = null;
    	    // Delete the matrix files
    		try{
    			File fileM 	= new File("matrix_.txt");
    			File fileL 	= new File("labelIDMap_.ser");
    			File fileV 	= new File("vocab_.ser");
    			Files.deleteIfExists(fileM.toPath());
    			Files.deleteIfExists(fileL.toPath());
    			Files.deleteIfExists(fileV.toPath());	
    				
    		} catch(IOException ex){
      			evalArea.append(ex.getMessage());	
   			}
    		
    		// If no train folder is selected
    		if(trainFolders.isEmpty()){
    			trainPanel.setBorder( new TitledBorder(E_TRAIN_SELECT) );
    		}
    		else{
    			trainPanel.setBorder( new TitledBorder(TRAIN_BORDER) );
				t = new Train(trainFolders, evalArea);
				t.execute();
    		}
    	}
    	
    	// ------------------------------------------------------
    	// Predict button
    	if(e.getSource() == predict){
    		p = null; // Reset p
    		// If no test folder is selected
    		if(predictFolders.isEmpty()){
    			predictPanel.setBorder( new TitledBorder(E_PRED_SELECT_T) );
    		}
    		// If no model exists
    		else if(t == null){
    			predictPanel.setBorder( new TitledBorder(E_NO_MODEL_P) );
    		}
    		// If training is still running
    		else if(!t.isDone() && modelname.equals("")){
    			predictPanel.setBorder( new TitledBorder(E_NO_MODEL_P) );
    		}
    		// If no gold folder is selected
    		// Start prediction without evaluation
    		else if(goldFolders.isEmpty()){
    			predictPanel.setBorder( new TitledBorder(PREDICT_BORDER) );
				p = new Predict(predictFolders, null, t.model, evalArea, modelname);
				p.no_eval = true;
				p.execute();	
    		}
    		// Start prediction with evaluation
    		else{
    			// If there is not the same number of test and gold folders
    			if(predictFolders.size() != goldFolders.size()){
    				predictPanel.setBorder( new TitledBorder(E_PRED_FOLDER) );
    			}
    			else{
    				predictPanel.setBorder( new TitledBorder(PREDICT_BORDER) );
					p = new Predict(predictFolders, goldFolders, t.model, evalArea, modelname);
					p.execute();				
    			}
    		}
    	}
    	
    	// ------------------------------------------------------
    	// Train remove button
    	if(e.getSource() == removeT){
    		// Delete the matrix files
    		try{
    			File fileM 	= new File("matrix_.txt");
    			File fileL 	= new File("labelIDMap_.ser");
    			File fileV 	= new File("vocab_.ser");
    			Files.deleteIfExists(fileM.toPath());
    			Files.deleteIfExists(fileL.toPath());
    			Files.deleteIfExists(fileV.toPath());	
    				
    		} catch(IOException ex){
      			evalArea.append(ex.getMessage());	
   			}

    		
    		trainFolders.clear();	// Clear the Folder Directories
    		trainArea.setText("");	// Clear the textarea
    		t = null;				// Reset the model
    		modelname = "";			// Reset modelname for vocab and labelIDMap
    	}
    	
    	// Predict remove button
    	if(e.getSource() == removeP){
    		predictFolders.clear();
    		goldFolders.clear();
    		predictArea.setText("");
    		p = null;
    	}
    	
    	// Save Model
    	if(e.getSource() == saveModel){
    		if(t == null){
    			trainPanel.setBorder( new TitledBorder(E_NO_MODEL_T) );
    		}
    		
    		if(t != null){  
    		
    			//Create a window 
    			String name = JOptionPane.showInputDialog(null,"Save as", "Model saving", JOptionPane.PLAIN_MESSAGE); 
    			if(!name.isEmpty() && !name.equals("") && !name.equals("model_") && !name.contains("/") && !name.contains("\\")){
    				Usefull.serialize(t.model, name + ".model");
    				
    				File oldLabelIDMap =new File("labelIDMap_.ser");
					File newLabelIDMap =new File(name + ".labelIDMap");

					oldLabelIDMap.renameTo(newLabelIDMap);
				
					File oldVocab =new File("vocab_.ser");
					File newVocab =new File(name + ".vocab");

					oldVocab.renameTo(newVocab);
				
					modelname = name;	// Set modelname so the prediction still finds the files
					evalArea.append("Saved Model: " + name + ".model \n");
    			}
				else{
					evalArea.append("Invalid name: " + name + "\n");
				}
    		}
    	}
    	
    	// Load Model
    	if(e.getSource() == loadModel){
    		trainFolders.clear();
    		JFileChooser chooser;
  			chooser = new JFileChooser(); 
    		chooser.setCurrentDirectory(new java.io.File("."));
      		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      		// Disable the "All files" option.
    		chooser.setAcceptAllFileFilterUsed(false);
      		FileNameExtensionFilter filter = new FileNameExtensionFilter("Model Files", "model");  
			chooser.setFileFilter(filter);
			t = new Train(); 

    		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				try{	
					InputStream file 	= new FileInputStream(chooser.getSelectedFile());
      				InputStream buffer  = new BufferedInputStream(file);
      				ObjectInput input 	= new ObjectInputStream (buffer);
      				t.model 			= (SVM) input.readObject();
      				String name			= chooser.getSelectedFile().getName();
      				modelname			= name.replace(".model", "");
      				file.close();
      				buffer.close();
      				input.close();
      			} catch(IOException ex){
					evalArea.append(ex.getMessage());	
   				} catch (ClassNotFoundException c) {
        			evalArea.append(c.getMessage());	
    			}
    			trainArea.append("Loaded Model: " + chooser.getSelectedFile() + "\n");
			}
			else{
				// No selection
			} 	
      			
    	}
    	
    	// Draw trees
    	if(e.getSource() == draw){
    		// If no test folder is selected
    		if(predictFolders.isEmpty()){
    			predictPanel.setBorder( new TitledBorder(E_PRED_SELECT_T) );
    		}
    		// If no model exists
    		else if(t == null){
    			predictPanel.setBorder( new TitledBorder(E_NO_MODEL_P) );
    		}
    		// If training is still running
    		else if(!t.isDone() && modelname.equals("")){
    			predictPanel.setBorder( new TitledBorder(E_NO_MODEL_P) );
    		}
    		// If no prediction yet
    		else if(p == null){
    			predictPanel.setBorder( new TitledBorder(E_NO_PREDICTION) );
    		}
    		// If prediction still running
    		else if(!p.isDone()){
    			evalArea.append("DRAW ERROR: Prediction is still running.");
    		}
    		// Else start drawing
    		else{
    			for(List<Node> predtrees : p.predtreesList){
    				Draw d = new Draw(predtrees);
    				d.execute();
    			}
    		}
    		
    	}
    	
  	}
  
/// Build Frame
  public void build(){
  	// Set everything to english NOTE: locale should be set in the main!
	JComponent.setDefaultLocale(Locale.ENGLISH);
    JFrame frame = new JFrame(PROGRAM_TITLE);
    trainPanel.setBorder( new TitledBorder(TRAIN_BORDER) );
    predictPanel.setBorder( new TitledBorder(PREDICT_BORDER));
    evalPanel.setBorder( new TitledBorder(EVAL_BORDER));
    
    // Text Area on the left
    trainPanel.add(new JScrollPane(trainArea));
	predictPanel.add(new JScrollPane(predictArea));
	evalPanel.add(new JScrollPane(evalArea));
	
	trainArea.setEditable(false);
	predictArea.setEditable(false);
	evalArea.setEditable(false);
	
	// Scroll to the bottom after each append
	DefaultCaret caret = (DefaultCaret)evalArea.getCaret();
	caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	
	// Set actionlistener
	chooseFolderT.addActionListener(this);
    chooseFolderP.addActionListener(this);
    chooseFolderG.addActionListener(this);
    train.addActionListener(this);
    predict.addActionListener(this);
    removeT.addActionListener(this);
    removeP.addActionListener(this);
    saveModel.addActionListener(this);
    loadModel.addActionListener(this);
    draw.addActionListener(this);
	
	
	// Buttons on the right
	JPanel trainButtons 	= new JPanel(new BorderLayout());
	JPanel predict1Buttons 	= new JPanel(new BorderLayout());
	JPanel predict2Buttons 	= new JPanel(new BorderLayout());
	JPanel modelButtons 	= new JPanel(new BorderLayout());
	JPanel selectButton0	= new JPanel(new BorderLayout());
	JPanel selectButton1	= new JPanel(new BorderLayout());
	JPanel selectButton2	= new JPanel(new BorderLayout());
	
	selectButton0.add(chooseFolderT, BorderLayout.PAGE_START);
	selectButton0.setBorder( new TitledBorder(TRAIN_B_BORDER));
	trainButtons.add(selectButton0,  BorderLayout.PAGE_START);
	
	trainButtons.add(train, BorderLayout.CENTER);
	trainButtons.add(removeT, BorderLayout.PAGE_END);
	
	
	selectButton1.add(chooseFolderP, BorderLayout.PAGE_START);
	selectButton1.setBorder( new TitledBorder(TEST_B_BORDER));
	selectButton2.add(chooseFolderG, BorderLayout.PAGE_START);
	selectButton2.setBorder( new TitledBorder(GOLD_B_BORDER));
	
	predict1Buttons.add(selectButton1, BorderLayout.PAGE_START);
	predict1Buttons.add(selectButton2, BorderLayout.PAGE_END);
	predict2Buttons.add(predict, BorderLayout.CENTER);
	predict2Buttons.add(removeP, BorderLayout.PAGE_END);
	
   	modelButtons.add(saveModel, BorderLayout.PAGE_START);
    modelButtons.add(loadModel, BorderLayout.PAGE_END);
    
    // Add to the bigger panels
    trainPanel.add(trainButtons);
    trainPanel.add(modelButtons);
	predictPanel.add(predict1Buttons);
	predictPanel.add(predict2Buttons);
	evalPanel.add(draw);
    
    // Add all bigger panels to the main panel
    frame.getContentPane().add(trainPanel, "North");
    frame.getContentPane().add(predictPanel, "Center");
    frame.getContentPane().add(evalPanel, "South");
    
    // Redirect the System.err and out Stream to the textArea
    OutputStream out = new OutputStream() {
    	@Override
    	public void write(final int b) throws IOException {
     		evalArea.append(String.valueOf((char) b));
    	}
    };
    System.setOut(new PrintStream(out, true));
  	System.setErr(new PrintStream(out, true));
  	
    
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	// For the close button
    frame.setSize(900,500);		 							// Size of the window
    frame.setLocationRelativeTo(null);                      // Put the window in the middle of the screen

    frame.setVisible(true);
  }

}