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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nut.nianzai.client.ScoreInfo;
import nut.nianzai.util.Parameter;
import nut.nianzai.util.Score;

public class InfoCallable implements Callable<ScoreInfo>
{
	private static final Log log = LogFactory.getLog(InfoCallable.class);
	
	private String ip;
	private int port;
	private Parameter p;
	
	public InfoCallable(String ip,int port,Parameter p)
	{
		this.ip=ip;
		this.port=port;
		this.p=p;
	}
	
	@Override
	public ScoreInfo call()
	{
		ScoreInfo info=new ScoreInfo();
		List<Score> list = new ArrayList<Score>();
		
		Socket socket=null;
		try 
		{
			socket = new Socket();
			SocketAddress host = new InetSocketAddress(InetAddress.getByName(ip),port);
			socket.connect(host,2*1000);//连接超时
			socket.setSoTimeout(2*1000);//读取超时
			ObjectOutputStream clientOutputStream = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream())); 
			clientOutputStream.writeObject(p);
			clientOutputStream.flush();
			socket.shutdownOutput();
			
			ObjectInputStream clientInputStream = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
			Object o=clientInputStream.readObject();
			info.setN((Integer)o);
			o=clientInputStream.readObject();
			while(o!=null)
			{
				list.add((Score)o);
				o=clientInputStream.readObject();
			}
			info.setList(list.toArray(new Score[0]));
			socket.shutdownInput();
		}
		catch (Exception e) 
		{
			log.error(e);
		}
		finally
		{
			if(socket!=null)
				try {socket.close();} catch (IOException e) {log.error(e);}
		}
		return info;
	}
}
