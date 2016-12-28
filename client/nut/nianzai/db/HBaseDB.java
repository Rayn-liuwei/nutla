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

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.HashMap;

import nut.nianzai.db.NutDB;
import nut.nianzai.util.NutUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

public class HBaseDB implements NutDB
{
	private static final Log log = LogFactory.getLog(HBaseDB.class);
	
	private Class<?> cls;
	
	private static HBaseConfiguration config=new HBaseConfiguration();
	private static HTable table;
	
	static 
	{
		ResourceBundle resource =  ResourceBundle.getBundle("hbase");
		String hbase_master=resource.getString("hbase.master");
		String hbase_zookeeper_quorum=resource.getString("hbase.zookeeper.quorum");
		String tablename=resource.getString("tablename");
		config.set("hbase.master", hbase_master);   
		config.set("hbase.zookeeper.quorum", hbase_zookeeper_quorum);
		try 
		{
			table = new HTable(config, tablename);
		}
		catch (IOException e)
		{
			log.info(e);
		}
	}

	public HBaseDB(Class<?> cls)
	{
		this.cls=cls;
		NutUtil.getFields(cls);
	}
	
	@Override
	public Object get(int id) 
	{
		Object objectCopy = null;
		try 
		{
			Get g = new Get(Bytes.toBytes(id));
			Result rowResult=null;

			rowResult = table.get(g);
			
			objectCopy = cls.getConstructor(new Class[] {}).newInstance(new Object[] {});
			HashMap<String,Method> t=NutUtil.modelMap.get(cls);
			for(Map.Entry<String, Method> entry:t.entrySet())
			{
				if(entry.getKey().equals("id"))
					continue;
				entry.getValue().invoke(objectCopy, new Object[] {Bytes.toString(rowResult.getValue(Bytes.toBytes(entry.getKey()))) });
			}
		} 
		catch (Exception e) 
		{
			log.info(e);
		}
		return objectCopy;
	}
}
