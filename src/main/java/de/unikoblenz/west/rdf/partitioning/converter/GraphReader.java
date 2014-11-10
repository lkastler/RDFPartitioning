package de.unikoblenz.west.rdf.partitioning.converter;

import java.util.HashMap;

import org.openrdf.model.Graph;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO doc
public class GraphReader implements RDFHandler {	

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private boolean locked = false;
	
	private HashMap<String, String> namespaces = new HashMap<String, String>();
	
	private Graph graph = new TreeModel();
			
	@Override
	public void endRDF() throws RDFHandlerException {
		locked = false;
	}

	@Override
	public void handleComment(String comment) throws RDFHandlerException {
		log.debug("comment: " + comment);
	}

	@Override
	public void handleNamespace(String prefix, String uri)
			throws RDFHandlerException {
		namespaces.put(prefix, uri);
	}

	@Override
	public void handleStatement(Statement statement) throws RDFHandlerException {
		graph.add(statement);
	}

	@Override
	public void startRDF() throws RDFHandlerException {
		locked = true;
	}

	/**
	 * @return the graph
	 */
	public Graph getGraph() throws RDFHandlerException {
		if(!locked) {
			return graph;
		}
		throw new RDFHandlerException("still reading");
	}
}
