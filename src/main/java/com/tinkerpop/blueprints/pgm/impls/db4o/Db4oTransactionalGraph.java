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

import java.util.Set;

import com.db4o.ObjectContainer;
import com.tinkerpop.blueprints.pgm.AutomaticIndex;
import com.tinkerpop.blueprints.pgm.Element;
import com.tinkerpop.blueprints.pgm.TransactionalGraph;

/**
 * @author germanviscuso
 *
 */
public class Db4oTransactionalGraph extends Db4oGraph implements
		TransactionalGraph {
	
	Db4oTransactionalGraph(ObjectContainer oc){
		super(oc);
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

}
