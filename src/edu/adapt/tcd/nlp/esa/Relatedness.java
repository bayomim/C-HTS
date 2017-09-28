package edu.adapt.tcd.nlp.esa;

import java.io.IOException;
import java.util.ArrayList;

import edu.adapt.tcd.nlp.pooling.TermsPool;
import edu.adapt.tcd.nlp.text.TextDocument;
import edu.wiki.api.concept.IConceptVector;
import edu.wiki.api.concept.scorer.CosineScorer;
import edu.wiki.concept.ConceptVectorSimilarity;
import edu.wiki.search.MongoESASearcher;

public class Relatedness {
	MongoESASearcher esa;
	TermsPool termsPool;
	ConceptVectorSimilarity sim = new ConceptVectorSimilarity(new CosineScorer());
	
	public Relatedness (TextDocument doc){
		termsPool = doc.termsPool;
		try {
			esa = new MongoESASearcher();
		} catch (Exception e) {
			System.err.println("Error in connection to the database...");
			e.printStackTrace();
		}
	}
	
public double score(String sentence1, String sentence2) {
	

	double relatednessScore = 0.0;
	if (sentence1 == null || sentence2 == null) {
		return 0.0;
	}
	
	else {
		IConceptVector c1 = null;
		IConceptVector c2 = null;
		try {
			c1 = esa.getCombinedVector(sentence1);
			c2 = esa.getConceptVector(sentence2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(c1 == null || c2 == null){
			return 0.0;	
		}
		relatednessScore= sim.calcSimilarity(c1, c2);
		
	}
	return relatednessScore;
}
/**
 * A method to be called after the document is done processing
 */
public void clean(){
	esa.clean();
}

}
