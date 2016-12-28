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

import nut.nianzai.index.FsDirectory;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.lucene.index.IndexReader;

class LuceneHDFSTest 
{
	public static void main(String[] args) throws IOException 
	{
		Configuration conf = new Configuration();   
	    FileSystem fs = FileSystem.get(conf);
	    FsDirectory dir=new FsDirectory(fs, new Path("nutindex/0/"), false, conf);
	    IndexReader reader = IndexReader.open(dir);
	    System.out.println(reader.numDocs());
	    for(int i=0;i<reader.numDocs();i++)
	    {
	    	System.out.println(reader.document(i).get("id"));
	    }
	    reader.close();
   }
}
