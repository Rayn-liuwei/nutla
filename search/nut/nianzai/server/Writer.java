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

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Writer implements Reactor 
{
	private static final Log log = LogFactory.getLog(Writer.class);
	
	private ByteBuffer output;
	
	public Writer(ByteBuffer output)
	{
		this.output=output;
	}
	
	public void execute(SelectionKey key)
	{
		SocketChannel sc = (SocketChannel) key.channel();
		try
		{
			while(output.hasRemaining())
			{
				int len=sc.write(output);
				if(len<0)
				{ 
					throw new EOFException(); 
			    } 
			    if(len==0) 
			    { 
			    	key.interestOps(SelectionKey.OP_WRITE); 
			        key.selector().wakeup(); 
			        break; 
			    }
			}
			if(!output.hasRemaining())
			{
				output.clear();
				key.cancel();
				sc.close();
				key.selector().selectNow();
			}
		}
		catch(IOException e)
		{
			log.info(e);
		}
	}
}
