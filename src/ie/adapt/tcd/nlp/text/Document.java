package ie.adapt.tcd.nlp.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import ie.adapt.tcd.iostream.DocsReader;
/**
 * 
 * @author Mostafa Bayomi
 * This class is to hold the document that is being processed. 
 * The class has different methods to help in getting the required fields in the document to be used in processing that document.
 * It has functionalities to retrieve:<br><ul>
 * 	- The document's text,
 * 	- document's unique terms
 * 	- sentences as strings
 * 	- sentences as tokens
 * 	- tokens of each sentence
 */

public class Document {
	private String documentText="";
	public ArrayList<String> docSentences;
	private TextProcessor tp;
	String sentencesBreak = null;
	private String filePath="";
	
	
	public Document(String filePath, String sBreak){
		this.filePath = filePath;
		sentencesBreak = sBreak;
		documentText = DocsReader.readFile(filePath);
		
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
