package edu.adapt.tcd.nlp.segmenter;

import java.sql.Time;
import java.util.ArrayList;
import java.util.TreeMap;

import edu.adapt.tcd.nlp.text.DocumentProcessor;
import edu.adapt.tcd.nlp.text.TextDocument;

/**
 * @author: Mostafa Bayomi
 * A class for the Hierarchical Agglomerative Clustering (HAC) algorithm
 **/
public class HAC2 {
	DocumentProcessor dp;
	TextDocument doc;
	TreeMap<Integer, ArrayList<int []>> levels;
	TreeMap<Integer, int []> levelsBoundaries;
	int currentLevel = -1; // segmentation level
	public HAC2(String filePath, String sentencesBreak){
		// for the first call to HAC, a document object will be created
		// After that, this document will be updated with the new merged clusters.
		/*doc = new TextDocument(filePath,sentencesBreak);
		dp = new DocumentProcessor(doc);
		levels = new TreeMap<Integer, ArrayList<int []>>();
		levelsBoundaries = new TreeMap<Integer, int []>();
		*/
	}
	public void runHac (){
		int numberOfNodes = doc.getSentencesAsTerms().size();
		if(numberOfNodes > 2){// iterate until reaches the top of the tree
			currentLevel++; 
			System.out.println("Working on level: "+currentLevel);
			ArrayList<int []> thisLevelSegments = dp.getCombinations();
			//levels.put(currentLevel, thisLevelSegments); no need for it in the segmentation evaluation
			levelsBoundaries.put(currentLevel, dp.getBoundaries(thisLevelSegments)); // just in the segmentation evaluation
			
			// update the document with the new segments
			doc.setSentencesAsTerms(dp.updateDocument(thisLevelSegments));
			runHac();
		}
		else{
			System.out.println("\n\n======================\n\nDone\n\n======================\n\n");
			System.err.println(levelsBoundaries);
		}
		
		
		for (int i = 0; i < levelsBoundaries.size(); i++) {
			System.out.println("==Level "+i+"==");
			int[] t = levelsBoundaries.get(i);
			for (int j : t) {
				System.out.println(j);
			}
			System.out.println("\n--------------------------------\n");
		}
		
		
		
	}
	//Test
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		HAC2 hac = new HAC2("data/testFolder/wiki_long_file.wiki","\n");
		hac.runHac();
		
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Total Time:"+totalTime);
	}
}
