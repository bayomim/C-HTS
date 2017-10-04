package ie.adapt.tcd.main;

import java.io.File;

import ie.adapt.tcd.iostream.DocsReader;
import ie.adapt.tcd.nlp.segmenter.HAC;
import ie.adapt.tcd.nlp.text.Document;
import ie.adapt.tcd.utils.MyIO;

public class Runner_haps {
	
	public static void main(String[] args){
		String [] filesFilter= {"txt"};
		String sBreaker = "haps";
		HAC hac = new HAC();
		DocsReader reader = new DocsReader("data/haps_data/chapters/", filesFilter);
		String resultsFilePath= "data/haps_data/output/haps_with_Wiki_2017.txt";
		File fileToWrite = new File(resultsFilePath);
		for (String filePath : reader.getFilesList()) {
			System.out.println("WORKING ON FILE: "+filePath);
			Document doc = new Document(filePath,sBreaker);
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
			MyIO.writeStringAppend(level_string, fileToWrite);
			hac.cleanHac();
			doc.termsPool.clean();
		}
		
	}

	
	
}
