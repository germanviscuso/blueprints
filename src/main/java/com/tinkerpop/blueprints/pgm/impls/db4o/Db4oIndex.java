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

import com.tinkerpop.blueprints.pgm.Index;

/**
 * @author germanviscuso
 *
 */
public class Db4oIndex<T extends Db4oElement> implements Index<T> {

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.Index#getIndexName()
	 */
	@Override
	public String getIndexName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.Index#getIndexClass()
	 */
	@Override
	public Class<T> getIndexClass() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.Index#getIndexType()
	 */
	@Override
	public com.tinkerpop.blueprints.pgm.Index.Type getIndexType() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.Index#put(java.lang.String, java.lang.Object, com.tinkerpop.blueprints.pgm.Element)
	 */
	@Override
	public void put(String key, Object value, T element) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.Index#get(java.lang.String, java.lang.Object)
	 */
	@Override
	public Iterable<T> get(String key, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.Index#remove(java.lang.String, java.lang.Object, com.tinkerpop.blueprints.pgm.Element)
	 */
	@Override
	public void remove(String key, Object value, T element) {
		// TODO Auto-generated method stub

	}

}
