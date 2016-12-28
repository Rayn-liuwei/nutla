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

import java.io.File;
import java.io.IOException;

import nut.nianzai.util.NutUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.Text;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;

public class ExampleIndexPlugin implements IndexPlugin 
{
	private static final Log log = LogFactory.getLog(ExampleIndexPlugin.class);
	
	private static Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
	
	@Override
	public void create(File file,Iterable<Text> values)
	{
		try
		{
			IndexWriter indexWriter = new IndexWriter(NIOFSDirectory.open(file),analyzer,true,IndexWriter.MaxFieldLength.UNLIMITED);
			indexWriter.setRAMBufferSizeMB(256);
			indexWriter.setUseCompoundFile(false);
			
		    for (Text str : values)
		    {
		    	Document doc = new Document();
			    String[] ss=str.toString().split("<<,>>");
				doc.add(new Field("id",ss[0], Field.Store.YES,Field.Index.NOT_ANALYZED));
				doc.add(new Field("title", ss[1], Field.Store.NO,Field.Index.ANALYZED));
				doc.add(new Field("descs", ss[2], Field.Store.NO,Field.Index.ANALYZED));
				doc.add(new Field("reviews",ss[3],Field.Store.YES,Field.Index.NOT_ANALYZED));
			    indexWriter.addDocument(doc);
		    }
		    indexWriter.optimize();
		    indexWriter.close();
		}
		catch(Exception e)
		{
			log.info(e);
		}
	}

	@Override
	public void merge(Directory[] dirs,Directory localWorkingDir) throws IOException 
	{
		IndexWriter writer = new IndexWriter(localWorkingDir, analyzer, true,IndexWriter.MaxFieldLength.UNLIMITED);
		writer.setRAMBufferSizeMB(256);
		writer.setUseCompoundFile(false);
		writer.addIndexesNoOptimize(dirs);
		writer.optimize();
		writer.close();
	}

	@Override
	public void delete(Directory dir, String ids)	throws IOException 
	{
		IndexWriter writer = new IndexWriter(dir,analyzer,false,IndexWriter.MaxFieldLength.UNLIMITED);
		writer.deleteDocuments(NutUtil.getTerms(ids));
		writer.close();
	}
}
