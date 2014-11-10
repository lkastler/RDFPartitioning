package de.unikoblenz.west.rdf.partitioning.test;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Graph;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.util.RDFLoader;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unikoblenz.west.rdf.partitioning.GraphRdfHandler;
import de.unikoblenz.west.rdf.partitioning.RdfPartitioner;
import de.unikoblenz.west.rdf.partitioning.RdfPartitioningException;
import de.unikoblenz.west.rdf.partitioning.impl.RandomPartitioner;

/**
 * testing RdfPartitioning implementations
 * @author lkastler
 */
public class TestRdfPartitioning {

	Logger log = LoggerFactory.getLogger(TestRdfPartitioning.class);
	
	/**
	 * sets logging up
	 */
	@Before
	public void setUp() {
		BasicConfigurator.configure();
	}
	
	@After
	public void tearDown() {
		BasicConfigurator.resetConfiguration();
	}
	/**
	 * tests RdfPartitioning implementations with a simple test.
	 */
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
		
		Graph graph = new TreeModel();
		
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
			Set<Graph> partitions = partitioner.partition(graph);
			
			log.info("successful partitioninging: " + partitions);
		} catch (RdfPartitioningException e) {
			log.error("could not partition graph", e);
		} 
		log.info("test done");
	}
	
	@Test
	public void testReaderSimplePartitioning() {
		final String fileName = getClass().getResource("/test.ttl").getFile();
		
		log.info("testing partitioning with graph in file: " + fileName);
		
		RDFLoader load = new RDFLoader(null, new ValueFactoryImpl());
		
		RdfPartitioner partitioner = new RandomPartitioner();
		GraphRdfHandler handler = new GraphRdfHandler();
		
		try {
			load.load(new File(fileName), null, RDFFormat.TURTLE, handler);
			Set<Graph> partitions = partitioner.partition(handler.getGraph());
			
			log.info("partitioning successful");
			
			for(Graph g : partitions) {
				log.info(String.valueOf(g.size()));
			}
			
		} catch (RDFParseException | RDFHandlerException | IOException e) {
			log.error("didn't work", e);
		} catch (RdfPartitioningException e) {
			log.error("ERROR",e);
		}
		
		log.info("done");
	}
}
