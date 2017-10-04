package ie.adapt.tcd.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class MyIO {
	public static HashMap<String, TreeMap<Integer, ArrayList<Integer>>> readSegFile(String filePath){ 
		String myResults;
		HashMap<String, TreeMap<Integer, ArrayList<Integer>>> resultHash = new HashMap<String, TreeMap<Integer, ArrayList<Integer>>>();
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filePath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
		    StringBuilder sb = new StringBuilder();
		    String line="";
			try {
				line = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        try {
					line = br.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		    myResults = sb.toString();
		} finally {
		    try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//========
		String [] results_with_names = myResults.split("========");
		for (String one_result : results_with_names) {
			
		String [] one_f_Results = one_result.trim().split("\n");
		String name = one_f_Results[0].replace(".clean", "");
		String oneFile = one_f_Results[1];
//		for(String oneFile:results){
			//ArrayList<TreeMap<Integer, ArrayList<Integer>>> allResults =  new ArrayList<TreeMap<Integer, ArrayList<Integer>>>();
			String [] oneFileResults = oneFile.split("--"); // get levels
			//System.err.println(oneFileResults);
			TreeMap<Integer, ArrayList<Integer>> oneResult = new TreeMap<Integer, ArrayList<Integer>>();
			int level = 0;
			for(String one:oneFileResults){
				//Integer [] n = new Integer();
				ArrayList<Integer> n = new ArrayList<Integer>();
				String [] oneF = one.split(","); 
				for(String num:oneF){
					n.add(Integer.parseInt(num.trim()));
				}
				oneResult.put(level,n);
				level++;
			}
			//allResults.add(oneResult);
			
//		}
		resultHash.put(name.trim(), oneResult);
		}
		return resultHash;
	}
	public static boolean writeStringAppend(String data, File file) {
		try {
			PrintWriter out = new PrintWriter(new FileWriter(file, true));
			out.print(data);
			out.close();
			return true;
		} catch (IOException e) {
			System.out.println("Problem writing String to disk!");
			e.printStackTrace();
			return false;
		}
	}
}
