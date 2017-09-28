package ie.adapt.tcd.test;

import java.io.IOException;
import java.util.ArrayList;

import edu.adapt.tcd.nlp.esa.Relatedness;

public class Tester {

	public static void main(String[] args) {
		//String doc1 = args[0];// Barack Obama 
		//String doc2 = args[1];//George W. Bush
		String doc1 = "Obama is the president"; 
		String doc2 = "Obama is the president";
		ArrayList<String> docTerms = new ArrayList<String>();
		docTerms.add("obama");
		docTerms.add("presid");
		docTerms.add("unit");
		docTerms.add("unite");
		docTerms.add("state");
		docTerms.add("george");
		docTerms.add("bush");
		docTerms.add("live");
		docTerms.add("usa");
		//Relatedness r = new Relatedness(docTerms);
		double relatedness=0.0;
		/*try {
			relatedness = r.score(doc1, doc2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(relatedness);
		*/
	}
}
