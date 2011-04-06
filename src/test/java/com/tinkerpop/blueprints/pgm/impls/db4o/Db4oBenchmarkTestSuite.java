/**
 * 
 */
package com.tinkerpop.blueprints.pgm.impls.db4o;

import com.tinkerpop.blueprints.BaseTest;
import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.TestSuite;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.GraphTest;
import com.tinkerpop.blueprints.pgm.util.graphml.GraphMLReader;

/**
 * @author germanviscuso
 *
 */
public class Db4oBenchmarkTestSuite extends TestSuite {
	
	private static final int TOTAL_RUNS = 10;
	
	public Db4oBenchmarkTestSuite() {
		
    }

    public Db4oBenchmarkTestSuite(final GraphTest graphTest) {
        super(graphTest);
    }
    
    public void testDb4oGraph() throws Exception {
        double totalTime = 0.0d;
        Graph graph = graphTest.getGraphInstance();
        GraphMLReader.inputGraph(graph, GraphMLReader.class.getResourceAsStream("graph-example-2.xml"));
        graph.shutdown();

        for (int i = 0; i < TOTAL_RUNS; i++) {
            graph = graphTest.getGraphInstance();
            this.stopWatch();
            int counter = 0;
            for (final Vertex vertex : graph.getVertices()) {
                counter++;
                for (final Edge edge : vertex.getOutEdges()) {
                    counter++;
                    final Vertex vertex2 = edge.getInVertex();
                    counter++;
                    for (final Edge edge2 : vertex2.getOutEdges()) {
                        counter++;
                        final Vertex vertex3 = edge2.getInVertex();
                        counter++;
                        for (final Edge edge3 : vertex3.getOutEdges()) {
                            counter++;
                            edge3.getOutVertex();
                            counter++;
                        }
                    }
                }
            }
            double currentTime = this.stopWatch();
            totalTime = totalTime + currentTime;
            BaseTest.printPerformance(graph.toString(), counter, "Db4oGraph elements touched", currentTime);
            graph.shutdown();
        }
        BaseTest.printPerformance("Db4oGraph", 1, "Db4oGraph experiment average", totalTime / (double) TOTAL_RUNS);
    }

}
