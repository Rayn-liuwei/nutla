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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.digest.DigestUtils;

import nut.nianzai.cache.NutCache;
import nut.nianzai.db.NutDB;
import nut.nianzai.util.Parameter;
import nut.nianzai.util.Score;
import nut.nianzai.util.NutUtil;

/**
 * 分布式搜索客户端
 * @author nianzai
 *
 */
public class NutClient 
{
	private static ExecutorService executor=null;
	public static Map<String,HashMap<String, Long>> hostlist=new HashMap<String,HashMap<String,Long>>();
	
	static
	{
		ResourceBundle resource =  ResourceBundle.getBundle("client");
		//服务线程数
		String threadpool=resource.getString("threadpool");
		String[] tp=threadpool.split(",");
		int minthread=Integer.parseInt(tp[0]);
		int maxthread=Integer.parseInt(tp[1]);
		int timeout=Integer.parseInt(tp[2]);
		int queue=Integer.parseInt(tp[3]);
		//启动工作线程
		executor = new ThreadPoolExecutor(minthread,maxthread,timeout,TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(queue),new ThreadPoolExecutor.CallerRunsPolicy());
	}
	
	public static HashMap<String, Long> randomMap()
	{
		List<HashMap<String, Long>> list = new ArrayList<HashMap<String, Long>>(hostlist.values());
		return list.get((int)(Math.random()*list.size()));
	}
	
	//向n台服务器并行搜索,得到各个服务器端发送回来的搜索结果，并对结果排序
	private static int parallelScore(List<Object> list,Parameter p) throws Exception
	{
		int temp=0;
		p.setFlag(1);
		List<Score[]> la=new ArrayList<Score[]>();
		HashMap<String, Long> map=randomMap();
		for(Map.Entry<String, Long> entry:map.entrySet())
		{
	    	String[] key = entry.getKey().split(":");
	    	{
	    		if(p.getLevel()>=Integer.parseInt(key[2]))
	    		{
		    		ScoreInfo info=requestInfo(key[0],Integer.parseInt(key[1]),p,executor);
		    		la.add(info.getList());
		    		temp=temp+info.getN();
	    		}
	    	}
		}
		//合并排序
		Score[] s=la.get(0);
		for(int i=1;i<la.size();i++)
		{
			if(la.get(i).length==0)
				continue;
			else
				s=NutUtil.mergesort(s, la.get(i),false);
		}
		//取出当前页结果
		Score[] news=new Score[p.getPs()];
		if(s.length>p.getPs())
		{
			System.arraycopy(s, 0, news, (p.getNo()-1)*p.getPs(), p.getNo()*p.getPs()>s.length?s.length:p.getNo()*p.getPs());
			list.addAll(Arrays.asList(news));
		}
		else
			list.addAll(Arrays.asList(s));
		return temp;
	}
	
	//并行搜索
	public static List<Object> parallelSearch(NutCache cache,NutDB db, Parameter p) throws Exception
	{
		List<Object> ll=new ArrayList<Object>();
		String key=DigestUtils.md5Hex(p.toString());
		Object value=cache.get(key);
		//如果缓存不存在则分布搜索，如果缓存存在则读取缓存
		if(value==null)
		{
			List<Object> list=new ArrayList<Object>();
			p.setRscount(parallelScore(list,p));
			if(p.getRscount()==0) return ll;
			String ids=p.getRscount()+",";
			for(int i=0;i<list.size();i++)
			{
				Score info=(Score)list.get(i);
				ll.add(db.get(info.getId()));
				ids=ids+info.getId()+",";
			}
			ids=ids.substring(0,ids.length()-1);
			cache.add(key, ids);
		}
		else
		{
			String[] ss=((String)value).split(",");
			p.setRscount(Integer.parseInt(ss[0]));
			if(p.getRscount()==0) return ll;
			for(int i=1;i<ss.length;i++)
			{
				ll.add(db.get(Integer.parseInt(ss[i])));
			}
		}
		return ll;
	}
	
	//统计
	public static int parallelTotal(Parameter p) throws Exception
	{
		int temp=0;
		p.setFlag(0);
		List<Integer> la=new ArrayList<Integer>();
		HashMap<String, Long> map=randomMap();
		for(Map.Entry<String, Long> entry:map.entrySet())
		{
	    	String[] key = entry.getKey().split(":");
	    	la.add(requestTotal(key[0],Integer.parseInt(key[1]),p,executor));
		}
		for(int i=0;i<la.size();i++)
		{
			temp=temp+la.get(i);
		}
		return temp;
	}
	
	//提交查询请求，得到得分信息
	public static ScoreInfo requestInfo(String ip,int port,Parameter parameter,ExecutorService executor) throws InterruptedException, ExecutionException
	{
		Callable<ScoreInfo> infocall=new InfoCallable(ip,port,parameter);
		Future<ScoreInfo> task = executor.submit(infocall);
		return task.get();
	}
	
	//请求查询条件的记录总数
	public static Integer requestTotal(String ip,int port,Parameter parameter,ExecutorService executor) throws InterruptedException, ExecutionException
	{
		Callable<Integer> call=new TotalCallable(ip,port,parameter);
		Future<Integer> task = executor.submit(call);
		return task.get();
	}
}