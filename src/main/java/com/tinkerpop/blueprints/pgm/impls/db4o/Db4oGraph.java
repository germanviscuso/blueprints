/*
 * Copyright 2011 Versant Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.tinkerpop.blueprints.pgm.impls.db4o;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.ext.Db4oUUID;
import com.db4o.ext.ObjectInfo;
import com.db4o.query.Predicate;
import com.db4o.query.Query;
import com.tinkerpop.blueprints.pgm.AutomaticIndex;
import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Element;
import com.tinkerpop.blueprints.pgm.Index;
import com.tinkerpop.blueprints.pgm.IndexableGraph;
import com.tinkerpop.blueprints.pgm.TransactionalGraph;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.TransactionalGraph.Conclusion;
import com.tinkerpop.blueprints.pgm.TransactionalGraph.Mode;
import com.tinkerpop.blueprints.pgm.util.AutomaticIndexHelper;

/**
 * @author germanviscuso
 *
 */
public class Db4oGraph implements TransactionalGraph, IndexableGraph {
	
	private ObjectContainer _rootContainer;
	private ObjectContainer _txnContainer;
	private Mode _mode = Mode.AUTOMATIC;

    protected Map<String, Db4oIndex> manualIndices = new HashMap<String, Db4oIndex>();
    protected Map<String, Db4oAutomaticIndex> autoIndices = new HashMap<String, Db4oAutomaticIndex>();

