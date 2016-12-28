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

package nut.nianzai.test;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HDFSUtil 
{
	public static void main(String[] args)  throws IOException,InterruptedException, ClassNotFoundException 
	{
		Configuration conf = new Configuration(); 
		HDFSUtil.putToHdfs("F:/eclipse3.6.1/workspace/nut/1.txt", "input", conf);
		//HDFSUtil.fromHdfs("hdfs://192.168.195.128:9000/user/nianzai/nutindex/0/","c:/index3/",conf);
		//HDFSUtil.renameHdfs("input/ExistedRecord.txt", "input/1.txt", conf);
		//HDFSUtil.checkAndDelete("input/", conf, true);
		//HDFSUtil.createFileHdfs("input/1.txt", conf, "zhongguo 中国  abc");
		//HDFSUtil.mkdir("input/dd", conf);
		//System.out.println(HDFSUtil.isExists("input/1.txt", conf));
	}
	
	//本地文件copy到HDFS
	public static void putToHdfs(String src, String dst, Configuration conf) throws IOException
	{
		Path srcPath = new Path(src);
		Path dstPath = new Path(dst);
		FileSystem hdfs = FileSystem.get(conf);
		hdfs.copyFromLocalFile(srcPath, dstPath);
	}
	
	//将文件从HDFS copy 到本地
	public static void fromHdfs(String src, String dst, Configuration conf) throws IOException
	{
		Path srcPath = new Path(src);
		Path dstPath = new Path(dst);
		FileSystem hdfs = FileSystem.get(conf);
		hdfs.copyToLocalFile(srcPath, dstPath);
	}
	
	//将HDFS上文件重新命名
	public static boolean renameHdfs(String src, String dst, Configuration conf) throws IOException
	{
		FileSystem hdfs = FileSystem.get(conf);
		Path srcPath = new Path(src);
		Path dstPath = new Path(dst);
		return hdfs.rename(srcPath, dstPath);
	}

	//在HDFS上新建一个文件
	public static void createFileHdfs(String fileName,Configuration conf,String content) throws IOException
	{
		FileSystem hdfs = FileSystem.get(conf);
		Path path = new Path(fileName);
		FSDataOutputStream os = hdfs.create(path,true);
		os.writeUTF(content);
		os.close();
	}
	
	//在HDFS上新建目录
	public static boolean mkdir(String dirname,Configuration conf) throws IOException
	{
		FileSystem hdfs = FileSystem.get(conf);
		Path path = new Path(dirname);
		return hdfs.mkdirs(path);
	}
	
	//删除HDFS上文件 isRecursive 是否递归
	public static boolean checkAndDelete(String path, Configuration conf,boolean isRecursive) throws IOException
	{
		Path dst_path = new Path(path);
		FileSystem hdfs = FileSystem.get(conf);
		if (hdfs.exists(dst_path)) 
			return hdfs.delete(dst_path,isRecursive);
		else
			return false;
	}
	
	//判断HDFS上文件是否存在
	public static boolean isExists(String fileName,Configuration conf) throws IOException
	{
		FileSystem hdfs = FileSystem.get(conf);
		Path path = new Path(fileName);
		return hdfs.exists(path);
	}
}
