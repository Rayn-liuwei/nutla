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

package nut.nianzai;

import java.net.URI;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import nut.nianzai.util.ZookeeperUtil;

public class Nut 
{
	public static void main(String[] args) 
	{
		if(args[0].equals("zk"))
		{
			//cmd:Nut zk create 192.168.195.128:2181 /nutzk
			if(args[1].equals("create"))
			{
				ZookeeperUtil keep = new ZookeeperUtil();
				keep.connect(args[2],5*1000);
				keep.create(args[3]);
				keep.close();
				System.out.println("OK");
			}
			//cmd:Nut zk searchgroup 192.168.195.128:2181 sg1
			else if(args[1].equals("searchgroup"))
			{
				String path="/nutzk/"+args[3];
				ZookeeperUtil keep = new ZookeeperUtil();
				keep.connect(args[2],5*1000);
				keep.create(path);
				keep.create(path+"/run");
				keep.create(path+"lock");
				keep.create(path+"/bak");
				keep.close();
				System.out.println("OK");
			}
			//cmd:Nut zk list 192.168.195.128:2181 /nutzk
			else if(args[1].equals("list"))
			{
				ZookeeperUtil keep = new ZookeeperUtil();
				keep.connect(args[2],5*1000);
				List<String> childs=keep.list(args[3]);
				for (String child : childs) 
				{
					System.out.println(child);
				}
				keep.close();
			}
			//cmd:Nut zk delete 192.168.195.128:2181 /nutzk
			else if(args[1].equals("delete"))
			{
				ZookeeperUtil keep = new ZookeeperUtil();
				keep.connect(args[2],5*1000);
				keep.delete(args[3]);
				keep.close();
				System.out.println("OK");
			}
		}
		if(args[0].equals("copy"))
		{
			//cmd:Nut copy h2l hdfs://192.168.195.128:9000/user/nianzai/nutindex/0/ /home/nianzai/index/
			if(args[1].equals("h2l"))
			{
				try
				{
					Configuration conf = new Configuration(); 
					FileSystem fs=FileSystem.get(URI.create(args[2]),conf);
					fs.copyToLocalFile(new Path(args[2]), new Path(args[3]));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			//cmd:Nut copy l2h /home/nianzai/1.txt hdfs://192.168.195.128:9000/user/nianzai/input/
			else if(args[1].equals("l2h"))
			{
				try
				{
					Configuration conf = new Configuration(); 
					FileSystem fs=FileSystem.get(URI.create(args[3]),conf);
					fs.copyFromLocalFile(new Path(args[2]),new Path(args[3]));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
