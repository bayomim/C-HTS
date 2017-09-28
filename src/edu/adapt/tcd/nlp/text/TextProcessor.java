package edu.adapt.tcd.nlp.text;

import java.io.IOException;
import java.io.StringReader;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import edu.wiki.index.WikipediaAnalyzer;

public class TextProcessor {
	WikipediaAnalyzer analyzer;
	TokenStream ts;
	public TextProcessor () throws IOException{
		analyzer = new WikipediaAnalyzer();
	}
	/**
	 * A method to break a document into sentences. 
	 * There are different libraries that could be used to split text into sentences like Stanford CorNLP
	 * @param document document's text
	 * @return ArrayList of sentences in the document if there are sentences, null otherwise 
	 */
	public ArrayList <String> sentenise (String document){
        BreakIterator boundary = BreakIterator.getSentenceInstance(Locale.US);
        ArrayList <String> sentences = new ArrayList <String> ();  
         boundary.setText(document);
         int start = boundary.first();
	     for (int end = boundary.next();
	          end != BreakIterator.DONE;
	          start = end, end = boundary.next()) {
	    	 sentences.add(document.substring(start,end));
	     }
	     sentences.removeAll(Arrays.asList("",null));// to remove empty sentences, just in case
	     if(sentences.size()==0)
	    	 return null;
	     
	     return sentences;
	} 
	/**
	 * This method tokenise text, stems tokens and removes stopwords using Lucene Analysis. 
	 * @param text The text we want to clean (tokenise, stem and remove stopwords from)
	 * @return ArrayList with all terms in the given text if there is any, null otherwise
	 */
	public ArrayList<String> getTerms(String text){
		
		if(text.length()<1) return null;

		ArrayList<String> terms = new ArrayList<String>();
		ts = analyzer.tokenStream("contents",new StringReader(text));
		CharTermAttribute charTermAttribute = ts.getAttribute(CharTermAttribute.class);
		try {
			ts.reset();
			 while (ts.incrementToken()) { 
				    String term = charTermAttribute.toString();
				    terms.add(term);
			 }
			ts.end();
	        ts.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(terms.size() == 0) 
			return null;
		
		return terms;
	}
}
