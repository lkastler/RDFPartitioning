package de.unikoblenz.west.rdf.partitioning.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.openrdf.model.Graph;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.TreeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unikoblenz.west.rdf.partitioning.RdfPartitioner;
import de.unikoblenz.west.rdf.partitioning.RdfPartitioningException;

/**
 * implements RdfPartitioner interface with a random partitioning
 * @author lkastler
 */
public class RandomPartitioner implements RdfPartitioner {

	private Logger log = LoggerFactory.getLogger(getClass());
	private Random rand;
	private int numberOfPartitions;
	
	/**
	 * constructor with properties. 
	 */
	public RandomPartitioner() {
		long seed = 0;
		
		rand = new Random(seed);
		numberOfPartitions = 2;
		
		log.debug("created with seed: " + seed);
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * @see de.unikoblenz.west.lkastler.rdf.partitioning.RdfPartitioner#partition(de.unikoblenz.west.lkastler.rdf.partitioning.RdfGraph)
	 */
	public Set<Graph> partition(Graph graph) throws RdfPartitioningException {
		HashMap<Integer, Graph> partitions = new HashMap<Integer, Graph>(numberOfPartitions);
		
		for(int i = 0; i < numberOfPartitions; i++) {
			partitions.put(i, new TreeModel());
		}
		
		for(Statement t : graph) {
			partitions.get(rand.nextInt(numberOfPartitions)).add(t);
		}
		
		
		return new HashSet<Graph>(partitions.values());
	}

}
