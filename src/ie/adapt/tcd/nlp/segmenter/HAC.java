package ie.adapt.tcd.nlp.segmenter;

import java.util.ArrayList;
import java.util.TreeMap;

import ie.adapt.tcd.nlp.text.DocumentProcessor;
import ie.adapt.tcd.nlp.text.Document;

/**
 * @author: Mostafa Bayomi
 * A class for the Hierarchical Agglomerative Clustering (HAC) algorithm
 **/
public class HAC {
	DocumentProcessor dp;
	Document doc;
	private int numberOfNodes;
	int numOfLevels=0;
	TreeMap<Integer, ArrayList<int []>> levels;
	TreeMap<Integer, int []> levelsBoundaries;
	ArrayList<int []> currentLevelSegments;
	public TreeMap<Integer, int[]> getLevelsBoundaries() {
		return levelsBoundaries;
	}
	int currentLevel = -1; // segmentation level
	public HAC(){
		// for the first call to HAC, a document object will be created
		// After that, this document will be updated with the new merged clusters.
		dp = new DocumentProcessor();
		levels = new TreeMap<Integer, ArrayList<int []>>();
		levelsBoundaries = new TreeMap<Integer, int []>();
	}
	public void cleanHac(){
		currentLevel = -1;
		levels.clear();
		levelsBoundaries.clear();
		currentLevelSegments.clear();
		doc = null;
	}
	
	/*
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
	 */
	
	public void runHac (){
			numOfLevels++;
			currentLevel++; 
			System.out.println("Working on level: "+currentLevel);
			currentLevelSegments = dp.getCombinations();
			levels.put(currentLevel, currentLevelSegments); //no need for it in the segmentation evaluation
			ArrayList<int []> currentLevelUpdatedSegments = currentLevelSegments;
			
			levelsBoundaries.put(currentLevel, dp.getBoundaries(currentLevelUpdatedSegments)); // just in the segmentation evaluation
			// update the document with the new segments
			doc.docSentences = dp.updateDocument(currentLevelSegments);
			
			numberOfNodes = doc.docSentences.size();
		
			
			
	}
	
	public void run(Document _doc, int highLevelNumOfSegments) {
		doc = _doc;
		numberOfNodes = doc.docSentences.size();
		dp.change(doc); // give the Document Processor a new document.
		while(numberOfNodes > highLevelNumOfSegments){
			runHac();
		}
		
	}
}
