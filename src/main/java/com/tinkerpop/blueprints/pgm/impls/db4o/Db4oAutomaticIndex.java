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

import com.tinkerpop.blueprints.pgm.AutomaticIndex;

/**
 * @author germanviscuso
 *
 */
public class Db4oAutomaticIndex<T extends Db4oElement> extends Db4oIndex<T>
		implements AutomaticIndex<T> {

	/* (non-Javadoc)
	 * @see com.tinkerpop.blueprints.pgm.AutomaticIndex#getAutoIndexKeys()
	 */
	@Override
	public Set<String> getAutoIndexKeys() {
		// TODO Auto-generated method stub
		return null;
	}

}
