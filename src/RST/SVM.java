/*
Line 57-60 and line 83-115 are copied from the libsvm library:
Copyright (c) 2000-2014 Chih-Chung Chang and Chih-Jen Lin
Licensed under the modified BSD Licence.

----------------------------------------------------------------------------------------
The code excluding line 57-60 and line 83-115:
Copyright 2017 Le Duyen Sandra Vu

Licensed under GNU General Public License as published by the Free Software Foundation, either 
version 3 of the License, or (at your option) any later version.
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
// This class uses the libsvm library.
//
// SVM.VERSION 22.03.17
// further description below
////////////////////////////////////////////////////////////////////////////////

package RST;
import java.util.*;
import java.io.*;
import libsvm.*;
/**
 * SVM.java is a class which generates an object Data, which can use the train and predict function.
 * A linear support vector machine is used from the libsvm library.
 * Also the file reading part of the code is copied from libsvm, so the read
 * data fits correctly into the svm-model of the library.
 *
 * Usage: import RST; SVM name = new SVM(); name.train();
*/
public class SVM implements Serializable
{
	private static final long serialVersionUID = 1L;
	private svm_model model;
	private int matrixsize = 0;

/// stops printing the stuff from .svm_train(x, y)
/// is copied from libsvm
	private static svm_print_interface svm_print_null = new svm_print_interface()
	{
		public void print(String s) {}
	};

/** uses libsvm support vector machine for training
 * mostly copied from libsvm
 *
 * \param matrixPath directory where the matrix should be saved later
*/
	public svm_model train(String matrixPath){		
    	// Preparing the SVM param
    	svm_parameter param	= new svm_parameter();
    	param.svm_type		= svm_parameter.C_SVC;
    	param.kernel_type	= svm_parameter.LINEAR;
    	param.gamma			= 0.5;
    	param.nu			= 0.5;
    	param.cache_size	= 20000;
    	param.C				= 0.8;
    	param.eps			= 0.001;
    	param.p				= 0.1;
		

    
    	// Read in training data (copied from libsvm)
		try {
			BufferedReader br = new BufferedReader(new FileReader(matrixPath));
			Vector<Double> vy = new Vector<Double>();
			Vector<svm_node[]> vx = new Vector<svm_node[]>();
			int max_index = 0;
			svm_node[] x = null;
			
			String line = "";
			while ((line = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");
				vy.addElement(Double.valueOf(st.nextToken()));
				matrixsize = st.countTokens()/2;
				x = new svm_node[matrixsize];
				for(int j=0 ; j < matrixsize; j++)
				{
					x[j] 		= new svm_node();
					x[j].index 	= Integer.parseInt(st.nextToken());
					x[j].value 	= Double.valueOf(st.nextToken());
            	}
        	if(matrixsize > 0) max_index = Math.max(max_index, x[matrixsize-1].index);
			vx.addElement(x);
        	}
			br.close();
        	
        	svm_problem prob = new svm_problem();
			prob.l = vy.size();
			prob.x = new svm_node[prob.l][];
			for(int i = 0; i < prob.l; i++){
				prob.x[i] = vx.elementAt(i);
			}
			prob.y = new double[prob.l];
			for(int i = 0; i < prob.l; i++){
				prob.y[i] = vy.elementAt(i);
			}
			
			// Don't print any stuff from .svm_train(x, y)
			svm.svm_set_print_string_function(svm_print_null);
			// TRAINING
    		model	= svm.svm_train(prob, param);
    		
    	}catch (Exception e){

    	}
    	return model;
    }
   
/** predicts the label/Shift Reduce action for a node/an EDU
 *
 * \param testPath 	directory of a data folder with .edus files
 * \param labelPath directory of the labelIDMap seriazed file
 * \param vocabPath directory of the vocab serialized file
*/
    // The casting of the serialization object to a LinkedHashmap throws a compiler warning
    @SuppressWarnings("unchecked")
    public List<Node> predict(String testPath, String labelPath, String vocabPath){
    	List<Node> trees 				= new ArrayList<Node>();
		LinkedHashMap<Double, String> labelIDMap 		= new LinkedHashMap<Double, String>();
		LinkedHashMap<List<String>, Integer> vocab		= new LinkedHashMap<List<String>,Integer>();
		
		//deserialize the .ser file
    	try{
    		// labelIDMap
      		InputStream file1 	= new FileInputStream(labelPath);
      		InputStream buffer1 = new BufferedInputStream(file1);
      		ObjectInput input1 	= new ObjectInputStream (buffer1);
      		labelIDMap = (LinkedHashMap<Double, String>) input1.readObject();
			file1.close();
			buffer1.close();	
      		input1.close();
			
			// vocab
      		InputStream file2 	= new FileInputStream(vocabPath);
      		InputStream buffer2 = new BufferedInputStream(file2);
      		ObjectInput input2 	= new ObjectInputStream (buffer2);
      		vocab = (LinkedHashMap<List<String>, Integer>) input2.readObject();
      		file2.close();
      		buffer2.close();	
      		input2.close();

    	} catch(IOException ex){
      		System.err.println(ex);
   		} catch (ClassNotFoundException c) {
        	System.err.println(c);
    	}
		
		
		File dir = new File(testPath);
  		// for every .edus test file: build predicted RSTTree
  		boolean no_eduFile = true;

  		for(File file : dir.listFiles()) {
    		if(file.getName().endsWith(".edus")) {
    			List<Node> stack 				= new ArrayList<Node>();
				List<Node> queue 				= new ArrayList<Node>();
    			String filename = file.getName();
    						
				List<List<String>> featureList 	= new ArrayList<List<String>>();
				// Get EDUs
				TestData doc					= new TestData();
				queue							= doc.getedus(testPath + "/"+ filename);
				int edusize = doc.id;
		
				SRParser sr  = new SRParser(stack, queue, edusize);
				while(!sr.endparsing()){
					// Generate Features
					Features features 		= new Features(sr.stack, sr.queue, edusize-1);
					featureList 			= features.getfeatures();
					
					// Sort the featureIDs in ascending order
					// So it is like the libsvm matrix order
					// label featureID:1 featureID:1 etc.
					List<Integer> featureIDList = new ArrayList<Integer>();
					
					for(List<String> f : featureList){
						if(vocab.get(f) != null){
							featureIDList.add(vocab.get(f));
						}
					}
					Collections.sort(featureIDList);
					
					// Get predicted label/action
					svm_node[] x = new svm_node[matrixsize];;
					for(int j = 0 ; j < matrixsize; j++)
					{
						if(j < featureIDList.size()){
							x[j] 		= new svm_node();
							x[j].index 	= featureIDList.get(j);
							x[j].value 	= 1.0;
						}
						else{
							x[j] 		= new svm_node();
							x[j].index 	= 1;
							x[j].value 	= 0;
						}
            		}
					
					// Calculate the right label/action
					int nr_class 		= model.nr_class;
    				double[] dec_values = new double[nr_class * (nr_class - 1) / 2];
    				double labelID 		= svm.svm_predict_values(model, x, dec_values);
    				// ## dec_values contains the decision values now
    				String label 		= labelIDMap.get(labelID);		// Convert labelID to label
					
					// Use this label/action to build the tree
					List<String> action	= new ArrayList<String>();
					String[] item 		= label.split("-");
					if(item.length == 1){
						action.add(item[0]);
						action.add("Null");
						action.add("Null");
					}
					else if (item.length == 3){
						action.add(item[0]);
						action.add(item[1]);
						action.add(item[2]);
					}
					else if(item.length == 4){
						action.add(item[0]);
						action.add(item[1]);
						action.add(item[2] + "-" + item[3]);
					}
					if(!sr.operate(action)){
						System.err.println(filename + " could not build tree. Eval: span = 0; nuc = 0; rel = 0");
						break;
					}
				}
				no_eduFile = false;
				Node tree = sr.gettree();
				trees.add(tree);
				doc.id = 1;
			}						
		}
		if(no_eduFile){
			System.err.println("No .edus file in folder: " + testPath);
		}
    return trees;
    } 

} // End of SVM.java