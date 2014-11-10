package de.unikoblenz.west.rdf.partitioning.test;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unikoblenz.west.rdf.partitioning.RdfGraph;
import de.unikoblenz.west.rdf.partitioning.RdfPartitioner;
import de.unikoblenz.west.rdf.partitioning.RdfPartitioningException;
import de.unikoblenz.west.rdf.partitioning.impl.RandomPartitioner;

public class TestRdfPartitioning {

	Logger log = LoggerFactory.getLogger(TestRdfPartitioning.class);
	
	@Before
	public void setUp() {
		BasicConfigurator.configure();;
	}
	
	@Test
	public void testSimplePartitioning() {
		log.info("testing partitioning");
		
		String namespace = "http://www.example.org/";
		ValueFactory fac = ValueFactoryImpl.getInstance();
		Random rand = new Random(0);
		
		log.debug("create URIs; ns=" + namespace); 
		
		List<URI> uris = new LinkedList<URI>();
		
		for(int i = 0; i < 1000; i++) {
			uris.add(fac.createURI(namespace, String.valueOf(i)));
		}
		
		log.debug("create graph");
		
		RdfGraph graph = new RdfGraph();
		
		for(int i = 0; i < 1000; i++) {
			graph.add(fac.createStatement(
					uris.get(rand.nextInt(uris.size())), 
					uris.get(rand.nextInt(uris.size())),
					uris.get(rand.nextInt(uris.size()))
				));
		}

		log.info("graph created: " + graph);
		
		RdfPartitioner partitioner = new RandomPartitioner();
		
		try {
			Set<RdfGraph> partitions = partitioner.partition(graph);
			
			log.info("successful partitioninging: " + partitions);
		} catch (RdfPartitioningException e) {
			log.error("could not partition graph", e);
		} 
		log.info("test done");
	}
}
