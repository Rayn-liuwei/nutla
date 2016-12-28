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

package nut.nianzai.index;

import java.io.IOException;
import java.util.ResourceBundle;

import nut.nianzai.plugin.HBasePlugin;
import nut.nianzai.util.NutUtil;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class HBaseMapper 
{
	private static String inputpath;
	private static String outputpath;
	private static String tablename;
	private static HBasePlugin plugin;
	
	static
	{
		ResourceBundle resource =  ResourceBundle.getBundle("hbase");
		inputpath=resource.getString("hb_inputpath");
		outputpath=resource.getString("hb_outputpath");
		tablename=resource.getString("tablename");
		plugin=(HBasePlugin)NutUtil.getObjectByClassName(resource.getString("hbaseplugin"));
	}
	
	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException 
	{
		Configuration conf = new Configuration();
		conf.set("tablename", tablename);
		Job job = new Job(conf, "HBaseMapper");
		job.setNumReduceTasks(0);
		job.setJarByClass(HBaseMapper.class);
		job.setMapperClass(HBaseRSMapper.class);
		FileInputFormat.addInputPath(job, new Path(inputpath));
		Path outpath= new Path(outputpath);
		FileSystem fs = FileSystem.get(conf);
		if(fs.exists(outpath))
			fs.delete(outpath, true);
		FileOutputFormat.setOutputPath(job, outpath);
		System.exit(job.waitForCompletion(true)?0:1);
	}
	
	public static class HBaseRSMapper extends Mapper<LongWritable, Text, NullWritable, NullWritable>
	{
		public static HTable table = null;

		protected void setup(Context context) throws IOException
		{
			HBaseConfiguration conf = new HBaseConfiguration();
			String table_name = context.getConfiguration().get("tablename");
			if (table == null) {
				table = new HTable(conf, table_name);
			}
			table.setAutoFlush(false);
		}
		
		protected void cleanup(Context context) throws IOException
		{
			table.flushCommits();
		}
		
		public void map(LongWritable key, Text value, Context context) throws IOException
		{
			table.put(plugin.insert(value));
		}
	}
}
