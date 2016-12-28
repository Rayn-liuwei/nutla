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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.util.Version;

/**
 * 例子
 * 该类为自定义查询例子类，必须实现Plugin接口
 * @author nianzai
 *
 */
public class ExampleQueryPlugin implements QueryPlugin 
{
	@Override
	/**
	 * 将自己要实现的查询功能在这实现
	 */
	public Query query(String keyword) throws Exception
	{
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
		QueryParser parser1 = new QueryParser(Version.LUCENE_30,"title", analyzer);
		QueryParser parser2 = new QueryParser(Version.LUCENE_30,"descs", analyzer);
		Query query1 = parser1.parse(keyword);
		query1.setBoost(1.2f); 
		Query query2 = parser2.parse(keyword);
		query2.setBoost(0.8f); 
		BooleanQuery bq = new BooleanQuery();
        bq.add(query1, BooleanClause.Occur.MUST);
        bq.add(query2, BooleanClause.Occur.SHOULD); 
		return bq;
	}

	@Override
	/**
	 * 实现自己的排序功能
	 */
	public Sort sort()
	{
		Sort sort=new Sort(new SortField("reviews", SortField.INT, true));
		return sort;
	}

	@Override
	/**
	 * 实现自己的filter功能
	 */
	public Filter filter() 
	{
		return null;
	}
}
