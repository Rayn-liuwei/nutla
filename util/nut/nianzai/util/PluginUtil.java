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

import java.util.Enumeration;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import nut.nianzai.plugin.QueryPlugin;

/**
 * 将查询插件配置文件plugin.properties读入内容解析
 * @author nianzai
 *
 */
public class PluginUtil 
{
	private static Map<String,QueryPlugin> map=new TreeMap<String,QueryPlugin>();
	
	static
	{
		ResourceBundle resource =  ResourceBundle.getBundle("plugin");
		Enumeration<String> enumplugin =resource.getKeys();
		while(enumplugin.hasMoreElements()) 
		{
			String key = enumplugin.nextElement().trim();
			String value = resource.getString(key).trim();
			map.put(key, (QueryPlugin)NutUtil.getObjectByClassName(value));
		}
	}
	
	public static QueryPlugin getValueByKey(String key)
	{
		return map.get(key);
	}
}
