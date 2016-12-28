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

package nut.nianzai.util;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.lucene.index.Term;

public class NutUtil 
{
	private static final Log log = LogFactory.getLog(NutUtil.class);
	
	public static Map<Class<?>,HashMap<String,Method>> modelMap=new HashMap<Class<?>,HashMap<String,Method>>(); 
	
	//类属性，方法以反射的方式缓存
	public static void getFields(Class<?> cls)
	{
		if(modelMap.containsKey(cls))
			return;
		try
		{
			HashMap<String,Method> t=new HashMap<String,Method>();
	        Field fields[]=cls.getDeclaredFields();
	        for (int i=0;i<fields.length;i++) 
	        {
	            Field field=fields[i];
	            String fieldName=field.getName();
	            String firstLetter=fieldName.substring(0,1).toUpperCase();
	            String setMethodName = "set" + firstLetter + fieldName.substring(1);
	            Method setMethod = cls.getMethod(setMethodName, new Class[] { field.getType() });
	            t.put(fieldName, setMethod);
	        }
	        modelMap.put(cls, t);
		}
		catch(Exception e)
		{
			log.info(e);
		}
	}
	
	//由类名生成类实例
	public static Object getObjectByClassName(String classname)
	{
		Object o=null;
		try 
		{
			o= Class.forName(classname).newInstance();
		} 
		catch (Exception e) 
		{
			log.info("由类名"+classname+"生成类实例出错："+e);
		} 
		return o;
	}
	

	//判断str是否为空，如果str是null返回空，如果str是""返回空
	public static boolean isEmpty(String str)
	{
		boolean temp=false;
		if(null==str) temp=true;
		if("".equals(str)) temp=true;
		return temp;
	}
	
	//当前时间数值
    public static long now()
    {
        GregorianCalendar calenda = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return Long.parseLong(sdf.format(calenda.getTime()));
    }
    
    //数组合并
    public static byte[] ArrayCoalition(byte[] a,byte[] b)
    {
    	  byte[] temp=new byte[a.length+b.length];  
    	  System.arraycopy(a,0,temp,0,a.length);  
    	  System.arraycopy(b,0,temp,a.length,b.length);
    	  return temp;
    }
    
    //合并排序
    public static Score[] mergesort(Score[] s1,Score[] s2,boolean reverse)
    {
    	Score[] s3 =new Score[s1.length+s2.length];
    	int i=0,j=0,k=0;
    	while(i<s1.length && j<s2.length) 
    	{
    		if(reverse?s1[i].getScore()<s2[j].getScore():s1[i].getScore()>s2[j].getScore())
    			s3[k++]=s1[i++];
    		else
    			s3[k++]=s2[j++];
    	}
    	while(i<s1.length)
    		s3[k++]=s1[i++];
    	while(j<s2.length)
    		s3[k++]=s2[j++];
    	return s3;
    }
    
	public static Term[] getTerms(String ids) 
	{
		ArrayList<Term> list = new ArrayList<Term>();
		String[] cc = ids.split(",");
		for (int i = 0; i < cc.length; i++) 
		{
			Term term = new Term("id", cc[i]);
			list.add(term);
		}
		return (Term[]) list.toArray(new Term[0]);
	}
	
	public static Term getTerm(String id) 
	{
		Term term = new Term("id", id);
		return term;
	}
	
	//在hdfs目录dir中查找prefix字符的文件名
	public static String findFileNameByHDFS(String dir,String prefix)
	{
		String temp="";
		try
		{
			Configuration conf = new Configuration(); 
			FileSystem fs=FileSystem.get(URI.create(dir),conf);
			FileStatus fileList[] = fs.listStatus(new Path(dir));
			for(int i = 0; i < fileList.length; i++){
				if(fileList[i].getPath().getName().indexOf(prefix)>-1)
					temp=fileList[i].getPath().getName();
			}
		}
		catch(Exception e)
		{
			log.info(e);
		}
		return temp;
	}
	
	//在本地目录dir中查找prefix字符的文件名
	public static String findFileNameByLocal(String dir,String prefix)
	{
		String temp="";
		File d=new File(dir);//建立当前目录中文件的File对象
		File[] list=d.listFiles();//取得代表目录中所有文件的File对象数组
		for(int i=0;i<list.length;i++)
		{
			if(list[i].getName().indexOf(prefix)>-1)
				temp=list[i].getName();
		}
		return temp;
	}
}
