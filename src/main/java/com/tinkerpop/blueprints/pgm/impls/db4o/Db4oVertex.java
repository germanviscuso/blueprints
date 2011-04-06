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

import com.db4o.collections.ActivatableArrayList;
import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Vertex;

/**
 * @author germanviscuso
 *
 */
public class Db4oVertex extends Db4oElement implements Vertex {

	public Db4oVertex(Db4oGraph graph) {
		super(graph);
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.Vertex#getOutEdges()
	 */
	@Override
	public Iterable<Edge> getOutEdges() {
		return _graph.getOutEdges(this, null);
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.Vertex#getInEdges()
	 */
	@Override
	public Iterable<Edge> getInEdges() {
		return _graph.getInEdges(this, null);
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.Vertex#getOutEdges(java.lang.String)
	 */
	@Override
	public Iterable<Edge> getOutEdges(String label) {
		return _graph.getOutEdges(this, label);
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.Vertex#getInEdges(java.lang.String)
	 */
	@Override
	public Iterable<Edge> getInEdges(String label) {
		return _graph.getInEdges(this, label);
	}

}
