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

import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.collections.ActivatableHashMap;
import com.db4o.ta.Activatable;
import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Element;
import com.tinkerpop.blueprints.pgm.TransactionalGraph;
import com.tinkerpop.blueprints.pgm.impls.StringFactory;

/**
 * @author germanviscuso
 *
 */
public class Db4oElement implements Element, Activatable {

	protected String _label = null;
    protected Db4oGraph _graph;
    protected ActivatableHashMap<String, Object> properties = new ActivatableHashMap<String, Object>();
    private transient Activator _activator;
	
    public Db4oElement(Db4oGraph graph){
    	activate(ActivationPurpose.WRITE);
    	_graph = graph;
    }

	@Override
	public Object getProperty(String key) {
		activate(ActivationPurpose.READ);
		return properties.get(key);
	}

	@Override
	public Set<String> getPropertyKeys() {
		activate(ActivationPurpose.READ);
		return properties.keySet();
	}

	@Override
	public void setProperty(String key, Object value) {
		if (key.equals(StringFactory.ID) || (key.equals(StringFactory.LABEL) && this instanceof Edge))
            throw new RuntimeException(key + StringFactory.PROPERTY_EXCEPTION_MESSAGE);
		try {
            _graph.autoStartTransaction();
            Object oldValue = getProperty(key);

            //for (Db4oAutomaticIndex autoIndex : _graph.getAutoIndices()) {
                //TODO autoIndex.autoUpdate(key, value, oldValue, this);
            //}
            activate(ActivationPurpose.WRITE);
            properties.put(key, value);
            _graph.autoStopTransaction(TransactionalGraph.Conclusion.SUCCESS);
        } catch (RuntimeException e) {
            _graph.autoStopTransaction(TransactionalGraph.Conclusion.FAILURE);
            throw e;
        } catch (Exception e) {
            _graph.autoStopTransaction(TransactionalGraph.Conclusion.FAILURE);
            throw new RuntimeException(e.getMessage(), e);
        }
	}

	@Override
	public Object removeProperty(String key) {
		activate(ActivationPurpose.WRITE);
		return properties.remove(key);
	}

	@Override
	public Object getId() {
		activate(ActivationPurpose.READ);
		return _graph.getId(this);
	}

	public String getLabel() {
		activate(ActivationPurpose.READ);
		return _label;
	}

	public void setLabel(String label) {
		activate(ActivationPurpose.WRITE);
		_label = label;
	}

	// db4o transparent persistence methods (Activatable interface)
	@Override
	public void bind(Activator activator) {
	    if (_activator == activator) {
	        return;
	    }
	    if (activator != null && null != _activator) {
	        throw new IllegalStateException("Object can only be bound to one activator");
	    }
	    _activator = activator;
	}

	@Override
	public void activate(ActivationPurpose activationPurpose) {
	    if(null != _activator){
	        _activator.activate(activationPurpose);
	    }
	}

}
