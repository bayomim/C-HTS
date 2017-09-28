package edu.adapt.tcd.nlp.text;

public class SentencesRelations {
	private int s1Index, s2Index;
	private double relatedness;
	public int getS1Index() {
		return s1Index;
	}
	public void setS1Index(int s1Index) {
		this.s1Index = s1Index;
	}
	public int getS2Index() {
		return s2Index;
	}
	public void setS2Index(int s2Index) {
		this.s2Index = s2Index;
	}
	public double getRelatedness() {
		return relatedness;
	}
	public void setRelatedness(double relatedness) {
		this.relatedness = relatedness;
	}
}
