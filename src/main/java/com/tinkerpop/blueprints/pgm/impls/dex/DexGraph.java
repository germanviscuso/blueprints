/**
 *
 */
package com.tinkerpop.blueprints.pgm.impls.dex;

import com.tinkerpop.blueprints.pgm.*;
import com.tinkerpop.blueprints.pgm.impls.dex.util.DexAttributes;
import com.tinkerpop.blueprints.pgm.impls.dex.util.DexTypes;
import edu.upc.dama.dex.core.DEX;
import edu.upc.dama.dex.core.Objects;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * {@link Graph} implementation for DEX.
 *
 * @author <a href="http://www.sparsity-technologies.com">Sparsity
 *         Technologies</a>
 */
public class DexGraph implements IndexableGraph {

    /**
     * Default Vertex label.
     * <p/>
     * Just used when invoked addVertex with a null parameter.
     */
    public static final String DEFAULT_DEX_VERTEX_LABEL = "?DEFAULT_DEX_VERTEX_LABEL?";

    /**
     * Vertex label used at {@link #addVertex(Object)} method.
     */
    public static String LABEL = DEFAULT_DEX_VERTEX_LABEL;

    /**
     * Database persistent file.
     */
    private File db = null;

    private edu.upc.dama.dex.core.DEX dex = null;
    private edu.upc.dama.dex.core.GraphPool gpool = null;
    private edu.upc.dama.dex.core.Session session = null;
    private edu.upc.dama.dex.core.DbGraph graph = null;

    /**
     * Gets the DEX raw graph.
     *
     * @return DEX raw graph.
     */
    edu.upc.dama.dex.core.Graph getRawGraph() {
        return graph;
    }

    /**
     * All collections are registered here to be automatically closed when the
     * database is stopped (at {@link #shutdown()}).
     */
    private List<DexIterable<? extends Element>> collections = new ArrayList<DexIterable<? extends Element>>();

    /**
     * Registers a collection.
     *
     * @param col Collection to be registered.
     */
    void register(DexIterable<? extends Element> col) {
        collections.add(col);
    }

    /**
     * Unregisters a collection.
     *
     * @param col Collection to be unregistered
     */
    void unregister(DexIterable<? extends Element> col) {
        collections.remove(col);
    }

    /**
     * Creates a new instance.
     *
     * @param db     Database persistent file.
     * @param create Create iff <code>true</code>, open otherwise.
     * @throws FileNotFoundException Given file is not found.
     */
    public DexGraph(File db, boolean create) throws FileNotFoundException {
        this.db = db;
        DEX.Config cfg = new DEX.Config();
        cfg.setCacheMaxSize(0); // use as much memory as possible
        dex = new DEX(cfg);
        gpool = (create ? dex.create(db) : dex.open(db));
        session = gpool.newSession();
        graph = session.getDbGraph();
    }

    /**
     * Adds a Vertex or retrieves an existing one.
     * <p/>
     * Since all {@link DexVertex} instances are labeled, the static field
     * {@link #LABEL} sets the label of the vertex to be created. If this is
     * null the {@link #DEFAULT_DEX_VERTEX_LABEL} is used.
     *
     * @param id In case this is an instance of Long, then it corresponds to
     *           the identifier of the instance to be retrieved. Otherwise, it
     *           is ignored.
     * @return Added or retrieved Vertex.
     * @see com.tinkerpop.blueprints.pgm.Graph#addVertex(java.lang.Object)
     */
    @Override
    public Vertex addVertex(Object id) {
        String label = LABEL;
        if (label == null) {
            label = DEFAULT_DEX_VERTEX_LABEL;
        }
        Vertex v = null;
        if (id instanceof Long) {
            v = getVertex(id);
        } else {
            int type = DexTypes.getTypeId(graph, label);
            if (type == edu.upc.dama.dex.core.Graph.INVALID_TYPE) {
                // First instance of this type, let's create it
                type = graph.newNodeType(label);
            }
            assert type != edu.upc.dama.dex.core.Graph.INVALID_TYPE;
            // create object instance
            long oid = graph.newNode(type);
            v = new DexVertex(this, oid);
        }
        return v;
    }

