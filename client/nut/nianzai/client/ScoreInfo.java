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

package nut.nianzai.client;

import java.io.Serializable;

import nut.nianzai.util.Score;

public class ScoreInfo implements Serializable 
{
	private static final long serialVersionUID = -8015933609608674356L;
	
	private int n;
	private Score[] list;
	
	public int getN() {
		return n;
	}
	public void setN(int n) {
		this.n = n;
	}
	public Score[] getList() {
		return list;
	}
	public void setList(Score[] list) {
		this.list = list;
	}
}
