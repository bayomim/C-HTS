package ie.adapt.tcd.nlp.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import edu.adapt.tcd.iostream.DocsReader;
import ie.adapt.tcd.nlp.pooling.TermsPool;
/**
 * 
 * @author Mostafa Bayomi
 * This class is to hold a text document that is being processed. 
 * The class has different methods to help in getting the required fields in the document to be used in processing that document.
 * It has functionalities to retrieve:<br><ul>
 * 	- The document's text,
 * 	- document's unique terms
 * 	- sentences as strings
 * 	- sentences as tokens
 * 	- tokens of each sentence
 */

public class TextDocument {
	private String documentText="";
	public TermsPool termsPool;
	ArrayList<String> docSentences;
	private ArrayList<String> documentUniqueTerms;
	/**
	 * How a document tree looks like:
	 * {[level,[[segment1],segment2],...],..}
	 * {[
	 * [
	 * 		0,
	 * 			[[0],[1,2],[3],[4,5],[6,7],[8,9],[10]]
	 * 	],
	 * 	[	1,
	 * 			[[0,1,2],[3],[4,5,6,7],[8,9,10]]
	 * 	]
	 * ]}
	 **/
	//private TreeMap <Integer, TreeMap<Integer,int[]>>tree;
	private TextProcessor tp;
	String sentencesBreak = null;
	private ArrayList<ArrayList<String>> sentencesAsTerms;
	private String filePath="";
	public ArrayList<ArrayList<String>> getSentencesAsTerms() {
		return sentencesAsTerms;
		
	}
	public void setSentencesAsTerms(ArrayList<ArrayList<String>> sentencesAsTerms) {
		this.sentencesAsTerms = sentencesAsTerms;
	}
	public TextDocument(String filePath, String sBreak){
		termsPool = null;
		this.filePath = filePath;
		//tree = new TreeMap <Integer, TreeMap<Integer,int[]>>();
		sentencesBreak = sBreak;
		documentText = DocsReader.readFile(filePath);
		
		sentencesAsTerms = new ArrayList<ArrayList<String>>();
		try {
			tp = new TextProcessor();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	/**
	 * A method to return the document sentences
	 * @return ArrayList of all sentences of the file
	 */
	public ArrayList<String> getDocumentSentences(){
		String [] lines = documentText.split(sentencesBreak);
		return  new ArrayList<String>(Arrays.asList(lines));
	}
	
	public ArrayList<String> getDocumentSentences(int haps){ // this splitter is just for haps dataset
		documentText = documentText.trim(); // to add the last segment of the text
		//documentText +="\n";
		String sentenceBreak = "\n";
		String [] lines = documentText.split(sentenceBreak);
		boolean newSegment = false;
		ArrayList<String> returned = new ArrayList<String>();
		String segment ="";
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i].trim();
			if(!line.equals("")){
				newSegment = true;
				segment+=lines[i];
				if(i==lines.length-1)// for the last segment
					returned.add(segment);
			}
			else{
				if(newSegment){					
					returned.add(segment);
					segment = "";
					newSegment = false;
				}
			}
		}
		return  returned;
	}


	public String getFileName(){
		//As I am returning a string from the readFile method, I cannot use file.getName(), I have to implement it myself
		String pattern = Pattern.quote(System.getProperty("file.separator"));
		String [] fileNameParts= filePath.split(pattern);
		String fileName = fileNameParts[fileNameParts.length-1];
		return fileName;
		
	}

}