    /*
      * (non-Javadoc)
      *
      * @see com.tinkerpop.blueprints.pgm.Graph#getVertex(java.lang.Object)
      */
    @Override
    public Vertex getVertex(Object id) {
        Vertex v = null;
        if (id instanceof Long) {
            Long oid = (Long) id;
            int type = graph.getType(oid);
            if (type != edu.upc.dama.dex.core.Graph.INVALID_TYPE) {
                // this is an existing oid
                v = new DexVertex(this, oid);
            }
        } else {
            throw new UnsupportedOperationException("Not implemented");
        }
        return v;
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * com.tinkerpop.blueprints.pgm.Graph#removeVertex(com.tinkerpop.blueprints
      * .pgm.Vertex)
      */
    @Override
    public void removeVertex(Vertex vertex) {
        assert vertex instanceof DexVertex;
        graph.drop((Long) vertex.getId());
    }

    /*
      * (non-Javadoc)
      *
      * @see com.tinkerpop.blueprints.pgm.Graph#getVertices()
      */
    @Override
    public Iterable<Vertex> getVertices() {
        Objects result = new Objects(session);
        for (Integer type : graph.nodeTypes()) {
            Objects objs = graph.select(type);
            result.union(objs);
            objs.close();
        }
        Iterable<Vertex> ret = new DexIterable<Vertex>(this, result,
                Vertex.class);
        return ret;
    }

    /*
      * (non-Javadoc)
      *
      * @see com.tinkerpop.blueprints.pgm.Graph#addEdge(java.lang.Object,
      * com.tinkerpop.blueprints.pgm.Vertex, com.tinkerpop.blueprints.pgm.Vertex,
      * java.lang.String)
      */
    @Override
    public Edge addEdge(Object id, Vertex outVertex, Vertex inVertex,
                        String label) {

        Edge e = null;
        if (id instanceof Long) {
            e = getEdge(id);
        } else {
            int type = DexTypes.getTypeId(graph, label);
            if (type == edu.upc.dama.dex.core.Graph.INVALID_TYPE) {
                // First instance of this type, let's create it
                type = graph.newEdgeType(label, true, true);
            }
            assert type != edu.upc.dama.dex.core.Graph.INVALID_TYPE;
            // create object instance
            assert outVertex instanceof DexVertex
                    && inVertex instanceof DexVertex;
            long oid = graph.newEdge((Long) outVertex.getId(),
                    (Long) inVertex.getId(), type);
            e = new DexEdge(this, oid);
        }
        return e;
    }

    /*
      * (non-Javadoc)
      *
      * @see com.tinkerpop.blueprints.pgm.Graph#getEdge(java.lang.Object)
      */
    @Override
    public Edge getEdge(Object id) {
        Edge e = null;
        if (id instanceof Long) {
            Long oid = (Long) id;
            int type = graph.getType(oid);
            if (type != edu.upc.dama.dex.core.Graph.INVALID_TYPE) {
                // this is an existing oid
                e = new DexEdge(this, oid);
            }
        } else {
            throw new UnsupportedOperationException("Not implemented");
        }
        return e;
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * com.tinkerpop.blueprints.pgm.Graph#removeEdge(com.tinkerpop.blueprints
      * .pgm.Edge)
      */
    @Override
    public void removeEdge(Edge edge) {
        assert edge instanceof DexEdge;
        graph.drop((Long) edge.getId());
    }

    /*
      * (non-Javadoc)
      *
      * @see com.tinkerpop.blueprints.pgm.Graph#getEdges()
      */
    @Override
    public Iterable<Edge> getEdges() {
        Objects result = new Objects(session);
        for (Integer type : graph.edgeTypes()) {
            Objects objs = graph.select(type);
            result.union(objs);
            objs.close();
        }
        Iterable<Edge> ret = new DexIterable<Edge>(this, result, Edge.class);
        return ret;
    }

