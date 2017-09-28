package edu.adapt.tcd.nlp.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import edu.adapt.tcd.nlp.esa.Relatedness;

public class DocumentProcessorRegression{
	TextDocument doc;
	Relatedness relatedness;
	/**
	 *  <b>Integer: The level</b><br>
	 *  <b>TreeMap</b>
	 *  	<ul><li><b>int[]</b>: boundaries as int values, e.g. [0,2,3,5,6,8,10,12]</li>
	 *  	<li><b>String</b>: The level segmentations as String, e.g. "[0]-[1,2]-[3]-[4,5]-[6]-[7,8]-[9,10]-[11,12]"</li> 
	 */
	TreeMap<Integer, TreeMap<int[],String>> levelsBoundaries;
	//{[0,"[0]-[1,2]-[3]-[4,5]-[6]-[7,8]-[9,10]-[11,12]"}
	
	public void change(TextDocument _doc){
		 doc = _doc;
		 relatedness = new Relatedness(doc);
	}
	/**
	 * This method works on the TextDocument object <b>doc</b> to genrate the combinations between clusters.<br>
	 * It gets all the sentences in the document as lists of their tokens (using <b>getDocumentSentencesAsTerms()</b> method)
	 *  and creates a map
	 * of sentences and how they are related to each other (<b>relatedness score</b>). <br><br>
	 * A sentence could be one sentence (at the start of the Hierarchical Agglomerative Clustering algorithm)
	 * or could be a combined sentences (one or more, i.e. a cluster) after the first iteration of the algorithm. <br><br>
	 * The map is in the form <b><i>[relatedness score , index]</i></b> where:<br>
	 * 			<ul><li><b>relatedness score</b>: is the score between the two sentences.</li>	
	 * 			<li><b>index</b>: is the index of a sentence and from it we infere the following sentence (index+1).</li></ul><br>
	 * The reason I put the "relatedness score" first (as the key of the TreeMap) is to make the TreeMap sorted by values.
	 * A step to save time and effort in sorting the tree map.<br><br>----------------------------------------<br><br>
	 * <b>After it finishes</b> it calls the <b>mergeClusters()</b> method:<br>
	 * The <b>mergeClusters</b> method is to <b>merge</b> clusters together. <br>
	 * The idea in that method is that the <b>scoresList</b> parameter passed from the <b><i>processDocument()</i></b> method is a TreeMap<br>
	 * This TreeMap is <b>sorted</b> (by default) by the scores between the clusters. <br><br>
	 * A sample from that TreeMap:<br>  
	 * 		<li><i>{0.0034158555424873317=7, 0.005819545189201961=34,.....}</i></li><br>
	 * <b>7</b> and <b>34</b> are the indexes of the sentences and their following sentences, i.e <b>7</b> 
	 * is the index of sentence 8 and sentence 9 (zero based)<br><br>
	 * Since the TreeMap is sorted by score, this means that the sentences that should be merged together are at the begining of the collection
	 * Based on that, the sentences comes first are merged together as it means that they have high score with each other.<br><br> 
	 * As an example, if the collection has: <b>7</b> in the first position (the score between sentences 7&8)
	 * and later in the TreeMap comes number <b>8</b> (the score between sentences 8&9) .<br><br>
	 * As they are sorted based by score, this means that the score between sentences  7&8 is higher than the score between sentences 8&9 
	 * and hence 7&8 are to be merged together and considered as one new cluster/segment. <br><br>
	 *  <big><b>Note:</b></big><br> The merge happens in another method, this method only puts the sentences in their actual indexes.
	 *  i.e. from the previous example, <b>7</b> (score 7&8) as it comes before <b>8</b> (score 8&9) is converted into 7,8 and added to the new ArrayList in this method<br><br>
	 *  When the method is processing an index (e.g. <b>8</b>) it looks for it in the  <b>combinedSentences</b> ArrayList, 
	 *  if it is there, it means that this number has been encountered before and has been merged with another number, which is <b>7</b>.
	 *  So this number (<b>8</b>) is ignored for now and is not to be added to the <b>combinedSentences</b>.
	 *   
	 * @return ArrayList of arrays, each array has the indexes of the merged clusters. Example: {[0],[1,2],[3],[4,5],...}<br><br>
	 * [1,2] is a cluster resulted from merging 1 with 2 and 0 is not merged with 1 hence it is considered a stand alone cluster.
	 */
	public ArrayList<int []> getCombinations(){
		
		List<ArrayList<String>> list = doc.getSentencesAsTerms();
		Map<Integer,Double> scoresList = new HashMap<Integer,Double> (); 
		for (int i = 0; i<list.size()-1 ;i++ ) { // size() -1 so the loop doesn't go beyond the array
			ArrayList<String> firstSentence = list.get(i);		// 0|1|2|3|4|5|6|7|8|9   .. Example of 10 sentences document
			ArrayList<String> secondSentence = list.get(i+1);	// 1|2|3|4|5|6|7|8|9|-   .. there is no score after 8 with 9
			double rel = relatedness.score(firstSentence, secondSentence);
			//System.out.println(i+"-"+(i+1)+":  "+rel);
			scoresList.put(new Integer(i),new Double(rel));
		}
		/*
		 * After we get all the scores between the given sentences, we need to update the document with
		 * the new segments
		 */
		
		Map<Integer,Double> sortedNewMap = scoresList.entrySet().stream().sorted((e1,e2)->
        e2.getValue().compareTo(e1.getValue()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (e1, e2) -> e1, LinkedHashMap::new));
sortedNewMap.forEach((key,val)->{
   // System.out.println(key+ " = "+ val.toString());
});//sorted(Map.Entry.<Integer,Double>comparingByValue().reversed());
		return mergeClusters(sortedNewMap);
	}
	/**
	 * This method's description is attached with the <b>processDocument()</b> method
	 * @param scoresList TreeMap of the sentences' indexes and the score between them
	 * @return ArrayList of arrays, each array has the indexes of the merged clusters. Example: {[0],[1,2],[3],[4,5],...}<br><br>
	 * [1,2] is a cluster resulted from merging 1 with 2 and 0 is not merged with 1 hence it is considered a stand alone cluster.
	 */
	public ArrayList<int []> mergeClusters(Map<Integer,Double> scoresList){
		ArrayList <Integer> combinedSentences = new ArrayList<Integer>();
		//TreeMap<Integer,Double> newMap = new TreeMap(Collections.reverseOrder());
		//newMap.putAll(scoresList);
		for(Map.Entry<Integer,Double> entry : scoresList.entrySet()) {
			Integer firstSentence= (int) entry.getKey();
			int secondSentence =  firstSentence+1;
			//Double sentencesScore = entry.getKey();
			if(!combinedSentences.contains(firstSentence) && !combinedSentences.contains(secondSentence)){
				combinedSentences.add(firstSentence);
				combinedSentences.add(secondSentence);
			}
		}
		return sortAndMerge(combinedSentences, scoresList.size());// for the last cluster
	}
	
