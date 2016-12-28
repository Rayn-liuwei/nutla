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

import java.io.Serializable;

/**
 * 查询参数对象
 * @author nianzai
 *
 */
public class Parameter implements Serializable 
{
	private static final long serialVersionUID = 4566907265686140754L;
	
	private int no;//当前页
	private int ps;//页大小
	private int rscount;//记录总数
	private String query;//查询插件
	private String keyword;//关键词
	private int flag;//统计还是获得结果
	private String sortfield;//排序字段
	private int level;//搜索优先级  9搜索全部服务器
	
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public int getPs() {
		return ps;
	}
	public void setPs(int ps) {
		this.ps = ps;
	}
	public int getRscount() {
		return rscount;
	}
	public void setRscount(int rscount) {
		this.rscount = rscount;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public String getSortfield() {
		return sortfield;
	}
	public void setSortfield(String sortfield) {
		this.sortfield = sortfield;
	}
	
	public String toString()
	{
		StringBuilder sb=new StringBuilder();
		sb.append(query);
		sb.append(keyword);
		sb.append(no);
		if(sortfield!=null)
			sb.append(sortfield);
		return sb.toString();
	}
}
