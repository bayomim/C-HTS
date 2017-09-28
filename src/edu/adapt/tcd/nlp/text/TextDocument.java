package edu.adapt.tcd.nlp.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import edu.adapt.tcd.iostream.DocsReader;
import edu.adapt.tcd.nlp.pooling.TermsPool;
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
		documentUniqueTerms =  getDocumentTermsList();
		termsPool = new TermsPool(documentUniqueTerms); 
		// build the document(for the first time) as lists of sentences and each list is a list of terms (tokens)
		makeDocumentSentencesAsTerms();
	}
	public ArrayList<String> getDocumentUniqueTerms() {
		return documentUniqueTerms;
	}
	/**
	 * This method gets the unique terms in the whole document.
	 * The returned terms are stemmed and unique i.e. redundant terms are removed 
	 * They will be used to build the HashMap that will contain these temrs with there related data from the 
	 * database (idf score & vector).
	 * @return ArrayList of the unique terms in the whole document if there is any, null otherwise
	 */
	private ArrayList<String> getDocumentTermsList(){
		ArrayList<String> uniqueTerms = tp.getTerms(documentText);
		if(uniqueTerms.size()>0){
			return clearRedundent(uniqueTerms);
		}
		else
			return null;
	}
	/**
	 * A method to remove the redundant terms in the document termsList
	 * @param termsList the document's terms list
	 * @return the cleaned terms list
	 */
	private ArrayList<String> clearRedundent(ArrayList<String> termsList){
		Set<String> hs = new HashSet<>();
		hs.addAll(termsList);
		termsList.clear();
		termsList.addAll(hs);
		return termsList;
	}
	/**
	 * A method to return the document sentences
	 * @param filePath path to the file
	 * @return ArrayList of all sentences of the file
	 */
	public ArrayList<String> getDocumentSentences(){
		return tp.sentenise(documentText);
	}
	public ArrayList<String> getDocumentSentences(String sentenceBreak){
		String [] lines = documentText.split(sentenceBreak);
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

	/**
	 * This method gets the terms in a given sentence.
	 * The returned terms are stemmed <b>BUT NOT</b> unique. 
	 * @return ArrayList of terms in the given sentence if there is any, null otherwise
	 */
	private ArrayList<String> getASentenceTermsList(String sentence){
		ArrayList<String> sentenceTerms = tp.getTerms(sentence);
		if(sentenceTerms!=null)
			return sentenceTerms;
		else
			sentenceTerms = new ArrayList<String>();
			sentenceTerms.add("");
		
		return sentenceTerms;
	}
	/**
	 * This method retrieves a List of all sentences in the document but as <i>terms</i>
	 * @return List of ArrayLists where each ArrayList represents a sentence. If there no sentences in the document, it returns null 
	 */
	private void makeDocumentSentencesAsTerms(){
		
		ArrayList<String> sentences;
		if(sentencesBreak!=null){
				if(sentencesBreak == "haps")
					sentences = getDocumentSentences(1); // 1 is for the haps experiment
				else
					sentences = getDocumentSentences(sentencesBreak); // 1 is for the haps experiment
			}
		else
			sentences = getDocumentSentences();
		
		if(sentences == null) return; 

		for (String sentence : sentences) {
				ArrayList<String> sentenceTerms = cleanSentence(getASentenceTermsList(sentence));
				if(sentenceTerms == null){
					sentenceTerms = new ArrayList<String>();
					sentenceTerms.add(new String("")); // empty string, yes, but this is to preserve the sentences numbers
				}
				sentencesAsTerms.add(sentenceTerms);
					
		}
	}
	
	private ArrayList<String> cleanSentence(ArrayList<String> sentence){
		ArrayList<String> cleanedSentene = new ArrayList<String> ();
		for (int i = 0; i < sentence.size(); i++) {
			float termIdf = termsPool.getIdf(sentence.get(i));
	    	if(termIdf!=0.0)
	    		cleanedSentene.add(sentence.get(i));
		}
		if(cleanedSentene.size()==0)
			return null;
		
		return cleanedSentene;
	}
	public String getFileName(){
		//As I am returning a string from the readFile method, I cannot use file.getName(), I have to implement it myself
		String pattern = Pattern.quote(System.getProperty("file.separator"));
		String [] fileNameParts= filePath.split(pattern);
		String fileName = fileNameParts[fileNameParts.length-1];
		return fileName;
		
	}

}
