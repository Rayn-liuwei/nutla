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

import java.io.IOException;
import java.util.ResourceBundle;

import nut.nianzai.plugin.IndexPlugin;
import nut.nianzai.util.NutUtil;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.lucene.store.Directory;

public class IndexDelete 
{
	private static IndexPlugin plugin;
	
	static
	{
		ResourceBundle resource =  ResourceBundle.getBundle("hadoop");
		plugin=(IndexPlugin)NutUtil.getObjectByClassName(resource.getString("indexplugin"));
	}
	
	public static void delete(Directory dir, String ids) throws IOException
	{
		plugin.delete(dir, ids);
	}
	
	public static void main(String[] args) throws IOException 
	{
		Configuration conf= new Configuration();
		FileSystem fs = FileSystem.get(conf);
		Directory dir=new FsDirectory(fs, new Path("index"), false, conf);
		delete(dir,"1001,1002");
	}
}
