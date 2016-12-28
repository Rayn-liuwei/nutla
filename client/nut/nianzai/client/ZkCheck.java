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

package nut.nianzai.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import nut.nianzai.util.NutUtil;
import nut.nianzai.util.ZookeeperUtil;

public class ZkCheck 
{
	private static String zkhost;
	private static int zktimeout;
	private static String path="/nutzk";
	
	static
	{
		ResourceBundle resource =  ResourceBundle.getBundle("client");//读取配置文件
		zkhost=resource.getString("zkhost");
		zktimeout=Integer.parseInt(resource.getString("zktimeout"));
	}
	
	public static void start()
	{
		ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(1);
		scheduled.scheduleAtFixedRate(new Runnable() 
		{
			public void run() 
			{
				Map<String,HashMap<String, Long>> hostlist=new HashMap<String,HashMap<String,Long>>();
				Map<String,HashMap<String, Long>> temp=new HashMap<String,HashMap<String,Long>>();
				ZookeeperUtil keep = new ZookeeperUtil();
				keep.connect(zkhost,zktimeout*1000);
				List<String> childs=keep.list(path);
				for (String child : childs) 
				{
					List<String> sgs=keep.list(path+"/"+child+"/run");
					HashMap<String,Long> run=new HashMap<String,Long>();
					for (String sg : sgs) 
					{
						run.put(sg.split(";")[0], NutUtil.now());
					}
					hostlist.put(child, run);
				}
				keep.close();
				temp=NutClient.hostlist;
				NutClient.hostlist=hostlist;
				for(Map.Entry<String,HashMap<String, Long>> entry:temp.entrySet())
				{
					entry.getValue().clear();
				}
				temp.clear();
	        }
		}, 0, zktimeout,TimeUnit.SECONDS);
	}
}
