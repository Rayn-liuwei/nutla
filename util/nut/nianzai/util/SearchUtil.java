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
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;


import nut.nianzai.plugin.QueryPlugin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.RAMDirectory;

public class SearchUtil 
{
	private static final Log log = LogFactory.getLog(SearchUtil.class);
	
	private static Searcher searcher = null;
	private static String _indexpath;
	private static String _hdfsindexpath;
	private static String indexmode="";

	public static String getIndexpath()
	{
		return _indexpath;
	}
	
	public static void setIndexpath(String indexpath)
	{
		_indexpath=indexpath;
	}
	
	public static String getHDFSindexpath()
	{
		return _hdfsindexpath;
	}
	
	public static void setHDFSindexpath(String hdfsindexpath)
	{
		_hdfsindexpath=hdfsindexpath;
	}
	
	static 
	{
		try 
		{
			ResourceBundle resource = ResourceBundle.getBundle("server");
			indexmode = resource.getString("indexmode");
			//索引路径
			_indexpath = resource.getString("indexpath");
			_hdfsindexpath = resource.getString("hdfsindexpath");
			String[] path=_indexpath.split(";");
			if(indexmode.equals("0"))
				searcher = new IndexSearcher(NIOFSDirectory.open(new File(path[1])));
			else if(indexmode.equals("1"))
				searcher = new IndexSearcher(new RAMDirectory(NIOFSDirectory.open(new File(path[1]))));
			log.info("index:" + path[1] + " max docs:" + searcher.maxDoc());
		}
		catch (IOException e)
		{
			log.error(e);
		}
	}
	
	public static void reopen()
	{
		Searcher newsearcher=null;
		Searcher temp=null;
		try
		{
			if(indexmode.equals("0"))
				newsearcher = new IndexSearcher(NIOFSDirectory.open(new File(_indexpath)));
			else if(indexmode.equals("1"))
				newsearcher = new IndexSearcher(new RAMDirectory(NIOFSDirectory.open(new File(_indexpath))));
			log.info("index:" + _indexpath + " max docs:" + newsearcher.maxDoc());
			temp=searcher;
			searcher=newsearcher;
			temp.close();//关闭旧索引
		}
		catch (IOException e)
		{
			log.error("reopen"+e);
		}
	}
	
	public static int search(List<Score> list,Query query,Filter filter,Sort sort,int n,String sortfield) throws IOException 
	{
		final int MAX_DOC = 2000; // 最多2000个
		int e = Math.min(MAX_DOC, n);
		TopDocs topDocs = null;
		if(sort!=null)
			topDocs=searcher.search(query,filter,e,sort);
		else
			topDocs=searcher.search(query,filter,e);
		e = Math.min(topDocs.totalHits, e);
		for (int i = 0; i < e; i++) 
		{
			Score score = new Score();
			score.setId(Integer.parseInt(searcher.doc(topDocs.scoreDocs[i].doc).get("id")));
			if(NutUtil.isEmpty(sortfield))
				score.setScore(topDocs.scoreDocs[i].score);
			else
				score.setScore(Float.parseFloat(searcher.doc(topDocs.scoreDocs[i].doc).get(sortfield)));
			list.add(score);
		}
		return topDocs.totalHits;
	}

	public static int total(QueryPlugin plugin,Parameter p) throws Exception
	{
		TopDocs topDocs=searcher.search(plugin.query(p.getKeyword()),1);
		return topDocs.totalHits;
	}
	
	public static int search(QueryPlugin plugin,List<Score> list,Parameter p)
	{
		int n=0;
		try 
		{
			n= search(list,plugin.query(p.getKeyword()),plugin.filter(),plugin.sort(),p.getNo()*p.getPs(),p.getSortfield());
		}
		catch (Exception e) {
			log.info(e);
		}
		return n;
	}
}
