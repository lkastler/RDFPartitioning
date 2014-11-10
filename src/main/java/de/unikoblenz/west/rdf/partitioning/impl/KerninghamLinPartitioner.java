package de.unikoblenz.west.rdf.partitioning.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.openrdf.model.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unikoblenz.west.rdf.generator.RdfGeneratorException;
import de.unikoblenz.west.rdf.generator.model.Edge;
import de.unikoblenz.west.rdf.generator.model.Vertex;
import de.unikoblenz.west.rdf.partitioning.RdfPartitioner;
import de.unikoblenz.west.rdf.partitioning.RdfPartitioningException;
import de.unikoblenz.west.rdf.partitioning.converter.Converter;
import de.unikoblenz.west.rdf.partitioning.converter.ConverterException;

// TODO add doc
public class KerninghamLinPartitioner implements RdfPartitioner {

	// TODO add doc
	public class D implements Comparable<D>{
		private Vertex v;
		private int e;
		private int i;
		
		public D(Vertex v) {
			this.v = v;
			e = 0;
			i = 0;
		}
		
		void incE() {
			e++;
		}
		
		void incI() {
			i++;
		}
		
		void swapEI() {
			int b = e;
			e = i;
			i = b;
		}
		
		Vertex getV() {
			return v;
		}

		@Override
		public int compareTo(D o) {
			return (o.e - o.i) - (e - i);
		}
		
		
	}
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private final int numberOfPartitions = 2;
		
	@Override
	public List<Graph> partition(Graph graph) throws RdfPartitioningException {
		try {
			List<Vertex> vertices = Converter.generateVertexList(graph);
			ArrayList<ArrayList<Vertex>> partitions = new ArrayList<ArrayList<Vertex>>();
			
			// create arraylists
			for(int i = 0; i < numberOfPartitions; i++) {
				partitions.add(new ArrayList<Vertex>());
			}
			
			// generate random distribution to partitions
			for(int i = 0; i < vertices.size(); i++) {
				partitions.get(i % 2).add(vertices.get(i));
			}
			
			// some tweaks to gain memory
			vertices.clear();
			
			// TODO termination reason
					
			// FIXME that is bull, change hashmaps to (v,E,I), create new class with (v,E,I) and swap values, create Comparable<(v,E,I)>
			Vertex v0 = getVertexToSwap(calculateDValues(partitions.get(0), partitions.get(1)));
			Vertex v1 = getVertexToSwap(calculateDValues(partitions.get(1), partitions.get(0)));

			log.debug("swapping " + v0 + " with " + v1);
			
			partitions.get(1).add(v0);
			partitions.get(0).remove(v0);
			
			
			partitions.get(0).add(v1);
			partitions.get(1).remove(v1);
			
			// return result
			LinkedList<Graph> result = new LinkedList<Graph>();
			
			for(List<Vertex> vl : partitions) {
				result.add(Converter.convertVertexSetToGraph(vl));
			}
			
			
			return result;
		} catch (ConverterException e) {
			log.error("FAIL: ", e);
		} catch (RdfGeneratorException e) {
			throw new RdfPartitioningException("could not convert to graph", e);
		}
		
		throw new RdfPartitioningException("something went wrong");
	}

	// TODO add doc
	private Map<Vertex,Integer> calculateDValues(ArrayList<Vertex> l1,
			ArrayList<Vertex> l2) {
		HashMap<Vertex, Integer> d = new HashMap<Vertex, Integer>();
		
		for(Vertex s : l1) {
			d.putIfAbsent(s, 0);
			for(Edge e : s.getNeighbors()) {
				Vertex o = e.getTarget();
				d.putIfAbsent(o, 0);
				
				if(l1.contains(o)) {
					d.put(s, d.get(s) - 1);
					d.put(o, d.get(o) - 1);
				}
				else {
					d.put(s, d.get(s) + 1);
					d.put(o, d.get(o) + 1);
				}
			}
		}
		
		return d;
	}

	private Vertex getVertexToSwap(Map<Vertex, Integer> d) {
		TreeMap<Integer, Vertex> sorted = new TreeMap<Integer, Vertex>();
		for(Entry<Vertex, Integer> e : d.entrySet()) {
			sorted.put(e.getValue(), e.getKey());
		}
		
		return sorted.pollLastEntry().getValue();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RandomVerticesPartitioner []";
	}

}
