package de.unikoblenz.west.rdf.partitioning;

import java.util.Set;

/**
 * supplies methods to partition a given RDF graph.
 * @author lkastler
 */
public interface RdfPartitioner {

	/**
	 * partitions a given RdfGraph to a set of Rdf graphs (partitions).
	 * @param graph
	 * @return
	 * @throws RdfPartitioningException
	 */
	public Set<RdfGraph> partition(RdfGraph graph) throws RdfPartitioningException;
}
