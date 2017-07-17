/*
Original "print(Node root, String title) function":
Copyright 2015 MightyPork 
Licensed under CC Attribution-ShareAlike 2.5 Generic (CC BY-SA 2.5) Licence.

----------------------------------------------------------------------------------------
All functions excluding the "print(Node root, String title) function":
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
//
// USEFULL.VERSION 22.03.17
// further description below
////////////////////////////////////////////////////////////////////////////////

package RST;
import java.util.*;
import java.io.*;
import java.util.regex.Pattern;
import javax.xml.stream.*;

/**
 * Usefull.java is a class with no objects. It hast static functions only.
 * The print function is copied from stackoverflow user MightyPork; a little bit changed.
 *
 * Usage: import RST; Usefull.function();
*/
public class Usefull
{	
/// Private Constructor - so it can't be used
	private Usefull(){
	}

	
/** converts root node to .rs3 file
 *
 * Not possible yet, as you need to know, where the filler nodes are in the binary tree!
 * Note: RST has a totally different parentID! The parentID is more a followsID.
 * Example: RST 			- if node1 is satellite the nucleus node will be the parent
 * 			This Program	- if node1 is satellite the nucleus node will be the sister node
 *			conclusion		- relation will be totally messy
 * 
 * \param node should be the root node
*/	
/*
	public static boolean to_rs3(Node node){
		String[] path 	  = node.filepath.split("/");
		String savePath   = "lala.rs3";
		String twoSpaces  = "  ";
		String fourSpaces = "    ";
		String sixSpaces  = "      ";
		try(
    		BufferedWriter bw 	= new BufferedWriter(new FileWriter(savePath));
    		PrintWriter out 	= new PrintWriter(bw);)
		{
			out.println("<rst>\n" + twoSpaces + "<header> \n" + fourSpaces + "<relations>");
			
			out.println(sixSpaces + "<rel name=\"antithesis\" type=\"rst\" />");
      		out.println(sixSpaces + "<rel name=\"background\" type=\"rst\" />");
      		out.println(sixSpaces + "<rel name=\"circumstance\" type=\"rst\" />");
      		out.println(sixSpaces + "<rel name=\"concession\" type=\"rst\" />");
      		out.println(sixSpaces + "<rel name=\"condition\" type=\"rst\" />");
      		out.println(sixSpaces + "<rel name=\"elaboration\" type=\"rst\" />");
      		out.println(sixSpaces + "<rel name=\"e-elaboration\" type=\"rst\" />");
      		out.println(sixSpaces + "<rel name=\"enablement\" type=\"rst\" />");
      		out.println(sixSpaces + "<rel name=\"evaluation-s\" type=\"rst\" />");
      		out.println(sixSpaces + "<rel name=\"evidence\" type=\"rst\" />");
      		out.println(sixSpaces + "<rel name=\"interpretation\" type=\"rst\" />");
      		out.println(sixSpaces + "<rel name=\"justify\" type=\"rst\" />");
      		out.println(sixSpaces + "<rel name=\"means\" type=\"rst\" />");
      		out.println(sixSpaces + "<rel name=\"motivation\" type=\"rst\" />");
      		out.println(sixSpaces + "<rel name=\"cause\" type=\"rst\" />");
      		out.println(sixSpaces + "<rel name=\"result\" type=\"rst\" />");
      		out.println(sixSpaces + "<rel name=\"otherwise\" type=\"rst\" />");
      		out.println(sixSpaces + "<rel name=\"preparation\" type=\"rst\" />");
      		out.println(sixSpaces + "<rel name=\"purpose\" type=\"rst\" />");
      		out.println(sixSpaces + "<rel name=\"restatement\" type=\"rst\" />");
      		out.println(sixSpaces + "<rel name=\"solutionhood\" type=\"rst\" />");
      		out.println(sixSpaces + "<rel name=\"summary\" type=\"rst\" />");
      		out.println(sixSpaces + "<rel name=\"unconditional\" type=\"rst\" />");
      		out.println(sixSpaces + "<rel name=\"unless\" type=\"rst\" />");
      		out.println(sixSpaces + "<rel name=\"unstated-relation\" type=\"rst\" />");
      		out.println(sixSpaces + "<rel name=\"evaluation-n\" type=\"rst\" />");
      		out.println(sixSpaces + "<rel name=\"reason\" type=\"rst\" />");
      		out.println(sixSpaces + "<rel name=\"\" type=\"rst\" />");
      		out.println(sixSpaces + "<rel name=\"conjunction\" type=\"multinuc\" />");
      		out.println(sixSpaces + "<rel name=\"contrast\" type=\"multinuc\" />");
      		out.println(sixSpaces + "<rel name=\"disjunction\" type=\"multinuc\" />");
      		out.println(sixSpaces + "<rel name=\"joint\" type=\"multinuc\" />");
      		out.println(sixSpaces + "<rel name=\"list\" type=\"multinuc\" />");
      		out.println(sixSpaces + "<rel name=\"restatement-mn\" type=\"multinuc\" />");
      		out.println(sixSpaces + "<rel name=\"sequence\" type=\"multinuc\" />");
			
			out.println(fourSpaces + "</relations> \n" + twoSpaces + "</header> \n" + twoSpaces + "<body>");
			
			List<Node> nodeList 	= new ArrayList<Node>();
			List<String> keepTrack 	= new ArrayList<String>();
			nodeList = Tree.bft(node);
			
			out.println(fourSpaces + "<segment id=\"" + 1 + "\">" + "Titel or something" + "</segment>");
			for(Node n : nodeList){
				// If EDU
				if(n.eduspan[0] == n.eduspan[1]){
					out.println(fourSpaces + "<segment id=\"" + n.id + "\" parent=\"" + n.pnode.id + "\" relname=\"" + n.relation + "\"> " + n.text + "</segment>");
				}
			}
	
			for(Node n : nodeList){
				if(n.eduspan[0] != n.eduspan[1]){
					String type 	= "span";
					
					if(n.isnucleus()){
						type = "multinuc";	
					}
				
					if(n.pnode != null){
						out.println("<group id=\"" + n.id + "\" type=\"" + type + "\" parent=\"" + n.pnode.id + "\" relname=\"" + n.relation + "\" />");
					}
					else{
						out.println("<group id=\"" + n.id + "\" type=\"" + type + "\" />");
					}
				}
			}
			
			out.println(twoSpaces + "</body> \n" + "</rst>");
		} catch (IOException e) {
   			e.printStackTrace();
		}
		
		return true;
	}
*/

/** prints trees into the file tree.txt
 * 
 * \param nodeList	a list containing (root) nodes
*/
public static void print(List<Node> nodeList){
	if(nodeList.size() != 0){
		System.out.println("Start drawing");
		String path = "";
		for(Node n : nodeList){
			if(n != null){
				// Add the EDU number to the text
				String text = "";
				Tree t 				= new Tree();
				List<Node> edulist 	= t.getedus(n);
				for(int ii = 0; ii < edulist.size(); ii++){
					int index = ii + 2; // Skip title and start with 2 
					text = text + " [" + index + "] "  + edulist.get(ii).text + "\n";
					
				}
				
				path = print(n, text);
			}
		}
		System.out.println("Trees saved in: " + path);
		System.out.println("- Drawing Finished - \n");
	}
	else{
		System.err.println("There are no predicted trees. No .edus file found.");
	}
}

// http://stackoverflow.com/questions/4965335/how-to-print-binary-tree-diagram
// MightyPork
// answered Apr 17 '15 at 16:06

/** prints a tree into the file tree.txt
 * 
 * \param node  should be a root node
 * \param title can be any title
*/
	public static String print(Node root, String title){
	
		String[] subDirs = root.filepath.split(Pattern.quote(File.separator));
		String parentFolder = "test"; 
		
		if(subDirs[subDirs.length-2] != null && !subDirs[subDirs.length-2].isEmpty()){
			parentFolder = subDirs[subDirs.length-2];
		}
		
		File theDir1 = new File("./trees/");
		File theDir2 = new File("./trees/" + parentFolder + "/");
		String path  =  "./trees/" + parentFolder + "/";
		// Create directory if not exist
		if(!theDir1.exists()) {
    		try{
        		theDir1.mkdir();
    		} 
    		catch(SecurityException se){
        		System.err.println("Could not create tree Folder for drawing trees");
    		}
    	}   
    	
    	// Create directory if not exist
		if(!theDir2.exists()) {
    		try{
        		theDir2.mkdir();
    		} 
    		catch(SecurityException se){
        		System.err.println("Could not create tree Folder for drawing trees");
    		}
    	}    

    	try(
    		BufferedWriter bw = new BufferedWriter(new FileWriter("./trees/" + parentFolder + "/" + subDirs[subDirs.length-1] + ".txt"));
    		PrintWriter out = new PrintWriter(bw);)
		{
		
    	
    	List<List<String>> lines = new ArrayList<List<String>>();
        List<Node> level 		 = new ArrayList<Node>();
        List<Node> next 		 = new ArrayList<Node>();

        level.add(root);
        int nn 		= 1;
        int widest 	= 0;
        int error	= 0;
		
		out.println(title);
		
		
		
		// print brackets
		Tree t = new Tree();
		List<Node> tree 	= t.bft(root);
		out.println();
		for(Node node : tree){
			if(node.lnode != null && node.rnode != null){
				out.println(node.eduspan[0] + "-" + node.eduspan[1] + ": \t" + 
							node.lnode.eduspan[0] + "-" + node.lnode.eduspan[1] + " " + node.lnode.relation + " " + 
							node.rnode.eduspan[0] +  "-" + node.rnode.eduspan[1] + " " + node.rnode.relation + "\n");
			}
			if(node.lnode != null && node.rnode == null){
				out.println(node.eduspan[0] + "-" + node.eduspan[1] + ": \t" + 
							node.lnode.eduspan[0] + "-" + node.lnode.eduspan[1] + " " + node.lnode.relation + "\n");
			}
		}
		out.println();
		
		
        while (nn != 0){
            List<String> line = new ArrayList<String>();
            nn = 0;
            for(Node n : level){
            	if(error == 5000){
            		out.println("Too big");
            		break;
				}
                if(n == null){
                    line.add(null);
                    next.add(null);
                    next.add(null);
                    error++;
                } 
                else{
                	String aa 	= "";
                	String prop = "";
                	if(n.prop.equals("Nucleus")){
                		prop = ",N ";
                	}
                	else{
                		prop = ",S ";
                	}
                	
                	if(n.eduspan[0] == n.eduspan[1]){
                    	aa = Integer.toString(n.eduspan[0]) + prop;
                    }
                    else{
                    	aa = Integer.toString(n.eduspan[0]) + "-" + Integer.toString(n.eduspan[1]) + prop;
                    }
                    line.add(aa);
                    if (aa.length() > widest){
                    widest = aa.length();}

                    next.add(n.lnode);
                    next.add(n.rnode);

                    if (n.lnode != null){
                   		nn++;
                    }
                    if (n.rnode != null){
                    	nn++;
                    }
                }
            }
            if(widest % 2 == 1){
            	widest++;
            }
            lines.add(line);

            List<Node> tmp = level;
            level = next;
            next = tmp;
            next.clear();
        }
        int perpiece = lines.get(lines.size()-1).size()/2 * (widest + 2);
        for(int i = 0; i < lines.size(); i++){
            List<String> line = lines.get(i);
            int hpw = (int) Math.floor(perpiece / 2f) - 1;

            if (i > 0) {
                for (int j = 0; j < line.size(); j++) {
                    // split node
                    char c = ' ';
                    if(j % 2 == 1){
                        if(line.get(j - 1) != null){
                            c = (line.get(j) != null) ? '┴' : '┘';
                        } 
                        else{
                            if(j < line.size() && line.get(j) != null) c = '└';
                        }
                    }
                    out.print(c);

                    // lines and spaces
                    if (line.get(j) == null) {
                        for (int k = 0; k < perpiece - 1; k++){
                            out.print(" ");
                        }
                    } 
                    else{

                        for (int k = 0; k < hpw; k++) {
                            out.print(j % 2 == 0 ? " " : "─");
                        }
                        out.print(j % 2 == 0 ? "┌" : "┐");
                        for (int k = 0; k < hpw; k++) {
                            out.print(j % 2 == 0 ? "─" : " ");
                        }
                    }
                }
                out.println();
            }

            // print line of numbers
            for (int j = 0; j < line.size(); j++) {

                String f = line.get(j);
                if (f == null) f = "";
                int gap1 = (int) Math.ceil(perpiece / 2f - f.length() / 2f);
                int gap2 = (int) Math.floor(perpiece / 2f - f.length() / 2f);

                // a number
                for (int k = 0; k < gap1; k++) {
                    out.print(" ");
                }
                out.print(f);
                for (int k = 0; k < gap2; k++) {
                    out.print(" ");
                }
            }
            out.println();

            perpiece /= 2;
        }
        } catch (IOException e) {
   				System.err.println(e);
		}
		return path;
    }

/** converts an action to a string label
 * 
 * \param action [Shift, null, null] or [Reduce, form, relation]
*/
	static String action2label(List<String> action){
		String label = "";
		if(action.get(0).equals("Shift")){
			label = action.get(0);
		}
		if(action.get(0).equals("Reduce")){
			label = String.join("-", action);	
		}
		return label;
	}

/** prints nested list
 * 
 * \param dict a nested list
*/
	static boolean printListList(List<List<String>> dict){
		for(List<String> list : dict){
			for(String element : list){
				System.out.print(element + ", ");		
			}
			System.out.println("\n");
		}
		return true;
	}
	
/** builds the full rst tree
 * 
 * \param path directory of .rs3 file
*/
	public static List<Node> buildgold(String path){
		List<Node> goldtrees 	= new ArrayList<Node>();
		File dir 				= new File(path);
		boolean no_rs3file 		= true;
		
  		// for every .rs3 file: build RSTTree
  		for(File file : dir.listFiles()) {
    		if(file.getName().endsWith(".rs3")) {
    			no_rs3file 		= false;
  				Tree rst = new Tree();
  				try {
					rst.build(file.getPath());
					// Add to goldtrees
					goldtrees.add(rst.root);
				} catch (Exception e) {
            		e.printStackTrace();
        		}
 			}
 		}
 		if(no_rs3file){
  			System.err.println("No .rs3 file in the folder: " + path);
  		}
 		return goldtrees;
	}
	
/** serializes an object to a file
 * 
 * \param object usually only vocab and labelIDMap
 * \param path   directory of the file, where the object should be saved
*/
	public static boolean serialize(Object object, String path){
		try {
			FileOutputStream fos 	= new FileOutputStream(path);
        	ObjectOutputStream oos = new ObjectOutputStream(fos);
        	oos.writeObject(object);
        	oos.close();
        	fos.close();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return true;
	}
	
/** generates Files with EDUs (each line one EDU)
 * 
 * \param rstPath directory of the file, where the EDUs should be saved
*/
	static boolean generate_rawdata(String rstPath){
		File dir = new File(rstPath);
  		// for every .rs3 file: build RSTTree
  		for(File file : dir.listFiles()) {
    		if(file.getName().endsWith(".rs3")) {
    			String filename = file.getName();
			
				XMLInputFactory factory = XMLInputFactory.newInstance();
		
				// Read rs3 file and generate all nodes
				try {
					XMLStreamReader streamReader = factory.createXMLStreamReader(new FileReader(rstPath + "/" + filename));
					if (filename.indexOf(".") > 0) {
    					filename = filename.substring(0, filename.lastIndexOf("."));
					}
		
					while (streamReader.hasNext()) {
            		int event = streamReader.next();

        			if(event == XMLStreamConstants.START_ELEMENT){
        				if(streamReader.getLocalName().equals("segment")){
        					String text = streamReader.getElementText().trim();
        					
        					try(BufferedWriter bw = new BufferedWriter(new FileWriter("./data/" + filename + ".edus", true));
    							PrintWriter out = new PrintWriter(bw);){
									out.println(text);
							} catch (IOException e) {
   								e.printStackTrace();
							}

            			}
					}
				
					} 
			} catch (Exception e) {
            	e.printStackTrace();
    		}
			}
		}
	return true;
	}
	
} // End of class Usefull