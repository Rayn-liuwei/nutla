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

package nut.nianzai.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ZookeeperUtil implements Watcher
{
	private static final Log log = LogFactory.getLog(ZookeeperUtil.class);
	
	private ZooKeeper zk;
	private CountDownLatch connectedSignal = new CountDownLatch(1);

	public void connect(String hosts,int SESSION_TIMEOUT) 
	{
		try
		{
			zk = new ZooKeeper(hosts, SESSION_TIMEOUT, this);
			connectedSignal.await();
		}
		catch (IOException e) 
		{
			log.info(e);
		}
		catch (InterruptedException e)
		{
			log.info(e);
		}
	}
	  
	@Override
	public void process(WatchedEvent event) 
	{
		if (event.getState() == KeeperState.SyncConnected) 
		{
			connectedSignal.countDown();
		}
	}
	  
	public void close()
	{
		try 
		{
			zk.close();
		} 
		catch (InterruptedException e) 
		{
			log.info(e);
		}
	}
	
	public List<String> list(String path)
	{
		List<String> childs = new ArrayList<String>();
 		try 
		{
			Stat stat = zk.exists(path, false);
			if(stat!=null)
			{
	 			childs = zk.getChildren(path, false);
				if (childs.isEmpty()) 
				{
					log.info("No members in group "+path);
				}
			}
		}
		catch (KeeperException e) 
		{
			log.info(e);
		}
		catch (InterruptedException e)
		{
			log.info(e);
		}
		return childs;
	}
  
	public void delete(String path)
	{
  		try 
		{
			Stat stat = zk.exists(path, false);
			if(stat!=null)
			{
				List<String> children = zk.getChildren(path, false);
				for (String child : children) 
				{
					zk.delete(path + "/" + child, -1);
				}
				zk.delete(path, -1);
			}
		}
		catch (KeeperException e) 
		{
			log.info(e);
		}
		catch (InterruptedException e)
		{
			log.info(e);
		}
	}

	public void create(String path)
	{
		try
		{
			Stat stat = zk.exists(path, false);
			if(stat==null)
				zk.create(path, null, Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
		}
		catch (KeeperException e) 
		{
			log.info(e);
		}
		catch (InterruptedException e)
		{
			log.info(e);
		}
	}

	public void join(String groupName, String memberName)
	{
		String path =groupName + "/" + memberName;
		try
		{
			Stat stat = zk.exists(path, false);
			if(stat==null)
				zk.create(path, null, Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
		} 
		catch (KeeperException e) 
		{
			log.info(e);
		}
		catch (InterruptedException e)
		{
			log.info(e);
		}
	}
	
	//获取分布式锁
	public boolean getLock(String lockname, String indexid)
	{
		String	path =lockname + "/" + indexid;
		boolean temp=false;
		try
		{
			String olock=zk.create(path, null, Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
			List<String> childs=zk.getChildren(lockname, false);
			//将不是indexid节点的排除
			for(String child:childs)
			{
				if(!child.startsWith(indexid))
					childs.remove(child);
			}
		    if(olock.equals(lockname+"/"+min(childs.toArray(new String[0]))))
		    	temp=true;
		} 
		catch (KeeperException e) 
		{
			log.info(e);
		}
		catch (InterruptedException e)
		{
			log.info(e);
		}
		return temp;
	}
	
	//得到数组最新值
	public String min(String[] s)
	{
		if(s.length==1)
			return s[0];
		long temp=Long.parseLong(s[0]);
		int k=0;
		for (int i=1;i<s.length;i++)
		{
			if(Long.parseLong(s[i])<temp)
			{
				temp=Long.parseLong(s[i]);
				k=i;
			}
		}
		return s[k];
	}
	
	public static void main(String[] args)
	{
		ZookeeperUtil keep = new ZookeeperUtil();
		keep.connect("192.168.195.128:2181",5*1000);
//		keep.create("/nutzk");
//		keep.create("/nutzk/sg1");
//		keep.create("/nutzk/sg1/run");
//		keep.create("/nutzk/sg1/lock");
//		keep.create("/nutzk/sg1/bak");
		List<String> childs=keep.list("/");
		//keep.join("zoo", "127.0.0.1:7000");
		for (String child : childs) 
		{
			System.out.println(child);
		}
		
		//System.out.println(keep.getLock("lock", "2"));
		//keep.delete("hbase");
		keep.close();
	}
}
