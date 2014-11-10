package de.unikoblenz.west.rdf.partitioning;

import java.util.List;

import org.openrdf.model.Graph;

/**
 * supplies methods to partition a given RDF graph.
 * @author lkastler
 */
public interface RdfPartitioner {

	/**
	 * partitions a given graph to a set of RDF graphs (partitions).
	 * @param graph - graph to partition.
	 * @return set of RDF graphs representing partitions of given RDF graph.
	 * @throws RdfPartitioningException
	 */
	public List<Graph> partition(Graph graph) throws RdfPartitioningException;
}
