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

public class DocumentProcessor {
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
	 * This method works on the TextDocument object <b>doc</b> to generate the combinations between clusters.<br>
	 * It gets all the sentences in the document as lists of their tokens (using <code>getDocumentSentencesAsTerms()</code> method)
	 *  and creates a map
	 * of sentences and how they are related to each other (<b>relatedness score</b>). <br><br>
	 * A sentence could be one sentence (at the start of the Hierarchical Agglomerative Clustering algorithm)
	 * or could be a combined sentences (one or more, i.e. a cluster) after the first iteration of the algorithm. <br><br>
	 * The map is in the form <b><i>[index, relatedness score ]</i></b> where:<br>
	 * 			<ul><li><b>index</b>: is the index of a sentence and from it we infer the following sentence (index+1).</li>
	 * 			<li><b>relatedness score</b>: is the score between the two sentences.</li>	</ul><br>
	 * 
	 * <b>After it finishes</b> it calls the <b>mergeClusters()</b> method:<br>
	 * The <b>mergeClusters</b> method is to <b>merge</b> clusters together. <br>
	 * 
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
			scoresList.put(new Integer(i),new Double(rel));
		}
		return mergeClusters(scoresList);
	}
	/**
	 * This method merges clusters and returns them in the format:
	 * <pre> {[0],[1,2],[3],[4,5],...}</pre>
	 * [1,2] is a cluster resulted from merging 1 with 2 and 0 is not merged with 1 hence it is considered a stand alone cluster.<br/>
	 * if 0&1 are merged, this means that 1&2 score is lower than 0&1.<br/>
	 * Hence, 2 will never be merged with 1 in any case.<br/>
	 * So no need to assess 1&2 and 2&3 because whatever the result is 2 will NEVER be 
	 * merged back with 1 (as 1 is already merged with 0 because they have higher relatedness then 1&2)<br/><br/>
	 * 
	 * <b>For example:</b>
	 * <pre>
	 * Sentences: 0      1      2      3      4</pre>
	 * <pre>
	 * Scores:       0.6    0.5    0.4    0.9</pre>
	 * 
	 * At the beginning, 0&1 score will be compared to 1&2 score ->
	 * 	<b>0.6 > 0.5</b><br/>
	 * So, 0 and 1 are merged -> [0,1]	<br/>
	 * 
	 * Then, in the next iteration, we suppose to compare the scores of 1&2 and 2&3 ->
	 * <b>1&2 > 2&3</b><br/>
	 * this means--> merge 1 and 2, <b>BUT</b> this can't happen because 1 is already merged wit 0
	 *  
	 * Hence, we skip this iteration and make i++ so it goes to compare scores of 2&3 and 3&4.<br/>
	 * And in that case, since 2&3 < 3&4, 3 and 4 are merged ([3,4]) and 2 is left as a stand alone cluster ([2])
	 * @param scoresList TreeMap of the sentences' indexes and the score between them
	 * @return ArrayList of arrays, each array has the indexes of the merged clusters.<br/> 
	 * 
	
	 */
	public ArrayList<int []> mergeClusters(Map<Integer,Double> scoresList){
		ArrayList<int []> combinedSentences = new ArrayList<int []>();
		int sIndex = 0; // sentences index
		int scoreWindowStart = 0;
		int scoreWindowEnd = 0;
		for (int i = 0; ;) {
			scoreWindowEnd = scoreWindowStart + 1;
			Double first_second_score = scoresList.get(scoreWindowStart); 		// score between 0,1
			Double second_third_score = scoresList.get(scoreWindowEnd);	// score between 1,2
			int [] oneClusterArray = null;
			
			if(first_second_score == null){ // the last one is left alone
				combinedSentences.add(new int[]{sIndex});
				return combinedSentences;
			}
			else if(second_third_score == null){ // the last two blocks are left alone, so merge them
				combinedSentences.add(new int[]{sIndex,sIndex + 1});
				return combinedSentences;
			}
			else if (first_second_score >= second_third_score){
				// score  0&1 > 1&2
				 int s1 = sIndex++; // first sentence ++ for the second one
                 int s2 = sIndex; // second sentence
				 oneClusterArray = new int[]{s1,s2}; // merge [0,1]
				 scoreWindowStart += 2;
                 sIndex++; 
			}
			else{ 
				// score  0&1 < 1&2
				// make [0] as a cluster, 1 and 2 will be re-assessed in the next iteration
				oneClusterArray = new int[]{sIndex};
				sIndex++;
                scoreWindowStart++;
				
			}
			
			combinedSentences.add(oneClusterArray);
		}
		
		//return combinedSentences;
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
