package ie.adapt.tcd.nlp.segmenter;

import java.util.ArrayList;
import java.util.TreeMap;

import ie.adapt.tcd.nlp.text.DocumentProcessor;
import ie.adapt.tcd.nlp.text.TextDocument;

/**
 * @author: Mostafa Bayomi
 * A class for the Hierarchical Agglomerative Clustering (HAC) algorithm
 **/
public class HACForTesting {
	DocumentProcessor dp;
	TextDocument doc;
	public int numberOfNodes;
	TreeMap<Integer, ArrayList<int []>> levels;
	TreeMap<Integer, int []> levelsBoundaries;
	int currentLevel = -1; // segmentation level
	public HACForTesting(String filePath, String sentencesBreak){
		// for the first call to HAC, a document object will be created
		// After that, this document will be updated with the new merged clusters.
		/*
		doc = new TextDocument(filePath,sentencesBreak);
		dp = new DocumentProcessor(doc);
		levels = new TreeMap<Integer, ArrayList<int []>>();
		levelsBoundaries = new TreeMap<Integer, int []>();
		numberOfNodes = doc.getSentencesAsTerms().size();
		*/
	}
	public void runHac (){
		numberOfNodes = doc.getSentencesAsTerms().size();
		
			currentLevel++; 
			System.out.println("Working on level: "+currentLevel);
			ArrayList<int []> thisLevelSegments = dp.getCombinations();
			//levels.put(currentLevel, thisLevelSegments); no need for it in the segmentation evaluation
			levelsBoundaries.put(currentLevel, dp.getBoundaries(thisLevelSegments)); // just in the segmentation evaluation
			
			// update the document with the new segments
			doc.setSentencesAsTerms(dp.updateDocument(thisLevelSegments));
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
		
		
		
	}
	//Test
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		HACForTesting hac = new HACForTesting("data/testFolder/wiki_long_file.wiki","\n");
		
		//while(hac.numberOfNodes > 2){
			hac.runHac();
		//}
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Total Time:"+totalTime);
	}
}
