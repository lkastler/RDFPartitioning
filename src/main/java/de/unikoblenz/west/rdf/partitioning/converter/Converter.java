package de.unikoblenz.west.rdf.partitioning.converter;

import java.util.ArrayList;
import java.util.List;
import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.TreeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unikoblenz.west.rdf.generator.EdgeGenerationException;
import de.unikoblenz.west.rdf.generator.RdfGeneratorException;
import de.unikoblenz.west.rdf.generator.model.Edge;
import de.unikoblenz.west.rdf.generator.model.Vertex;

// TODO add doc
abstract public class Converter {

	public static final Logger log = LoggerFactory.getLogger(Converter.class);

	public static List<Vertex> generateVertexList(Graph graph)
			throws ConverterException {
		log.info("generating vertices list");
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();

		for (Statement s : graph) {
			Vertex subj = new Vertex((URI) s.getSubject());
			URI pred = s.getPredicate();
			Vertex obj = new Vertex((URI) s.getObject());

			if (!vertices.contains(subj)) {
				vertices.add(subj);
			}

			if (!vertices.contains(obj)) {
				vertices.add(obj);
			}

			try {
				vertices.get(vertices.indexOf(subj)).addEdge(pred, obj);
			} catch (EdgeGenerationException e) {
				log.error("FAIL", e);
			}
		}
		log.info("done");

		return vertices;
	}

	// add doc
	public static Graph convertVertexSetToGraph(List<Vertex> set)
			throws RdfGeneratorException {
		Graph graph = new TreeModel();

		for (Vertex v : set) {
			for (Edge e : v.getNeighbors()) {
				graph.add(v.getUri(), e.getLabel(), e.getTarget().getUri(),
						new Resource[0]);
			}
		}
		return graph;
	}
}
