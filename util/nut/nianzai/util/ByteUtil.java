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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

/**
 * 对象与字节缓存的转换工具
 * 该类来自于互联网
 *
 */
public class ByteUtil 
{
	/**
	 * 从字节转成对象
	 * @param bytes
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object getObject(byte[] bytes) throws IOException, ClassNotFoundException  
	{   
		ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
	    ObjectInputStream oi = new ObjectInputStream(bi);
	    Object obj = oi.readObject();
	    bi.close();
	    oi.close();
	    return obj;
	}
	             
	/**
	 * 将对象转成字节缓存
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	public static ByteBuffer getByteBuffer(Object obj) throws IOException 
	{   
		byte[] bytes = getBytes(obj);
	    ByteBuffer buff = ByteBuffer.wrap(bytes);
	    return buff;
    }
	
	/**
	 * 将对象转成字节
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	public static byte[] getBytes(Object obj) throws IOException   
	{   
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
	    ObjectOutputStream out = new ObjectOutputStream(bout);
	    out.writeObject(obj);
	    out.flush();
	    byte[] bytes = bout.toByteArray();
	    bout.close();
	    out.close();
        return bytes;
     } 
}