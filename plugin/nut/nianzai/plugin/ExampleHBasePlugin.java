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

package nut.nianzai.plugin;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;

public class ExampleHBasePlugin implements HBasePlugin 
{
	@Override
	public Put insert(Text value) 
	{
		String[] ss=value.toString().split("<<,>>");
		Put put = new Put(Bytes.toBytes(Integer.parseInt(ss[0])));
		put.add(Bytes.toBytes("title"), Bytes.toBytes(""), Bytes.toBytes(ss[1]));
		put.add(Bytes.toBytes("descs"), Bytes.toBytes(""), Bytes.toBytes(ss[2]));
		put.add(Bytes.toBytes("reviews"), Bytes.toBytes(""), Bytes.toBytes(ss[3]));
		return put;
	}
}
