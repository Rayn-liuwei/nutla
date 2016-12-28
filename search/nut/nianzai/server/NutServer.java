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

package nut.nianzai.server;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import nut.nianzai.util.ZookeeperUtil;

public class NutServer 
{
	public static void main(String[] args) throws IOException
	{
		ResourceBundle resource =  ResourceBundle.getBundle("server");
		int isrun=Integer.parseInt(resource.getString("isrun"));
		String level=resource.getString("level");
		int portnumber=Integer.parseInt(resource.getString("portnumber"));//服务端口号
		String indexid= resource.getString("indexpath").split(";")[0];
		String hostport= InetAddress.getLocalHost().getHostAddress()+":"+portnumber+":"+level+";"+indexid;
		String zksearchgroup=resource.getString("zksearchgroup");
		String zkhost=resource.getString("zkhost");
		int zktimeout=Integer.parseInt(resource.getString("zktimeout"));
		ZookeeperUtil keep = new ZookeeperUtil();
		keep.connect(zkhost,zktimeout*1000);
		String zkstr="";
		if(isrun==0)
		{
			zkstr="/nutzk/"+zksearchgroup+"/run";
			//启动索引同步程序
			IndexSyncServer.start();
		}
		else if(isrun==1)
		{
			zkstr="/nutzk/"+zksearchgroup+"/bak";
			//启动备份检查程序
			CheckServer.start();
		}
		keep.join(zkstr, hostport);
		//服务线程数
		String threadpool=resource.getString("threadpool");
		String[] tp=threadpool.split(",");
		int minthread=Integer.parseInt(tp[0]);
		int maxthread=Integer.parseInt(tp[1]);
		int timeout=Integer.parseInt(tp[2]);
		int queue=Integer.parseInt(tp[3]);
		//启动工作线程
		ExecutorService executor = new ThreadPoolExecutor(minthread,maxthread,timeout,TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(queue),new ThreadPoolExecutor.CallerRunsPolicy());
		//启动服务器
		new MiniServer(portnumber,executor).start();
	}
}