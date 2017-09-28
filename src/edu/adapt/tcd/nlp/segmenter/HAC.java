package edu.adapt.tcd.nlp.segmenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import edu.adapt.tcd.nlp.text.DocumentProcessor;
import edu.adapt.tcd.nlp.text.TextDocument;

/**
 * @author: Mostafa Bayomi
 * A class for the Hierarchical Agglomerative Clustering (HAC) algorithm
 **/
public class HAC {
	DocumentProcessor dp;
	TextDocument doc;
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
			if(currentLevel>0){
				currentLevelUpdatedSegments = createNextLevel();
			}
			levelsBoundaries.put(currentLevel, dp.getBoundaries(currentLevelUpdatedSegments)); // just in the segmentation evaluation
			// update the document with the new segments
			doc.setSentencesAsTerms(dp.updateDocument(currentLevelSegments));
			
			numberOfNodes = doc.getSentencesAsTerms().size();
		
			
			
	}
	
	public void run(TextDocument _doc, int highLevelNumOfSegments) {
		doc = _doc;
		numberOfNodes = doc.getSentencesAsTerms().size();
		dp.change(doc); // give the Document Processor a new document.
		while(numberOfNodes > highLevelNumOfSegments){
			runHac();
		}
		/*
		for (int i = 0; i < levelsBoundaries.size(); i++) {
			System.out.println("==Level "+i+"==");
			int[] t = levelsBoundaries.get(i);
			for (int j : t) {
				System.out.println(j);
			}
			System.out.println("\n--------------------------------\n");
		}
		*/
		//long endTime   = System.currentTimeMillis();
		//long totalTime = endTime - startTime;
		//System.out.println("Total Time:"+totalTime);
	}
	private void getBoundaries(){
		for (int i = levels.size()-1; i >= 0 ; i--) {
			ArrayList<int[]> level = levels.get(i);
			//ArrayList<E> x = new ArrayList<>();
		}
	}
	private ArrayList<int[]> createNextLevel(){
		int prevLevel = currentLevel-1; // starting from the second level
		ArrayList<int[]> prevLevelList = levels.get(prevLevel);
		ArrayList<int[]> currentLevelUpdatedList = new ArrayList<int[]> (); // the new level segments 
		for (int i = 0; i < currentLevelSegments.size() ; i++) {
			int[] segment = currentLevelSegments.get(i);
			ArrayList<Integer>newActualSegment = new ArrayList<Integer>(); 
			for (int j = 0; j < segment.length; j++) { // I have to do all of that because Java SUCKS, it cann't add two arrays together
				int sentenceNumber = segment[j]; 
				int actualSN []= prevLevelList.get(sentenceNumber);
				for (int v = 0; v < actualSN.length; v++) {
					newActualSegment.add(actualSN[v]);
				}
				
			}
			int [] ar = new int[newActualSegment.size()];
			for (int k = 0; k <newActualSegment.size(); k++) {
				ar[k] = newActualSegment.get(k);
			}
			currentLevelUpdatedList.add(ar);
		}
		
		levels.put(currentLevel, currentLevelUpdatedList);
		return currentLevelUpdatedList;
	}
}