	private ArrayList<int []> sortAndMerge(ArrayList<Integer> sentencesList, int size){
		Collections.sort(sentencesList);
		ArrayList<int []> combined = new ArrayList<int []>(); 
		
		for (int i = 0; i < size; i++) {
			int sentenceNumber;
			
				sentenceNumber = sentencesList.get(i);
				if(sentencesList.get(i)!= i){
					// a stand alone sentence, i.e. not combined with any other sentence
					// to be replaced with -1
					sentencesList.add(i,-1);
				}
		}
		if(!sentencesList.contains(size)){ // if it doesn't have 
			sentencesList.add(-1);
		}
			for (int j = 0; j < sentencesList.size(); j++) {
				if(sentencesList.get(j) == -1){ // stand alone
					int ar [] = {j};
					combined.add(ar);
				}
				else{
					int ar [] = {j,j+1};
					combined.add(ar);
					j++;
				}
			}
			//getBoundaries(combined,1);
		return combined;
	}
	/**
	 * This method is used <b>just</b> in the Evaluation of the segmentation.
	 * @param combined
	 * @return
	 */
	public  int [] getBoundaries(ArrayList<int []> combined){
		int [] oneLevelBoundary  = new int[combined.size()];
		int count = 0;
		for (int[] cluster : combined) {
			oneLevelBoundary [count]= cluster[cluster.length-1];
			count++;
		}
		return	oneLevelBoundary;
	}
	
	private void modifyString(){
		
	}
	/**
	 * Updates the document with the new merged clusters.
	 * If we have <b><i>combinations</i></b> {[0],[1,2],[3],[4,5],...}, this means that 
	 * sentences <b>1</b> and <b>2</b> will be merged together as one segment (cluster) which menas that
	 * their tokens will be will be merged together and will be treated as one sentence.
	 * @param combinations combinations between segments e.g. {[0],[1,2],[3],[4,5],...}
	 */
	public ArrayList<ArrayList<String>> updateDocument(ArrayList<int []> combinations){
		ArrayList<ArrayList<String>> oldSentencesAsTerms =  doc.getSentencesAsTerms();
		ArrayList<ArrayList<String>> newSentencesAsTerms =  new ArrayList<ArrayList<String>>();
		
		for (int [] sentencesArray: combinations) {
			if (sentencesArray.length == 1){
				// leave it as is
				int senteceIndex = sentencesArray[0];
				newSentencesAsTerms.add(oldSentencesAsTerms.get(senteceIndex));
			}
			else{ // two indexes, i.e. two sentences to be merged as one sentence
				int s_1_Index = sentencesArray[0]; // first sentence in the combination
				int s_2_Index = sentencesArray[1]; // second sentence in the combination
				ArrayList<String> s_1_tokens = oldSentencesAsTerms.get(s_1_Index);
				ArrayList<String> s_2_tokens = oldSentencesAsTerms.get(s_2_Index);
				s_1_tokens.addAll(s_2_tokens);
				// note: I can make this in one step, but I prefer to make the code more clear
				newSentencesAsTerms.add(s_1_tokens);
			}
		}
		return newSentencesAsTerms;
	}
	
	<K,V extends Comparable<? super V>> 
    	List<Entry<K, V>> entriesSortedByValues(Map<K,V> map) {

			List<Entry<K,V>> sortedEntries = new ArrayList<Entry<K,V>>(map.entrySet());

			Collections.sort(sortedEntries, 
			new Comparator<Entry<K,V>>() {
				@Override
				public int compare(Entry<K,V> e1, Entry<K,V> e2) {
					return e2.getValue().compareTo(e1.getValue());
				}
			});

		return sortedEntries;
	}
	
	public static void main(String[] args) {
		/*String path = "data/testFolder/ch1_an1.txt";
		TextDocument td = new TextDocument(path);
		DocumentProcessor dp = new DocumentProcessor(td);
		*/
		//dp.getCombinations();
	}
}