    /*
      * (non-Javadoc)
      *
      * @see com.tinkerpop.blueprints.pgm.Graph#clear()
      */
    @Override
    public void clear() {
        closeAllCollections();

        for (Integer etype : graph.edgeTypes()) {
            for (Long attr : graph.getAttributesFromType(etype)) {
                graph.removeAttribute(attr);
            }
            graph.removeType(etype);
        }
        for (Integer ntype : graph.nodeTypes()) {
            for (Long attr : graph.getAttributesFromType(ntype)) {
                graph.removeAttribute(attr);
            }
            graph.removeType(ntype);
        }

        DexAttributes.clear();
        DexTypes.clear();
    }

    /**
     * Closes all non-closed collections.
     */
    protected void closeAllCollections() {
        while (!collections.isEmpty()) {
            collections.remove(collections.size() - 1).close();
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see com.tinkerpop.blueprints.pgm.Graph#shutdown()
      */
    @Override
    public void shutdown() {
        closeAllCollections();

        graph = null;
        session.close();
        gpool.close();
        dex.close();

        DexAttributes.clear();
        DexTypes.clear();
    }

    @Override
    public String toString() {
        return "dexgraph[" + db.getPath() + "]";
    }

    /*
    * (non-Javadoc)
    *
    * @see
    * com.tinkerpop.blueprints.pgm.IndexableGraph#createManualIndex(java.lang
    * .String, java.lang.Class)
    */
    @Override
    public <T extends Element> Index<T> createManualIndex(String indexName,
                                                          Class<T> indexClass) {
        throw new UnsupportedOperationException();
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * com.tinkerpop.blueprints.pgm.IndexableGraph#createAutomaticIndex(java
      * .lang.String, java.lang.Class, java.util.Set)
      */
    @Override
    public <T extends Element> AutomaticIndex<T> createAutomaticIndex(
            String indexName, Class<T> indexClass, Set<String> indexKeys) {
        throw new UnsupportedOperationException();
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * com.tinkerpop.blueprints.pgm.IndexableGraph#getIndex(java.lang.String,
      * java.lang.Class)
      */
    @Override
    public <T extends Element> Index<T> getIndex(String indexName,
                                                 Class<T> indexClass) {
        if (indexName.compareTo(Index.VERTICES) == 0
                || indexName.compareTo(Index.EDGES) == 0)
            return null;

        int type = DexTypes.getTypeId(getRawGraph(), indexName);
        if (type == edu.upc.dama.dex.core.Graph.INVALID_TYPE) {
            throw new IllegalArgumentException();
        }
        edu.upc.dama.dex.core.Graph.TypeData tdata = DexTypes.getTypeData(getRawGraph(), indexName);
        Index<T> index = null;
        if (tdata.isNodeType()) {
            index = (Index<T>) new DexAutomaticIndex<Vertex>(this, Vertex.class, type);
        } else {
            index = (Index<T>) new DexAutomaticIndex<Edge>(this, Edge.class, type);
        }
        return index;
    }

    /*
      * (non-Javadoc)
      *
      * @see com.tinkerpop.blueprints.pgm.IndexableGraph#getIndices()
      */
    @Override
    public Iterable<Index<? extends Element>> getIndices() {
        List<Index<? extends Element>> ret = new ArrayList<Index<? extends Element>>();
        for (Integer ntype : getRawGraph().nodeTypes()) {
            ret.add(new DexAutomaticIndex<Vertex>(this, Vertex.class, ntype));
        }
        for (Integer etype : getRawGraph().edgeTypes()) {
            ret.add(new DexAutomaticIndex<Edge>(this, Edge.class, etype));
        }
        return ret;
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * com.tinkerpop.blueprints.pgm.IndexableGraph#dropIndex(java.lang.String)
      */
    @Override
    public void dropIndex(String indexName) {
        throw new UnsupportedOperationException();
    }
}
