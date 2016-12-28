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

package nut.nianzai.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nut.nianzai.util.ZookeeperUtil;

public class StatusServlet extends HttpServlet
{
	private static final long serialVersionUID = -6647725702752355130L;

	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException 
	{
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		
		ResourceBundle resource =  ResourceBundle.getBundle("server");
		String zkhost=resource.getString("zkhost");
		int zktimeout=Integer.parseInt(resource.getString("zktimeout"));
		String path="/nutzk";
		ZookeeperUtil keep = new ZookeeperUtil();
		keep.connect(zkhost,zktimeout*1000);

		List<String> childs=keep.list(path);
		for (String child : childs) 
		{
			out.println(child+":运行中服务器<br/>");
			List<String> sgs=keep.list(path+"/"+child+"/run");
			for(String sg:sgs)
			{
				out.println(sg+"<br/>");
			}
			out.println(child+":备份中服务器<br/>");
			sgs=keep.list(path+"/"+child+"/bak");
			for(String sg:sgs)
			{
				out.println(sg+"<br/>");
			}
			out.println("<hr>");
		}
		keep.close();
		out.close();
	}

	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException 
	{
		doGet(request, response);
	}
}