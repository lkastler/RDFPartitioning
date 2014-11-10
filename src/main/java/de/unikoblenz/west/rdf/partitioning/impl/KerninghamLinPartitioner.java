package de.unikoblenz.west.rdf.partitioning.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

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

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof D) {
				return v.equals(((D) obj).v);
			}
			return false;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "D [v=" + v + ", e=" + e + ", i=" + i + "]";
		}

	}
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private final int THRESHOLD = 5;
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
			
			
			HashMap<Vertex, D> vd0 = new HashMap<Vertex, D>();
			
			for(Vertex v : partitions.get(0)) {
				vd0.put(v, new D(v));
			}
			
			HashMap<Vertex, D> vd1 = new HashMap<Vertex, D>();
			
			for(Vertex v : partitions.get(1)) {
				vd1.put(v, new D(v));
			}
			
			for(Vertex s : partitions.get(0)) {
				for(Edge e: s.getNeighbors()) {
					Vertex o = e.getTarget();
					
					if(partitions.get(0).contains(o)) {
						vd0.get(s).incI();
						vd0.get(o).incI();
					}
					else {
						vd0.get(s).incE();
						vd1.get(o).incE();
					}
				}
			}
			
			for(Vertex s : partitions.get(1)) {
				for(Edge e: s.getNeighbors()) {
					Vertex o = e.getTarget();
					
					if(partitions.get(1).contains(o)) {
						vd1.get(s).incI();
						vd1.get(o).incI();
					}
				}
			}
			
			log.debug(vd0.toString());
			log.debug(vd1.toString());
			
			// moving to tree sets + freeing memory
			TreeSet<D> ts0 = new TreeSet<D>(vd0.values());
			vd0.clear();
			
			TreeSet<D> ts1 = new TreeSet<D>(vd1.values());
			vd1.clear();
			
			// TODO termination reason
			while((ts0.last().e - ts0.last().i) > THRESHOLD || (ts1.last().e - ts1.last().i) > THRESHOLD) {
				D d0 = ts0.pollLast();
				D d1 = ts1.pollLast();
	
				log.debug("swapping " + d0 + " with " + d1);
				
				partitions.get(1).add(d0.getV());
				partitions.get(0).remove(d0.getV());
				d0.swapEI();
				ts1.add(d0);
				
				partitions.get(0).add(d1.getV());
				partitions.get(1).remove(d1.getV());
				d1.swapEI();
				ts0.add(d1);
			}
			
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


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RandomVerticesPartitioner []";
	}

}
