package edu.adapt.tcd.iostream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;

import edu.adapt.tcd.nlp.text.TextProcessor;

/**
 * 
 * @author Mostafa Bayomi
 * A class to have all input output operations like:
 * 	- Read all files in a folder.
 * 	- Read a single document file and return it as text
 */
public class DocsReader {
	ArrayList<String> filesList;
	
	
	
	public ArrayList<String> getFilesList() {
		return filesList;
	}
	
	public DocsReader(){}
	public DocsReader(String folderPath,String[] filter){
		filesList = getFilesInFolder(new File(folderPath),new FilesFilter(filter));
	}
	/**
	 * 
	 * A method to read all paths of files in a folder and it allows filtering the files.
	 * @author Mostafa Bayomi
	 * @param folderPath the path to the folder that we want to read from -> "path/to/folder"
	 * @param filter files extensions that we need to read, if null, all files in the 
	 * folder will be read -> {"txt"} array of strings.
	 * @return a list of the AbsolutePath to the files in that folder
	 */
	public ArrayList<String> getFilesInFolder(File folderPath,FilesFilter filter){
		File[] filesInFolder = (filter == null)?folderPath.listFiles():folderPath.listFiles(filter);
		ArrayList<String> fl = new ArrayList<String>();
		for (File aFile: filesInFolder){
			fl.add(aFile.getAbsolutePath());
		}
		return fl;
	}
	
	/** @author Mostafa Bayomi
	 * Reads a file and returns its content as a String
	 * @param filePath the absolute file path
	 * @return the file's content as String
	 */
	public static String readFile(String filePath){
		String docAsString="";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
		    StringBuilder sb = new StringBuilder();
		    String line="";
			try {
				line = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        try {
					line = br.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }
		    docAsString = sb.toString();
		} finally {
		    try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return docAsString;
	}
	
	
	/**
	 * Testing this class
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 DocsReader d = new DocsReader();
		 
		ArrayList<String> allFiles = d.navigateFolder(new File("data/testFolder"),new FilesFilter(new String []{"sent"}));
		for (Object filePath: allFiles) {
			d.readFile(filePath.toString());
		}
		*/
	}
}
