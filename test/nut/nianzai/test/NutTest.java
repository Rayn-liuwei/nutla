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

import java.util.List;

import nut.nianzai.cache.MemCached;
import nut.nianzai.cache.NutCache;
import nut.nianzai.client.NutClient;
import nut.nianzai.client.ZkCheck;
import nut.nianzai.db.HBaseDB;
import nut.nianzai.db.NutDB;
import nut.nianzai.util.Parameter;

/**
 * 例子
 * 先运行CreateIndex创建测试索引，再启动服务器端，最后运行该类进行搜索测试
 * @author nianzai
 *
 */
public class NutTest 
{
	//分布式搜索例子
	public static void main(String[] arg)
	{
		try
		{
			ZkCheck.start();
			Thread.sleep(8*1000);
			
			//构造查询对象
			Parameter parameter=new Parameter();
			parameter.setLevel(1);
			parameter.setQuery("basicQuery");
			parameter.setKeyword("soho");
			parameter.setNo(1);
			parameter.setPs(2);
			parameter.setSortfield("reviews");//如果实现了排序，指明排序字段，需要根据这个来对结果进行排序
			
			//只统计
			int n=NutClient.parallelTotal(parameter);
			System.out.println(n);
			
			//搜索
			NutDB db=new HBaseDB(House.class);
			NutCache cache=new MemCached();
			List<Object> ll=NutClient.parallelSearch(cache,db,parameter);
			System.out.println(parameter.getRscount());//返回搜索记录总数
			for(int i=0;i<ll.size();i++)
			{
				System.out.println(((House)ll.get(i)).getId());
				System.out.println(((House)ll.get(i)).getTitle());
				System.out.println(((House)ll.get(i)).getDescs());
				System.out.println(((House)ll.get(i)).getReviews());
			}
		}
		catch(Exception e)
		{e.printStackTrace();}
	}
}
