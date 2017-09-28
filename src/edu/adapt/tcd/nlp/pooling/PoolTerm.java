package edu.adapt.tcd.nlp.pooling;

import java.io.ByteArrayInputStream;

public class PoolTerm {
	public float idf;
	private ByteArrayInputStream vector;
	public float getIdf() {
		return idf;
	}
	public void setIdf(float idf) {
		this.idf = idf;
		//this.idf = (float)0.8;
	}
	public ByteArrayInputStream getVector() {
		return vector;
	}
	public void setVector(ByteArrayInputStream vectore) {
		this.vector = vectore;
	}
	
}
