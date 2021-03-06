package ie.adapt.tcd.main;

import java.io.File;
import java.util.regex.Pattern;

import ie.adapt.tcd.iostream.DocsReader;
import ie.adapt.tcd.nlp.segmenter.HAC;
import ie.adapt.tcd.nlp.text.Document;
import ie.adapt.tcd.utils.MyIO;

public class Runner_wiki {
	public static void main(String[] args){
		String [] filesFilter= {"txt"};
		long startTime = System.currentTimeMillis();
		String sBreaker = "\n";
		HAC hac = new HAC();
		DocsReader reader = new DocsReader("data/wiki_data/docs/", filesFilter);
		String resultsFilePath= "data/wiki_data/output/wikiData_with_Wiki_2017.txt";
		File fileToWrite = new File(resultsFilePath);
		
		int filesCount = 0;
		for (String filePath : reader.getFilesList()) {
			filesCount++;
			String currentFileName = getFileName(filePath).trim();
			
			
			
			System.out.println("WORKING ON FILE: "+currentFileName);
			Document doc = new Document(filePath,sBreaker);
			hac.run(doc,2);
			String oneFileString= "";
			int size = hac.getLevelsBoundaries().size();
			int levelsToCount = 0;
			/**
			 *  number of levels we need in the output 
			 */
			int levelsToOutput = 3;
			/**
			 *  The level we want to start from, 1 means starting from the top level 
			 */
			int startFromLevel = 1;
			for (int i = size-startFromLevel; i >=0 ; i--) {
				int [] oneLevelBoundaries = hac.getLevelsBoundaries().get(i);
				String oneBoundaryStr = "";
				for (int j = 0; j < oneLevelBoundaries.length; j++) {
					oneBoundaryStr+=oneLevelBoundaries[j];
					if (j!=oneLevelBoundaries.length-1)oneBoundaryStr+=",";
				}
				if(i>0)
					oneBoundaryStr = "--"+oneBoundaryStr;
				
				oneFileString = oneBoundaryStr+oneFileString;
						
			}
			oneFileString = doc.getFileName().replace(".clean", ".txt")+"\n"+oneFileString ;
			if(filesCount < reader.getFilesList().size()){
				oneFileString+= "\n========\n";
			}
			MyIO.writeStringAppend(oneFileString, fileToWrite);
			hac.cleanHac();
			doc = null;
			
	
		}
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("\n======================\nTotal Time:"+totalTime/1000+"\n======================\n");
		
	}
	public static String getFileName(String filePath){
		//As I am returning a string from the readFile method, I cannot use file.getName(), I have to implement it myself
		String pattern = Pattern.quote(System.getProperty("file.separator"));
		String [] fileNameParts= filePath.split(pattern);
		String fileName = fileNameParts[fileNameParts.length-1];
		return fileName.replace(".clean", "");
		
	}
	

}
