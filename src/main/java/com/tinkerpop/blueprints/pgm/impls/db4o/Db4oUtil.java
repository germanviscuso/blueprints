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

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.config.ConfigScope;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.config.QueryEvaluationMode;
import com.db4o.cs.Db4oClientServer;
import com.db4o.cs.config.ServerConfiguration;
import com.db4o.ta.TransparentPersistenceSupport;

/**
 * @author germanviscuso
 *
 */
public class Db4oUtil {
	
	protected static String DATABASE_FILE_NAME = "db.db4o";
	protected static ObjectServer _objectServer;// = startServer();
	
	protected static ServerConfiguration serverConfig() {
		ServerConfiguration config = Db4oClientServer.newServerConfiguration();
        // Use lazy mode
        config.common().queries().evaluationMode(QueryEvaluationMode.LAZY);
        // Set activation depth to 5
        config.common().activationDepth(5);
        // Enable TP
        config.common().add(new TransparentPersistenceSupport());
        // Enable UUIDs
        config.file().generateUUIDs(ConfigScope.GLOBALLY);
        // Set index by Key
        //config.common().objectClass(keyValuePairClass).objectField(keyFieldName).indexed(true);
        // Cascade on delete
        //config.common().objectClass(keyValuePairClass).cascadeOnDelete(true);
        return config;
    }
	
	protected static EmbeddedConfiguration embeddedConfig() {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
        // Use lazy mode
        config.common().queries().evaluationMode(QueryEvaluationMode.LAZY);
        // Set activation depth to 5
        config.common().activationDepth(5);
        // Set update depth to 5
        config.common().updateDepth(3);
        // Enable TP
        config.common().add(new TransparentPersistenceSupport());
        // Enable UUIDs
        config.file().generateUUIDs(ConfigScope.GLOBALLY);
        // Set index by Key
        //config.common().objectClass(keyValuePairClass).objectField(keyFieldName).indexed(true);
        // Cascade on delete
        //config.common().objectClass(keyValuePairClass).cascadeOnDelete(true);
        return config;
    }
	
	protected static ObjectServer startServer(){
		shutdownServer();
		return Db4oClientServer.openServer(serverConfig(), DATABASE_FILE_NAME, 0);
	}
	
	protected static ObjectContainer openDatabase(){
		return Db4oEmbedded.openFile(embeddedConfig(), DATABASE_FILE_NAME);
	}
	
	protected static ObjectContainer openDatabase(String directory){
		return Db4oEmbedded.openFile(embeddedConfig(), directory + "/" + DATABASE_FILE_NAME);
	}
	
	protected static void shutdownServer(){
		if (_objectServer != null){
			_objectServer.close();
			_objectServer = null;
		}
	}

}