	public Db4oGraph(ObjectContainer _objectContainer) {
		_rootContainer = _objectContainer;
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.Graph#addVertex(java.lang.Object)
	 */
	@Override
	public Vertex addVertex(Object id) {
		try {
            autoStartTransaction();
            final Vertex vertex = new Db4oVertex(this);
            autoStopTransaction(Conclusion.SUCCESS);
            return vertex;
        } catch (RuntimeException e) {
            autoStopTransaction(TransactionalGraph.Conclusion.FAILURE);
            throw e;
        } catch (Exception e) {
            autoStopTransaction(TransactionalGraph.Conclusion.FAILURE);
            throw new RuntimeException(e.getMessage(), e);
        }
	}
	
	private Db4oElement getElement(Object id){
		Db4oElement element;
		if (null == id)
            return null;
		try {
        	Db4oUUID uuid = (Db4oUUID)id;
        	element = _rootContainer.ext().getByUUID(uuid);
        	return element;
        } catch (ClassCastException e) {
            throw new RuntimeException("Db4o vertex ids must be of type Db4oUUID", e);
        }
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.Graph#getVertex(java.lang.Object)
	 */
	@Override
	public Vertex getVertex(Object id) {
    	Db4oElement element = getElement(id);
    	if(element == null)
    		return null;
        try {
    		_rootContainer.ext().activate(element);
    		return (Db4oVertex)element;
        } catch (ClassCastException e) {
            throw new RuntimeException("Retrieved vertex must be of type Vertex", e);
        }
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.Graph#removeVertex(com.tinkerpop.blueprints.pgm.Vertex)
	 */
	@Override
	public void removeVertex(Vertex vertex) {
        if (null != vertex) {
            try {
                AutomaticIndexHelper.removeElement(this, vertex);
                this.autoStartTransaction();
                for (final Edge edge : vertex.getInEdges()) {
                	_txnContainer.delete((Db4oEdge) edge);
                }
                for (final Edge edge : vertex.getOutEdges()) {
                	_txnContainer.delete((Db4oEdge) edge);
                }
                _txnContainer.delete((Db4oVertex) vertex);
                this.autoStopTransaction(Conclusion.SUCCESS);
            } catch (Exception e) {
                this.autoStopTransaction(TransactionalGraph.Conclusion.FAILURE);
                throw new RuntimeException(e.getMessage(), e);
            }
        }
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.Graph#getVertices()
	 */
	@Override
	public Iterable<Vertex> getVertices() {
		Query query = _rootContainer.query();
        query.constrain(Db4oVertex.class);
        return query.execute();
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.Graph#addEdge(java.lang.Object, com.tinkerpop.blueprints.pgm.Vertex, com.tinkerpop.blueprints.pgm.Vertex, java.lang.String)
	 */
	@Override
	public Edge addEdge(Object id, Vertex outVertex, Vertex inVertex,
			String label) {
        try {
            autoStartTransaction();
            Db4oEdge edge = new Db4oEdge(this);
            edge.setOutVertex((Db4oVertex)outVertex);
            edge.setInVertex((Db4oVertex)inVertex);
            autoStopTransaction(Conclusion.SUCCESS);
            return edge;
        } catch (Exception e) {
            autoStopTransaction(TransactionalGraph.Conclusion.FAILURE);
            throw new RuntimeException(e.getMessage(), e);
        }
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.Graph#getEdge(java.lang.Object)
	 */
	@Override
	public Edge getEdge(Object id) {
		Db4oElement element = getElement(id);
        try {
    		_rootContainer.ext().activate(element);
    		return (Db4oEdge)element;
        } catch (ClassCastException e) {
            throw new RuntimeException("Retrieved edge must be of type Edge", e);
        }
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.Graph#removeEdge(com.tinkerpop.blueprints.pgm.Edge)
	 */
	@Override
	public void removeEdge(Edge edge) {
		if (null != edge) {
            try {
                AutomaticIndexHelper.removeElement(this, edge);
                this.autoStartTransaction();
                _txnContainer.delete((Db4oEdge) edge);
                this.autoStopTransaction(Conclusion.SUCCESS);
            } catch (Exception e) {
                this.autoStopTransaction(TransactionalGraph.Conclusion.FAILURE);
                throw new RuntimeException(e.getMessage(), e);
            }
        }
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.Graph#getEdges()
	 */
	@Override
	public Iterable<Edge> getEdges() {
		Query query = _rootContainer.query();
        query.constrain(Db4oEdge.class);
        return query.execute();
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.Graph#clear()
	 */
	@Override
	public void clear() {
		for (Vertex vertex : getVertices()) {
            removeVertex(vertex);
        }
        for (Index index : getIndices()) {
            dropIndex(index.getIndexName());
        }
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.Graph#shutdown()
	 */
	@Override
	public void shutdown() {
		shutdown(_txnContainer);
        shutdown(_rootContainer);
        manualIndices.clear();
        autoIndices.clear();
	}
	
	private void shutdown(ObjectContainer oc){
		if (oc != null) {
			oc.rollback();
			oc.close();
			oc = null;
        }
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.IndexableGraph#createManualIndex(java.lang.String, java.lang.Class)
	 */
	@Override
	public <T extends Element> Index<T> createManualIndex(String indexName,
			Class<T> indexClass) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.IndexableGraph#createAutomaticIndex(java.lang.String, java.lang.Class, java.util.Set)
	 */
	@Override
	public <T extends Element> AutomaticIndex<T> createAutomaticIndex(
			String indexName, Class<T> indexClass, Set<String> indexKeys) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.IndexableGraph#getIndex(java.lang.String, java.lang.Class)
	 */
	@Override
	public <T extends Element> Index<T> getIndex(String indexName,
			Class<T> indexClass) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.IndexableGraph#getIndices()
	 */
	@Override
	public Iterable<Index<? extends Element>> getIndices() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.IndexableGraph#dropIndex(java.lang.String)
	 */
	@Override
	public void dropIndex(String indexName) {
		// TODO Auto-generated method stub

	}
	
	protected Iterable<Db4oAutomaticIndex> getAutoIndices() {
        return autoIndices.values();
    }

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.TransactionalGraph#startTransaction()
	 */
	@Override
	public void startTransaction() {
		if (Mode.AUTOMATIC == _mode)
            throw new RuntimeException(TransactionalGraph.TURN_OFF_MESSAGE);
        if (_txnContainer == null)
        	// Start new transaction
    		_txnContainer = _rootContainer.ext().openSession();
        else
            throw new RuntimeException(TransactionalGraph.NESTED_MESSAGE);
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.TransactionalGraph#stopTransaction(com.tinkerpop.blueprints.pgm.TransactionalGraph.Conclusion)
	 */
	@Override
	public void stopTransaction(Conclusion conclusion) {
		if (Mode.AUTOMATIC == _mode)
            throw new RuntimeException(TransactionalGraph.TURN_OFF_MESSAGE);

        if (null == _txnContainer)
            throw new RuntimeException("There is no active transaction to stop");

        if (conclusion == Conclusion.SUCCESS) {
        	_txnContainer.commit();
        } else {
        	_txnContainer.rollback();
        }
        _txnContainer.close();
        _txnContainer = null;
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.TransactionalGraph#setTransactionMode(com.tinkerpop.blueprints.pgm.TransactionalGraph.Mode)
	 */
	@Override
	public void setTransactionMode(Mode mode) {
		if (null != _txnContainer) {
			_txnContainer.commit();
			_txnContainer.close();
            _txnContainer = null;
        }
        _mode = mode;
	}
	
	protected void autoStartTransaction() {
        if (getTransactionMode() == Mode.AUTOMATIC) {
            if (_txnContainer == null)
            	_txnContainer = _rootContainer.ext().openSession();
            else
                throw new RuntimeException(TransactionalGraph.NESTED_MESSAGE);
        }
    }

    protected void autoStopTransaction(final Conclusion conclusion) {
        if (getTransactionMode() == Mode.AUTOMATIC) {
            if (conclusion == Conclusion.SUCCESS)
            	_txnContainer.commit();
            else
            	_txnContainer.rollback();
            _txnContainer.close();
            _txnContainer = null;
        }
    }

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.TransactionalGraph#getTransactionMode()
	 */
	@Override
	public Mode getTransactionMode() {
		return _mode;
	}
	
	public ObjectContainer getRawGraph(){
		return _rootContainer;
	}

    public String toString() {
        return "db4ograph[" + _rootContainer.toString() + "]";
    }
    
    public Db4oUUID getId(Db4oElement element){
    	//return _rootContainer.ext().getID(element);
    	ObjectInfo of = _rootContainer.ext().getObjectInfo(element);
    	if(of != null)
    		return _rootContainer.ext().getObjectInfo(element).getUUID();
    	return null;
    }

	public Iterable<Edge> getOutEdges(final Db4oVertex db4oVertex, String label) {
		/*ObjectSet<Db4oEdge> result = _txnContainer.query(new Predicate<Db4oEdge>() {
		    @Override
		    public boolean match(Db4oEdge edge) {
		    	boolean vertexMatch = edge.getInVertex().equals(db4oVertex);
		    	boolean labelMatch = true;
		    	if(label != null)
		        	labelMatch = edge.getLabel().equals(label);
		        return vertexMatch && labelMatch;
		    }
		});
		return result;*/
		Query query = _rootContainer.query();
        query.constrain(Db4oEdge.class);
        query.descend("_inVertex").constrain(db4oVertex);
        if(label != null)
        	query.descend("_label").constrain(label);
        return query.execute();
	}

	public Iterable<Edge> getInEdges(Db4oVertex db4oVertex, String label) {
		Query query = _rootContainer.query();
        query.constrain(Db4oEdge.class);
        query.descend("_outVertex").constrain(db4oVertex);
        if(label != null)
        	query.descend("_label").constrain(label);
        return query.execute();
	}

}
