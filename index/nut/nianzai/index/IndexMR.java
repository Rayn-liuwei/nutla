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

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.ResourceBundle;

import nut.nianzai.plugin.IndexPlugin;
import nut.nianzai.util.NutUtil;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * Mapper/Reducer 建立索引
 * @author nianzai
 *
 */
public class IndexMR
{
	private static String inputpath;
	private static String outputpath;
	private static int indexcount;
	private static String temppath;
	private static String nutindex;
	private static IndexPlugin plugin;
	
	static
	{
		ResourceBundle resource =  ResourceBundle.getBundle("hadoop");
		inputpath=resource.getString("inputpath");
		outputpath=resource.getString("outputpath");
		indexcount=Integer.parseInt(resource.getString("indexcount"));
		temppath=resource.getString("temppath");
		nutindex=resource.getString("nutindex");
		
		plugin=(IndexPlugin)NutUtil.getObjectByClassName(resource.getString("indexplugin"));
	}
	
	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException
	{
		Configuration conf= new Configuration();
		Job job = new Job(conf, "Indexer");
		job.setJarByClass(IndexMR.class);
		FileInputFormat.addInputPath(job, new Path(inputpath)); 
		Path outpath= new Path(outputpath);
		FileSystem fs = FileSystem.get(conf);
		if(fs.exists(outpath))
			fs.delete(outpath, true);
		FileOutputFormat.setOutputPath(job, outpath); 

		job.setMapperClass(RecordMapper.class);
		job.setReducerClass(RecordReducer.class);
		System.exit(job.waitForCompletion(true)?0:1);
	}
	
	public static class RecordMapper extends Mapper<LongWritable, Text, LongWritable, Text>
	{
		private long count=0;
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
		{
			count++;
			context.write(new LongWritable(count/indexcount), value);
		}
	}
	
	public static class RecordReducer extends Reducer<LongWritable, Text, LongWritable, Text>
	{
		public void reduce(LongWritable key, Iterable<Text> values,	final Context context) throws IOException, InterruptedException
		{
		    Thread t = new Thread() 
		    {
		    	public boolean stop = false;
		    	
		        public void run()
		        {
		        	while (!stop)
		        	{
		        		context.progress();
		        		try 
		        		{
		        			sleep(10000);
		        		}
		        		catch (InterruptedException e) 
		        		{
		        			stop = true;
		        		}
		        	}
		        }
		    };
		   	t.start();
		   	
			File file = new File(temppath,key.toString()+"/"+System.currentTimeMillis()+ "-" + new Random().nextInt());
			context.setStatus("Start adding documents.");
			plugin.create(file, values);
		   	context.setStatus("Done adding documents.");
		    FileSystem fs = FileSystem.get(context.getConfiguration());
		    context.setStatus("Starting copy to final destination...");
		    fs.copyFromLocalFile(new Path(file.getAbsolutePath()), new Path(nutindex));
		    context.setStatus("Copy to final destination done!");
		    context.setStatus("Deleting tmp files...");
		    FileUtil.fullyDelete(file);
		    context.setStatus("Deleting tmp files done!");
		    t.interrupt();
		}
	}
}
