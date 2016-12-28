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

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nut.nianzai.util.NutUtil;

public class Reader implements Reactor 
{
	private static final Log log = LogFactory.getLog(Reader.class);
	
	private byte[] bytes=new byte[0];
	private ExecutorService executor;
	
	public Reader(ExecutorService executor)
	{
		this.executor=executor;
	}
	
	@Override
	public void execute(SelectionKey key)
	{
		SocketChannel sc = (SocketChannel) key.channel();
		try
		{
			ByteBuffer buffer=ByteBuffer.allocate(1024);
			int len=-1;
			while((len=sc.read(buffer))>0)
			{
				buffer.flip();
	  			byte [] content = new byte[buffer.limit()];
				buffer.get(content);
				bytes=NutUtil.ArrayCoalition(bytes,content);
			    buffer.clear();
			}
			if(len==0)
			{
				key.interestOps(SelectionKey.OP_READ);
				key.selector().wakeup(); 
			}
			else if(len==-1)
			{
				Callable<byte[]> call=new ProcessCallable(bytes);
				Future<byte[]> task=executor.submit(call);
				ByteBuffer output=ByteBuffer.wrap(task.get());
				sc.register(key.selector(), SelectionKey.OP_WRITE, new Writer(output));
			}
		}
		catch(Exception e)
		{
			log.info(e);
		}
	}
}
