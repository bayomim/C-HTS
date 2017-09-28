package edu.adapt.tcd.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import edu.adapt.tcd.iostream.DocsReader;
import edu.adapt.tcd.nlp.segmenter.HAC;
import edu.adapt.tcd.nlp.text.TextDocument;
import util.gen.IO;

public class Runner {
	
	public static void main(String[] args){
		String [] filesFilter= {"txt"};
		String sBreaker = "haps";
		HAC hac = new HAC();
		DocsReader reader = new DocsReader("data/haps_data/ch6", filesFilter);
		String resultsFilePath= "data/haps_data/wiki_2017/output/haps_db2017_allLevels_NO_regression_noIDF_10InTerm.txt";//filePath.replace(".txt", "_result.txt");
		File fileToWrite = new File(resultsFilePath);
		for (String filePath : reader.getFilesList()) {
			System.out.println("WORKING ON FILE: "+filePath);
			TextDocument doc = new TextDocument(filePath,sBreaker);
			hac.run(doc,3);
			String level_string = doc.getFileName()+"\n";
			int size = hac.getLevelsBoundaries().size();
			for (int i = 0; i < size; i++) {
				int [] oneLevelBoundaries = hac.getLevelsBoundaries().get(i);
				for (int j = 0; j < oneLevelBoundaries.length; j++) {
					level_string +=oneLevelBoundaries[j];
					if (j!=oneLevelBoundaries.length-1)level_string +=",";
				}
				if(i<size-1)
					level_string+="--";			
			}
			level_string+="\n";
			IO.writeStringAppend(level_string, fileToWrite);
			hac.cleanHac();
			doc.termsPool.clean();
		}
		
	}

	
	
}
