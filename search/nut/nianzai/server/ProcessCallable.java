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

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nut.nianzai.util.ByteUtil;
import nut.nianzai.util.Parameter;
import nut.nianzai.util.PluginUtil;
import nut.nianzai.util.Score;
import nut.nianzai.util.SearchUtil;

public class ProcessCallable implements Callable<byte[]>
{
	private static final Log log = LogFactory.getLog(ProcessCallable.class);
	
	private byte[] bytes;
	
	public ProcessCallable(byte[] bytes)
	{
		this.bytes=bytes;
	}

	@Override
	public byte[] call()
	{
		ByteArrayOutputStream buf=new ByteArrayOutputStream();
		try
		{
			int n=0;
			List<Score> list=new ArrayList<Score>();
			
			Parameter p=(Parameter)ByteUtil.getObject(bytes);//接收搜索条件
			
			if(p.getFlag()==1)
			{
				n=SearchUtil.search(PluginUtil.getValueByKey(p.getQuery()),list,p);
			}
			else if(p.getFlag()==0)
			{
				n=SearchUtil.total(PluginUtil.getValueByKey(p.getQuery()),p);
			}
			
			ObjectOutputStream o=new ObjectOutputStream(buf);
			o.writeObject(n);
			for(int i=0;i<list.size();i++)
			{
				o.writeObject(list.get(i));
			}
			//以null对象作为结束
			o.writeObject(null);
			o.flush();
		}
		catch(Exception e)
		{
			log.info(e);
		}
		return buf.toByteArray();
	}
}
