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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nut.nianzai.util.NutUtil;
import nut.nianzai.util.SearchUtil;
import nut.nianzai.util.ZookeeperUtil;

/**
 * 检查运行中的服务器，如果发现有宕机的服务器则用备份服务器顶上
 * 备份索引服务器并不需要同步索引，因为备份索引服务器一旦转为运行中索引服务器，那么很快就会由索引同步程序同步索引
 * @author nianzai
 *
 */
public class CheckServer 
{
	private static final Log log = LogFactory.getLog(CheckServer.class);
	
	private static String zksearchgroup;
	private static String zkhost;
	private static int zktimeout;
	private static String hostport;
	private static String[] indexbakuppaths;
	private static String[] hdfsindexbakuppaths;
	private static String run;
	private static String lock;
	private static String bak;
	
	static
	{
		ResourceBundle resource =  ResourceBundle.getBundle("server");//读取配置文件
		zksearchgroup=resource.getString("zksearchgroup");
		zkhost=resource.getString("zkhost");
		zktimeout=Integer.parseInt(resource.getString("zktimeout"));
		indexbakuppaths=resource.getString("indexbakuppaths").split(",");
		hdfsindexbakuppaths=resource.getString("hdfsindexbakuppaths").split(",");
		try {
			hostport = InetAddress.getLocalHost().getHostAddress()+":"+resource.getString("portnumber");
		} catch (UnknownHostException e) {
			log.info(e);
		}
		run="/nutzk/"+zksearchgroup+"/run";
		lock="/nutzk/"+zksearchgroup+"/lock";
		bak="/nutzk/"+zksearchgroup+"/bak";
	}
	
	public static void start()
	{
		final ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(1);
		scheduled.scheduleAtFixedRate(new Runnable() 
		{
			public void run() 
			{
				ZookeeperUtil keep = new ZookeeperUtil();
				keep.connect(zkhost,zktimeout*1000);
				List<String> childs=keep.list(run);
				String runids=",";//正在运行的索引ids
				String indexpath="";//备用索引路径
				String hdfsindexpath="";//HDFS索引对应路径
				String indexid="";//备用索引id
				for (String child : childs)
				{
					String[] rr=child.split(";");
					runids=runids+rr[1]+",";
				}
				for(int i=0;i<indexbakuppaths.length;i++)
				{
					String path=indexbakuppaths[i];
					String[] hh=path.split(";");
					if(runids.indexOf(hh[0])==-1)
					{
						indexid=hh[0];//索引id
						indexpath=","+hh[1]+",";//本地索引路径
						hdfsindexpath=hdfsindexbakuppaths[i];//同步HDFS索引路径
						break;
					}
				}
				keep.close();
				if(!NutUtil.isEmpty(indexpath))
				{
					ZookeeperUtil kp = new ZookeeperUtil();
					kp.connect(zkhost,zktimeout*1000);
					if(kp.getLock(lock, indexid))
					{
						kp.join(run, hostport+";"+indexid);//加入到正在运行组
						kp.delete(bak+"/"+hostport);//从备用组中删除自己
						SearchUtil.setIndexpath(indexpath);
						SearchUtil.setHDFSindexpath(hdfsindexpath);
						SearchUtil.reopen();
						IndexSyncServer.start();//启动索引同步程序
						scheduled.shutdown();
					}
				}
	        }
		}, 0, zktimeout,TimeUnit.SECONDS);
	}
	
	public static void main(String[] args) 
	{
		CheckServer.start();
	}
}
