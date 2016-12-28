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

package nut.nianzai.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import nut.nianzai.plugin.IndexPlugin;
import nut.nianzai.util.NutUtil;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.FileAlreadyExistsException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * 将小索引合并成大索引
 * IndexMerger outputpath localpath inputpath...
 * 例子:IndexMerger index /home/nianzai/work nutindex/0 nutindex/1
 * @author nianzai
 *
 */
public class IndexMerger 
{
	public static void main(String[] args) throws IOException  
	{
		ResourceBundle resource =  ResourceBundle.getBundle("hadoop");
		IndexPlugin plugin=(IndexPlugin)NutUtil.getObjectByClassName(resource.getString("indexplugin"));
		Configuration conf= new Configuration();
	    //输出目录
	    Path outputIndex = new Path(args[0]);
		FileSystem fs = FileSystem.get(conf);
		if (fs.exists(outputIndex)) 
		{
			throw new FileAlreadyExistsException("Output directory " + outputIndex + " already exists!");
		}
		//本地工作目录
	    Path workDir = new Path(args[1]);
		FileSystem localFs = FileSystem.getLocal(conf);  
		if (localFs.exists(workDir)) 
		{
			localFs.delete(workDir, true);
		}
		localFs.mkdirs(workDir);
		//索引目录
		List<Directory> indexDirs = new ArrayList<Directory>();
		for (int i=2;i<args.length;i++) 
		{
			indexDirs.add(new FsDirectory(fs, new Path(args[i]), false, conf));
		}
		Directory[] indexFiles = (Directory[])indexDirs.toArray(new Directory[indexDirs.size()]);
	    //开始合并索引
		System.out.println("Start...");
		plugin.merge(indexFiles, FSDirectory.open(new File(workDir.toString())));
		System.out.println("Done!");
		fs.completeLocalOutput(outputIndex, workDir);
	    FileSystem.getLocal(conf).delete(workDir, true);
	}
}
