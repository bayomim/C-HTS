package ie.adapt.tcd.nlp.pooling;

import java.util.HashMap;
import java.util.Map;

public class PoolHelper {
	public static HashMap<String,PoolTerm> copyTermsHash(TermsPool original){
		HashMap<String, PoolTerm> temp = original.getTermsHash() ;
		HashMap<String, PoolTerm> compyHash = new HashMap<String,PoolTerm>();
		for (Map.Entry<String, PoolTerm> entry : temp.entrySet()) {
			compyHash.put(entry.getKey(), entry.getValue());
		}
		return compyHash;
	}
}
