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

import com.db4o.activation.ActivationPurpose;
import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Vertex;

/**
 * @author germanviscuso
 *
 */
public class Db4oEdge extends Db4oElement implements Edge {
	
	private Db4oVertex _inVertex = null;
	private Db4oVertex _outVertex = null;

	public Db4oEdge(Db4oGraph graph) {
		super(graph);
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.Edge#getOutVertex()
	 */
	@Override
	public Vertex getOutVertex() {
		activate(ActivationPurpose.READ);
		return _outVertex;
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.Edge#getInVertex()
	 */
	@Override
	public Vertex getInVertex() {
		activate(ActivationPurpose.READ);
		return _inVertex;
	}

	protected void setInVertex(Db4oVertex inVertex) {
		activate(ActivationPurpose.WRITE);
		_inVertex = inVertex;
	}

	protected void setOutVertex(Db4oVertex outVertex) {
		activate(ActivationPurpose.WRITE);
		_outVertex = outVertex;
	}

}
