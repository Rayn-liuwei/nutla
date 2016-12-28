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

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import nut.nianzai.util.NutUtil;
import nut.nianzai.util.SearchUtil;

/**
 * HDFS索引与本地索引同步
 * @author nianzai
 *
 */
public class IndexSyncServer 
{
	private static final Log log = LogFactory.getLog(IndexSyncServer.class);
	
	public static void start()
	{
		final ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(1);
		scheduled.scheduleAtFixedRate(new Runnable() 
		{
			public void run() 
			{
				String indexpath=SearchUtil.getIndexpath();
				String hdfsindexpath=SearchUtil.getHDFSindexpath();
		
				String hdfs= NutUtil.findFileNameByHDFS(hdfsindexpath, "segments_");
				String local=NutUtil.findFileNameByLocal(indexpath, "segments_");
				if(!hdfs.equals(local))
				{
					String del=NutUtil.findFileNameByHDFS(hdfsindexpath, ".del");
					try
					{
						Configuration conf = new Configuration(); 
						FileSystem fs=FileSystem.get(URI.create(del),conf);
						fs.copyToLocalFile(new Path(del), new Path(indexpath));//从hdfs索引目录复制删除文件到本地索引
						fs=FileSystem.get(URI.create(hdfs),conf);
						fs.copyToLocalFile(new Path(hdfs), new Path(indexpath));//从hdfs索引目录复制段文件到本地索引
						fs=FileSystem.get(URI.create(hdfsindexpath+"segments.gen"),conf);
						fs.copyToLocalFile(new Path(del), new Path(indexpath));//从hdfs索引目录复制segments.gen文件到本地索引
						
						SearchUtil.reopen();
					}
					catch(Exception e)
					{
						log.info(e);
					}
				}
			}
		}, 0, 5,TimeUnit.MINUTES);
	}
}
