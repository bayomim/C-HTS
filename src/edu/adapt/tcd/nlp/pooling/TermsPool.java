package edu.adapt.tcd.nlp.pooling;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import edu.wiki.search.MongoESASearcher;

/**
 * 1/5/2017 @7:35pm
 * @author Mostafa Bayomi
 * This class is to create a pool for all the unique terms in the document.
 * The terms will be passed as array of strings and a Hashmap will be created that will have:
 * 	- term (String)
 *  - idf (float)
 *  - vector (BasicDBObject)
 * When a query is recieved, the ESA will look for the idf and the vector for a term in that HashMap instead 
 * of looking in the database.
 * This will save time and will increase performance.
 */
public class TermsPool{
	MongoClient conn;
	DB db;
	int maxConceptId;
	private ArrayList<String> termList;
	public HashMap<String,PoolTerm> termsHash;
	public TermsPool(){}
	public HashMap<String, PoolTerm> getTermsHash() {
		return termsHash;
	}

	public void setTermsHash(HashMap<String, PoolTerm> termsHash) {
		this.termsHash = termsHash;
	}
	
	public TermsPool(ArrayList<String>  docTerms){
		termList = docTerms;
		termsHash = new HashMap<String,PoolTerm>();
		try {
			initDB();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (String strTerm : termList) {
			getTermFromDB(strTerm);
		}
		System.out.println("All terms are loaded....");
		try {
			finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	public TermsPool(TermsPool t){
		termsHash = new HashMap<String,PoolTerm>(termsHash);
	}
	protected void finalize() throws Throwable {
        conn.close();
        conn = null;
        db = null;
		super.finalize();
	}
	public void initDB() throws ClassNotFoundException, IOException {
		// read DB config
		InputStream is = MongoESASearcher.class.getResourceAsStream("/config/db.conf");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String serverName = br.readLine();
		String databaseName = br.readLine();
		br.close();

		// Create a connection to the database 
		conn = new MongoClient(serverName);
	  	db = conn.getDB(databaseName);
		maxConceptId = (int)db.getCollection("articles").count() + 1;
  }
	
	
	public void clean(){
		synchronized (termList) {
			termList.clear();
		}
		termsHash.clear();
		//conn.close();
	}
	
	/*************
	 * NOOOOOOTE
	 * Need to check what happens if the term is not in the DB?????????
	 * I now know
	 * I don't put it in the TermsPool HashMap
	 * AND when I search for it and it is not in the HashMap I return 0 for idf and null for the vector
	 */
	public void getTermFromDB(String strTerm){
		PoolTerm t = new PoolTerm();
		BasicDBObject query = new BasicDBObject("term", strTerm);
		BasicDBObject fields = new BasicDBObject();
		fields.put("idf",1);
		fields.put("vector",1);
		DBCursor cur = db.getCollection("terms").find(query, fields);
        if(cur.hasNext()){
        	DBObject next = cur.next();
        	ByteArrayInputStream vector = new ByteArrayInputStream((byte[])next.get("vector"));
        	float idf = ((Double)next.get("idf")).floatValue();
        	t.setIdf(idf);
        	t.setVector(vector);
     	   	termsHash.put(strTerm, t);	          	  
        }
       
	}
	
	
	public float getIdf(String term){
		if(termsHash.get(term)== null)
			return 0;
		return termsHash.get(term).getIdf();
	}
	public ByteArrayInputStream getVector(String term){
		if(termsHash.get(term)== null)
			return null;
		return termsHash.get(term).getVector();
	}
}
