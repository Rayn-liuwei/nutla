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

package nut.nianzai.cache;

import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

public class MemCached implements NutCache
{
	private static MemCachedClient mcc=null;
	private static int timeout=0;

	static 
	{
		ResourceBundle resource =  ResourceBundle.getBundle("memcached");
		String instancename=resource.getString("instancename");
		String ss=resource.getString("servers");
		String ws=resource.getString("weights");
		timeout=Integer.parseInt(resource.getString("timeout"));
		
		mcc = new MemCachedClient(instancename);
		mcc.setSanitizeKeys(false);
		
		String[] servers=ss.split(",");
		String[] weights=ws.split(",");
		ArrayList<Integer> list=new ArrayList<Integer>();
		for(String s:weights)
		{
			Integer w=Integer.parseInt(s);
			list.add(w);
		}

		SockIOPool pool=SockIOPool.getInstance(instancename);
		pool.setServers(servers);
		pool.setWeights(list.toArray(new Integer[0]));
		pool.setInitConn(5);
		pool.setMinConn(5);
		pool.setMaxConn(250);
		pool.setMaxIdle(1000*60*60*6);
		pool.setMaintSleep(30);
		pool.setNagle(false);
		pool.setSocketTO(2*1000);//两秒超时
		pool.setSocketConnectTO(0);
		pool.initialize();
	}
	
	public boolean add(String key, Object value)
	{   
		return mcc.add(key, value, new Date(timeout*60*1000));
	}   

    public Object get(String key)
    {   
    	return mcc.get(key,null,true);
    }
    
    public void delete(String key)
    {
    	mcc.delete(key);
    }
    
	public static void main(String args[]) 
	{
		MemCached mm= new MemCached();
		//mm.add("soho", "1,4,7,4,3,@,9");
		System.out.println(mm.get("soho"));
	}
}
