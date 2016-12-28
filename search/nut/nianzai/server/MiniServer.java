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
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import nut.nianzai.util.NutUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MiniServer extends Thread
{
	private static final Log log = LogFactory.getLog(MiniServer.class);
	
	private final Selector s;
	private final ServerSocketChannel ssc;
	private ExecutorService executor;
	
	private static Map<String,Long> map=new TreeMap<String,Long>();//保存不能正确完成的SelectionKey
	private ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(1);
	
	public MiniServer(int portnumber,ExecutorService executor) throws IOException
	{
		scheduled.scheduleAtFixedRate(task,10,5,TimeUnit.MINUTES);//每5分钟清空一次map
		this.executor=executor;
		s = Selector.open();
		ssc = ServerSocketChannel.open();
		ssc.socket().bind(new InetSocketAddress(portnumber));
		ssc.configureBlocking(false);
		ssc.register(s,SelectionKey.OP_ACCEPT);
	}
	
	public void run()
	{
		try
		{
			while(s.isOpen())
			{
				int nKeys=s.select();
				if(nKeys==0)
				{
                    for (SelectionKey key : s.keys())
                    {
                    	//log.info("channel " + key.channel() + " waiting for " + key.interestOps());
                    	//如果超过1分钟就废除
                    	if(map.containsKey(key.toString()))
                    	{
                    		Long t= map.get(key.toString());
                    		if((NutUtil.now()-t)>100);
                    		{
                    			map.remove(key.toString());
                    			s.keys().remove(key);
                    			key.cancel();
                    			key.channel().close();
                    			s.selectNow();
                    		}
                    	}
                    	else
                    	{
                    		map.put(key.toString(), NutUtil.now());
                    	}
                    }
	                continue;
				}
				
				Iterator<SelectionKey> it = s.selectedKeys().iterator();  
				while (it.hasNext()) 
				{
					SelectionKey key = it.next();
					it.remove();
	                if (!key.isValid() || !key.channel().isOpen())
	                    continue;
	                if(key.isAcceptable())
	                {
	        			SocketChannel sc = ssc.accept();
	    		        if (sc != null)
	    		        {
	    		    		sc.configureBlocking(false);
	    		            sc.register(s, SelectionKey.OP_READ, new Reader(executor));
	    		        }
	                }
	                else if(key.isReadable()||key.isWritable())
	                {
	                	Reactor reactor = (Reactor) key.attachment();
	                	reactor.execute(key);
	                }
				}
			}
		}
		catch(IOException e)
		{
			log.info(e);
		}
	}
	
	Runnable task = new Runnable()
	{
		public void run()
		{
			map.clear();
		}
	};
}