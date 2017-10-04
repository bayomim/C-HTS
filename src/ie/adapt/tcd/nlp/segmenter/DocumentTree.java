package ie.adapt.tcd.nlp.segmenter;

import java.util.TreeMap;

public class DocumentTree{
	int[] root;
	TreeMap	<Integer,int[]> children;
	int [] levels;
	public DocumentTree(int [] allDocSentences){
		children = new TreeMap<Integer,int[]>();
		root = allDocSentences;
	}
}
