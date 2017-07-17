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
// TREE.VERSION 22.03.17
// further description below
////////////////////////////////////////////////////////////////////////////////

package RST;
import java.util.*;
import javax.xml.stream.*;
import java.io.FileReader;
/**
 * Tree.java is a class which generates an object Tree. You can only use
 * the build(path, filename), getedus(node) and bracketing(node) function.
 * 
 * The data from the files will be parsed and the trees will be built.
 *
 * Usage: import RST; Tree name = new Tree(); name.function();
*/
public class Tree
{
	// Initialization
	protected Node root 						= null;
	protected TreeMap<Integer,Node> nodeMap 	= new TreeMap<Integer,Node>();	 			// key: xmlID value: node
	protected int edusize						= 0;
	protected List<List<List<String>>> sampleList 	= new ArrayList<List<List<String>>>();	// samples/features
	protected List<List<String>> actionList			= new ArrayList<List<String>>();		// list of (action, form, relation) for every node
	private int id 							= 1;
	private int sentenceID 					= 1;
	
/** builds the trees, backpropagates all information and generates samlples aka 
 * Features
 * 
 * \param rstPath 	directory of .rs3 file
*/
	public boolean build(String rstPath){
		Node tree = buildtree(rstPath);	// Build Tree
		backprop(tree);					// Backprop Information
		generate_samples(tree);			// Generate samples(Features) 
		//debug();
		return true;
	}

/** goes through a list with all nodes and gets the ones without children
 * returns the EDU nodes
 * 
 * \param node should be a root node
*/
    public List<Node> getedus(Node node){
    	List<Node> eduList		= new ArrayList<Node>();
    	List<Node> tempList		= new ArrayList<Node>();
    	List<Node> dft_nodeList 	= dft(node, tempList);
    	
    	for(Node ii : dft_nodeList){
    		if(ii.lnode == null && ii.rnode == null){
    			eduList.add(ii);
    		}
    	}
    	return eduList;
    }

/** creates bracketing structure [eduspan1, esduspan2, property, relation] for
 * every node in a tree and add them to a list
 * 
 * \param node should be a root node
*/
    public List<List<String>> bracketing(Node node){
        List<Node> tempList		= new ArrayList<Node>();
    	List<Node> dft_nodeList = dft(node, tempList);
    	
        dft_nodeList.remove(dft_nodeList.size()-1); // Remove the root node
        
        List<List<String>> brackets = new ArrayList<List<String>>();
        
        for(Node n : dft_nodeList){
            String relation = n.relation;
            List<String> elementList		= new ArrayList<String>();
        	elementList.add(Integer.toString(n.eduspan[0]) + " " + Integer.toString(n.eduspan[1]));
        	elementList.add(n.prop);
        	elementList.add(relation);
            brackets.add(elementList);
        }
        return brackets;
	}

/** builds the tree and returns the root node
 * 
 * \param filePath 	directory of .rs3 file
*/
	private Node buildtree(String filePath){
		// Variables
		XMLInputFactory factory = XMLInputFactory.newInstance();
		
		// Read rs3 file and generate all nodes
		try {
		XMLStreamReader streamReader = factory.createXMLStreamReader(new FileReader(filePath));
		
		while (streamReader.hasNext()) {
            int event = streamReader.next();

        	if(event == XMLStreamConstants.START_ELEMENT){
				getleaves(streamReader, filePath);	// Generates the leaf nodes
				getnonleaves(streamReader, filePath); // Generates the non leaf nodes
            }
		}
		sentenceID = 1;				// Reset sentenceID

		connectNodes(nodeMap);				// Connects the nodes to a tree
				
		} catch (Exception e) {
            System.err.println(e);
    	}
    	id = 1;								// Reset id
    	return root;
	}
		
/** gets all the leaf nodes from the .rs3 file
 * 
 * \param streamReader stream of the .rs3 file
*/
	private boolean getleaves(XMLStreamReader streamReader, String filePath){
		try {
    		if(streamReader.getLocalName().equals("segment")){
        		int xmlID 		= Integer.parseInt(streamReader.getAttributeValue(null, "id"));
            	String parent 	= streamReader.getAttributeValue(null, "parent");
            	String relation = streamReader.getAttributeValue(null, "relname");
            	String text 	= streamReader.getElementText().trim();
            	// If the node is connected to the tree/ has a parent
            	if(parent != null){
                	int parentID 	= Integer.parseInt(parent);
					Set<Integer> sID = new LinkedHashSet<Integer>(); 
					sID.add(sentenceID);
                	Node node		= new Node(id, xmlID, parentID, relation, text, sID); 
                	node.filepath = filePath;
                	node.eduset();					// Set eduspan, nucspan, nucedu 
                	node.setprop();					// Set property: Nucleus or Satellite
					nodeMap.put(xmlID, node); 		// Save to node in map with its key
					String lastChar = 	text.substring(text.length() - 1); 
					if(lastChar.equals(".") || lastChar.equals("!") || lastChar.equals("?") || lastChar.equals(":") || lastChar.equals("\"") ){
						sentenceID++;
					}
					edusize++;
					id++;
            	}
            	// Else it is a Title node
            	else{
                	int parentID 	= 0;
                	relation = "";
                	Node node		= new Node(id, xmlID, parentID, relation);
                	node.filepath 	= filePath;
            		node.prop = "Title";			// Set property to "Title"
                	nodeMap.put(xmlID, node); 		// Append itself to the map "as a new child"
                	edusize++;
                	id++;
                	}
            	}
            } catch (Exception e) {
            	System.err.println(e);
            }
		return true;
	}
	
/** gets all the non leaf nodes from the .rs3 file
 * 
 * \param streamReader stream of the .rs3 file
*/
	private boolean getnonleaves(XMLStreamReader streamReader, String filePath){
		try {
        	if(streamReader.getLocalName().equals("group")){
        		int xmlID 		= Integer.parseInt(streamReader.getAttributeValue(null, "id"));
            	String parent 	= streamReader.getAttributeValue(null, "parent");
            	String relation = streamReader.getAttributeValue(null, "relname");
            	// If it has a parent
            	if(parent != null){
                	int parentID 	= Integer.parseInt(parent);
                	Node node		= new Node(id, xmlID, parentID, relation);
                	node.filepath = filePath;
                	node.setprop();					// Set property: Nucleus or Satellite
                	nodeMap.put(xmlID, node); 		// Append itself to the map "as a new child"
                	id++;
            	}
           		// Else it is a root node
            	else{
                	int parentID 	= 0;
                	relation 	    = "";
                	Node node	    = new Node(id, xmlID, parentID, relation);
                	node.filepath   = filePath;
                	root 			= node;		
                	node.prop = "Root";				// Set property to "Root"
                	nodeMap.put(xmlID, node); 		// Append itself to the map "as a new child"
                	id++;
            	}
        	}
        } catch (Exception e) {
            System.err.println(e);
        }
        return true;
	}

/** builds binary tree, while connecting the nodes
 * 
 * \param nodeMap key: xmlID value: node
*/
	private boolean connectNodes(TreeMap<Integer,Node> nodeMap){
		for (int key : nodeMap.keySet()) {
    		Node currentNode 	= nodeMap.get(key);
    		Node parentNode  	= nodeMap.get(currentNode.pnodeID);
    		
    		// connect the nodes to the parentID Node of the xml only if they have nuc relations
    		// Some Nodes do not have a parent
    		if(parentNode !=  null && currentNode.isnucleus()){		
    			currentNode.pnode 	= parentNode;    			
    			// If there is no left node, than insert the current node
    			if(parentNode.lnode == null){			
    				parentNode.lnode = currentNode;
    			}
    			else{
    				// If there is a left node but no right node, than insert the current node
    				if(parentNode.rnode == null){
    					parentNode.rnode = currentNode;
    				}
    				else{
    					// If left and right node are already inserted, than make a new node, where the left is
    					// the right node of the current node und the current node is the left node of the new node
    					Node filler 		= new Node(id, currentNode.relation, currentNode.prop);
    					filler.rnode 		= currentNode;
    					filler.lnode 		= parentNode.rnode;
    					filler.fillernode 	= true;
    					
    					parentNode.rnode 	= filler;
    					id++;
    				}
    			}
    		}
    		// If they do not have nuc relation, than search for the next node, with a nuc relation, this will be the parent
    		else{
    			Node tempNode = currentNode;
    			while(parentNode != null && !tempNode.isnucleus()){
    				tempNode = parentNode;
    				parentNode = nodeMap.get(parentNode.pnodeID);
    			}
    			
    			if(parentNode !=  null){
    				currentNode.pnode 	= parentNode;
    				// If there is no left node, than insert the current node
    				if(parentNode.lnode == null){			
    					parentNode.lnode = currentNode;
    				}
    				else{
    					// If there is a left node but no right node, than insert the current node
    					if(parentNode.rnode == null){
    						parentNode.rnode = currentNode;
						}
    					else{
    						// If left and right node are already inserted, than make a new node, where the left is
    						// the right node of the current node und the current node is the left node of the new node
    						Node filler 		= new Node(id, currentNode.relation, currentNode.prop);
    						filler.rnode 		= currentNode;
    						filler.lnode 		= parentNode.rnode;
    						filler.fillernode 	= true;
    					
							parentNode.rnode 	= filler;
							id++;
   						}
    				}
    			}	
    		}
    	}
    	return true;
	}

/** backpropagates all information
 * 
 * \param node should be a root node
*/
	private boolean backprop(Node node){
		List<Node> tree 	= bft(node);
		Collections.reverse(tree);
		for(Node ii : tree){
			// If ii has left and right nodes
        	
			if(ii.lnode != null && ii.rnode != null){
				// Alwas get the eduspan with the smaler number first
				if(ii.lnode.eduspan[0] < ii.rnode.eduspan[0]){
					setinfo(ii, ii.lnode, ii.rnode);
				}
				// Some nodes are switched, because of the xmlID order
				// Building the tree by id is also not efficient (also wrong order)
				else{
					Node temp 	= ii.lnode;
					ii.lnode 	= ii.rnode;
					ii.rnode 	= temp;
					setinfo(ii, ii.lnode, ii.rnode);
				}
			}
			
			// If ii has only a right node
			if(ii.lnode != null && ii.rnode == null){
				ii.eduspan = ii.lnode.eduspan;	// get the eduspan of the left node
				ii.text = ii.lnode.text;		// get the text of the left node
				ii.nucspan = ii.lnode.eduspan;
			}
			
			// If ii has only a left node
			if(ii.lnode == null && ii.rnode != null){
				ii.eduspan = ii.rnode.eduspan;	// get the eduspan of the right node
				ii.text = ii.rnode.text;		// get the text og the right node
				ii.nucspan = ii.rnode.eduspan;
			}
		}
		return true;
	}
	
/** inserts the information to the parent node
 * 
 * \param ii 	current node
 * \param left 	left child node of the current node
 * \param right	right child node of the current node
*/
	private boolean setinfo(Node ii, Node left, Node right){
		ii.eduspan[0] = left.eduspan[0];
		ii.eduspan[1] = right.eduspan[1];

		if(left.prop.equals("Nucleus") && right.prop.equals("Satellite")){
        	ii.nucspan 	= left.eduspan;
        	ii.form 	= "NS";
        }
        
    	if(left.prop.equals("Nucleus") && right.prop.equals("Nucleus")){
        	ii.nucspan[0] = left.eduspan[0];
        	ii.nucspan[1] = right.eduspan[1];
        	ii.form = "NN";
        }
        
        // There are some SSN SNS, but if S is on the left, 
        // there will be always an N somewhere on the right
        if(left.prop.equals("Satellite")){
        	ii.nucspan = right.eduspan;
        	ii.form = "SN";

        }
        
        ii.text = left.text + right.text;
        
        return true;
	}
	
/** decodes Shift-reduce actions from an binary RST tree
 * 
 * \param node should be a root node
*/
	private List<List<String>> decodeSRaction(Node node){
		List<Node> nodeList		= new ArrayList<Node>();
		dft(node, nodeList);
		
		String relation		 			= "";
		List<List<String>> actionList	= new ArrayList<List<String>>();
		
		for(Node ii : nodeList){
			if(ii.lnode == null && ii.rnode == null){
				actionList.add(action("Shift", "Null", "Null"));
			}
			else{
				if(ii.form.equals("NN") || ii.form.equals("NS")){
					relation = ii.rnode.relation;
					actionList.add(action("Reduce", ii.form, relation));
				}
				else{
					if(ii.lnode.prop.equals("Satellite")){
						relation 	= ii.lnode.relation;
						ii.form 	= "SN";
						actionList.add(action("Reduce", ii.form, relation));
					}
				}
			}
		}
		return actionList;
	}
	
/** adds action, form and relation to a list, which will be added to actionList
 * 
 * \param action 	[Shift, null, null] or [Reduce, form, relation]
 * \param form		NN|NS|SN
 * \param relation	Cause|...
*/
	private List<String> action(String action, String form, String relation){
		List<String> elementList		= new ArrayList<String>();
		elementList.add(action);
		elementList.add(form);
		elementList.add(relation);
		
		return elementList;
	}

/** every node gets a List of features
 * one feature is also a list
 * 
 * \param tree should be a root node
*/
	private boolean generate_samples(Node tree){
		//List<List<List<String>>> sampleList = new ArrayList<List<List<String>>>();
		List<List<String>> featureList 		= new ArrayList<List<String>>();
		
		List<Node> stack 				= new ArrayList<Node>();
		List<Node> queue 				= getedus(tree);
		actionList 						= decodeSRaction(tree);	
		int edusize						= queue.size();
		
		SRParser sr = new SRParser(stack, queue, edusize);
		for(List<String> action : actionList){
			
			Features features 		= new Features(sr.stack, sr.queue, edusize);
			featureList 			= features.getfeatures();
			sampleList.add(featureList);
            sr.operate(action);
		}
		return true;
	}
    
/** Post order Deep-First traversal on a binary RST tree
 * 
 * \param node		should start with the rood node
 * \param nodeList 	starts empty, will be full after recursion ends
*/
    protected static List<Node> dft(Node node, List<Node> nodeList){
    	if(node.lnode != null){
    		dft(node.lnode, nodeList);
    	}
    	if(node.rnode != null){
    		dft(node.rnode, nodeList);
    	}
    	nodeList.add(node);
    	return nodeList;
    }
     
/** Breadth-First treavsal on a binary RST tree
 * Generates a List with all nodes 
 * 
 * \param node should be a root node
*/
	protected static List<Node> bft(Node node){
		List<Node> queue 		= new ArrayList<Node>();
		List<Node> bft_nodeList = new ArrayList<Node>();
		
		queue.add(node);
		
		while(queue.size() != 0){
			Node newnode = queue.remove(0);	
			bft_nodeList.add(newnode);   
			if(newnode.lnode != null){
				queue.add(newnode.lnode);
			}
			if(newnode.rnode != null){
				queue.add(newnode.rnode);
			}   
		}
		return bft_nodeList;
	}
	
/** returns the node with the searched xmlID
 * 
 * \param xmlID xmlID of the EDU/node
*/
	@SuppressWarnings("unused")
	private Node getnode(int xmlID){
		return nodeMap.get(xmlID);
	}
	
/// gets current node with its parent  
	@SuppressWarnings("unused")
	private boolean debug(){
		for (int key : nodeMap.keySet()) {
    		Node currentNode 	= nodeMap.get(key);
    		//System.out.println("currID " + currentNode.xmlID + " form " + currentNode.form);
    		if(currentNode.pnode != null){
    			//System.out.println("parentID " + currentNode.pnode.xmlID + " form " + currentNode.pnode.form);
    		}
    		if(currentNode.lnode != null){
    			//System.out.println("left ID " + currentNode.lnode.xmlID + " form " + currentNode.lnode.form);
    		}
    		if(currentNode.rnode != null){
    			//System.out.println("right ID " + currentNode.rnode.xmlID + " form " + currentNode.rnode.form);
    		}
    	}
    	//System.out.println(root.nucspan[0] + " " + root.nucspan[1]);
    	//System.out.println(root.text);
    	
    	//System.out.println(nodeMap.get(32).rnode.xmlID + " " + nodeMap.get(32).rnode.eduspan[0] + " " + nodeMap.get(32).rnode.eduspan[1]);

    	return true;
	}
	
	
} // End of class Tree