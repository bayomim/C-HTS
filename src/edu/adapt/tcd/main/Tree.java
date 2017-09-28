package edu.adapt.tcd.main;


/**
 * A class to contain the tree resulted from the <code>C-HTS</code> algorithm.<br/>
 * 
 * Using the measure proposed by Wu and Palmer
 *  
 * measure (s1,s2) = 2*N/(N1+N2)
 * 
 * @author Mostafa Bayomi
 *
 * 
 */
public class Tree {
    private Node root;
    int numOfLevels = 4;// number of levels in the tree after the initial level that has all the nodes
    public Tree(Node _root) {
        root = _root;
        root.parent = null; // Root doesn't have parent
    }
    private int getNForNode(Node node){
    	int distance = 0;
    	while(node.parent!=null){
    		distance++;
    		node = node.parent;
    	}
    	return distance;
    }
    private int getNForCommonAncestor(Node node1,Node node2){
    	while(node1.parent!=null && !node1.parent.data.equals(node2.parent.data)){
    		node1 = node1.parent;
    		node2 = node2.parent;
    	}
    	//return getNForNode(node1.parent);
    	 return (numOfLevels - node1.parent.level); 
    }
    /**
     * A method to measure the distance between two sentences in the tree using
     *  Wu & Palmer equation:
     * <pre>measure(s1,s2) = 2*N/(N1+N2)</pre>
     * where N is the distance between the closest common
     * ancestor (CS) of s1 and s2 and the taxonomy root, and N1 and
     * N2 are the distances between the taxonomy root on one hand
     * and s1 and s2 on the other hand respectively.
     * @return
     */
    public double wuAndPalmerMeasure(Node s1, Node s2){
    	double measure = 0.0D;
    	int N = getNForCommonAncestor(s1,s2);
    	measure = (double)2*N/(double)(2*numOfLevels);
    	//int N1,N2;
    	//N1 = N2 = numOfLevels;//getNForNode(s1);
    	//int N2 = getNForNode(s2);
    	
    	//measure = (double)2*N/(double)(N1+N2);
    	return measure;
    }
    public static void main(String[] args) {
    	
		// level 0
		Node s00 = new Node("0",0);	
		Node s10 = new Node("1",0);
		Node s20 = new Node("2",0);
		Node s30 = new Node("3",0);
		Node s40 = new Node("4",0);
		Node s50 = new Node("5",0);
		Node s60 = new Node("6",0);
		Node s70 = new Node("7",0);
		// level 1
		Node s01 = new Node("0",1);
		Node s11 = new Node("1,2",1);
		Node s21 = new Node("3",1);
		Node s31 = new Node("4,5",1);
		Node s41 = new Node("6,7",1);
		// level 2
		Node s02 = new Node("0,1,2",2);
		Node s12 = new Node("3",2);
		Node s22 = new Node("4,5,6,7",2);
		// level 3
		Node s03 = new Node("0,1,2,3",3);
		Node s13 = new Node("4,5,6,7",3);
		// ROOT (level 4)
		Node root = new Node("0,1,2,3,4,5,6,7",4);
		Tree myTree = new Tree(root); 
		
		
		s00.parent = s01;
		s10.parent = s20.parent = s11;
		s30.parent = s21;
		s40.parent = s50.parent = s31;
		s60.parent = s70.parent = s41;
		
		s01.parent = s11.parent = s02;
		s21.parent = s12;
		s31.parent = s41.parent = s22;
		
		s02.parent = s12.parent = s03;
		s22.parent = s13;
		
		s03.parent = s13.parent = root;
		
		System.out.println(myTree.wuAndPalmerMeasure(s20, s30));
		
		
	}
    public static class Node {
        String data;
        public int level;
        Node parent = null;  
        public Node(String _data, int _level){
        	level = _level;
        	data = _data+"-"+level;
        	
        }
    }
}