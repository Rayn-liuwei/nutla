/** 
  * Copyright 2010	 曾年仔	mail:zengnianzai@163.com
  * 
  * Licensed under the Apache License, Version 2.0 (the "License"); 
  * you may not use this file except in compliance with the License. 
  * You may obtain a copy of the License at 
  * 
  *     http://www.apache.org/licenses/LICENSE-2.0 
  * 
  * Unless required by applicable law or agreed to in writing, software 
  * distributed under the License is distributed on an "AS IS" BASIS, 
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
  * See the License for the specific language governing permissions and 
  * limitations under the License. 
  */ 

package nut.nianzai.db;

import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.ReflectionDBObject;

public class MongoDB implements NutDB
{
	private static final Log log = LogFactory.getLog(MongoDB.class);
	
	private static DBCollection coll=null;
	private static DB db=null;
	
	static
	{
		try
		{
			ResourceBundle resource =  ResourceBundle.getBundle("mongodb");
			String host=resource.getString("host");
			int port=Integer.parseInt(resource.getString("port"));
			String dbname=resource.getString("dbname");
			db = new Mongo(host,port).getDB(dbname);
			coll = db.getCollection("NutCollection");
		}
		catch(Exception e){log.info(e);}
	}
	
	public MongoDB(Class<?> cs)
	{
		coll.setObjectClass(cs);
	}
	
	public void insert(ReflectionDBObject r)
	{
		coll.insert(r);
	}
	
	public void delete(ReflectionDBObject r)
	{
		coll.remove(r);
	}
	
	public void update(ReflectionDBObject oldr,ReflectionDBObject newr)
	{
		coll.update(oldr, newr);
	}
	
	public Object get(int id)
	{
        DBObject in = new BasicDBObject("Id", id); 
        if(coll.find(in).hasNext())
        	return coll.find(in).next();
        else       
        	return null;
	}
	
	public void empty()
	{
		coll.drop();
	}
}
