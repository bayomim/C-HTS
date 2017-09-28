package edu.adapt.tcd.utils;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class SortCollections {
	public static SortedSet<Map.Entry<Integer, Double>>  entriesSortedByValues(Map<Integer, Double> map) {
        SortedSet<Map.Entry<Integer, Double>> sortedEntries = new TreeSet<Map.Entry<Integer, Double>>(
            new Comparator<Map.Entry<Integer, Double>>() {
                @Override 
                public int compare(Map.Entry<Integer, Double> e1, Map.Entry<Integer, Double> e2) {
                    int res = e1.getValue().compareTo(e2.getValue());
                    return res != 0 ? res : 1; // Special fix to preserve items with equal values
                }
            }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }
}
