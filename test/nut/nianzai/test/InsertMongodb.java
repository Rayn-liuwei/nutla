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

package nut.nianzai.test;

import nut.nianzai.db.MongoDB;

/**
 * 例子
 * 建立测试索引
 * @author nianzai
 *
 */
public class InsertMongodb 
{
	public static void main(String[] args)
	{
		House h1=new House();
		House h2=new House();
		House h3=new House();
		House h4=new House();
		House h5=new House();
		House h6=new House();
		
		h1.setId(1001);
		h1.setTitle("建外SOHO");
		h1.setDescs("建外SOHO位于长安街上，国贸中心正对面，是北京的商业心脏。建外SOHO总占地面积为12.28公顷（东西长约760米），总建筑面积约为70万平米，地下建筑面积为19万平米，地上建筑面积约51万平米，由18栋公寓、2栋写字楼、4栋SOHO小型办公房及大量裙房组成。第一大道写字楼：南北两座，办公面积98000平米，楼高28层。整栋楼通透明亮，简约现代。SOHO公寓楼：2727米的体量，体型细长，白天透明，夜晚发亮。SOHO公寓楼13层为临街商铺，4层以上为SOHO公寓单元，18栋楼高低错落，异常生动。SOHO商铺：全部临街，1、2层铺面复式结构自成一体，3层铺面由滚梯直达公共院落，出入便利。中心花");
		h1.setReviews("1");
		
		h2.setId(1002);
		h2.setTitle("百环家园");
		h2.setDescs("概况“百环家园”是北京百环房地产开发有限责任公司拟在北京人民机器厂现址内开发的一处经济适用房项目，用地18.2公顷。由于该公司获取此地的费用高达9亿元，而经济适用房政府定价不超过4000元，根据其经济测算，按照原控规，很难实现经济平衡，以致影响此地区的开发。因此，百环公司委托北京市建筑设计研究院对该地块的控规进行调整，并论证其合理性，以利项目的顺利实施。区位及周边概况该地区西邻东三环，向北京市2公里就是国贸和CBD中心，南侧就是华腾园小区，沿三环路向南，则有京瑞大厦、御景园等高层住宅小区。“百环家园”用地现在是北人集团下属企业－北京人民机器厂。该用地北侧为三幢建紧邻广渠路的建筑，分别为13层板");
		h2.setReviews("2");
		
		h3.setId(1003);
		h3.setTitle("万达广场");
		h3.setDescs("北京万达广场整个项目是50万平方米规模，在开发的程序我们分为北区和南区，北区已经落成投入使用，建筑面积在30万平方米，南区分为A、B、C三座，其中A座2006年3月正式开盘，2006年年底入住。二期B座由德国GMP国际建筑设计有限公司担纲设计，可谓北京CBD核心区的新地标级建筑，二期开发商持有，于2007年4月入住。二期C座为酒店。二期总建筑面积20万平方米，其中二期写字楼建筑面积3万平方米。“三塔两座”商务综合体坐北朝南，紧邻长安街。二期由雕塑感的塔楼组成，其建筑以德国理性主义设计理念为主导，关注细部节点及功能上的应用，建筑完成后，将成为北京CBD林立的办公楼座中又一独特风景。B座就是中间只");
		h3.setReviews("3");
		
		h4.setId(1004);
		h4.setTitle("SOHO现代城");
		h4.setDescs("现代城位于东长安街延长线，国贸东800米，北京商务中心区内（CBD）内。 如果以天安门为中心坐标，与西部的军事博物馆相对应的东部位置就是现代城。随着地铁复八线的建设、国贸桥的改造、通惠河的治理和东四环的开通，现代城所处的商务中心区已成为北京最现代、最繁华，最具人气的地区。 从现代城出发，无论去哪儿都非常方便。因为它东南西北，地下水上皆四通八达。地铁网串起黄金锁链：有地下长安街之称的复八线地铁在现代城设有出口。 复八线地铁象一条黄金锁链串起了现代城国贸贵友大厦赛特中心恒基中心东方广场天安门西单等繁华的黄金地点。 地面交通网纵横天下：西面有东三环，东面是东四环，北面有建国路，南面是滨河快速路，它连");
		h4.setReviews("4");
		
		h5.setId(1005);
		h5.setTitle("美景东方");
		h5.setDescs("铂金区位在北京突飞猛进般超高速城市化的语境下，东三环像一条钻链闪耀夺目光芒。在东三环置业的机会已经濒临灭绝，珍稀地段，未来三年内难以复制。优品.国际公寓，紧靠东三环，华威桥东500米，距CBD核心仅3.5公里。区位确立高端价值先天血统，潜力无限。地铁贯通松榆南路横铺门前，东西依次贯通东二环、东三环、西大望路及东四环等主要道路，交通路网四通八达。公交线路密集，可以便捷到达城市的各个角落。29、53路直通王府井和北京西站；300、801任意畅游在三环；752通往亚运村；983直达通州；974、680通往亦庄；还有 34、822、742、985、649、752、28、30、352、368等众多线路");
		h5.setReviews("5");
		
		h6.setId(1006);
		h6.setTitle("蓝堡国际公寓");
		h6.setDescs("项目规划项目用地呈南北长235.7米，东西宽169.8米的长方形，总用地为3.15公顷，分南、北两块。总建筑面积、使用率、容积率、面积及户型分布居住面积114886平米，总居住户数832户。主力户型：70120平方米的小户型，160250平方米的大户型。计入销售面积的分摊公用部分说明：严格遵守国家有关规定。入住时间首期将于2003年6月25日底之前竣工，有望在CBD商圈内的同期开发项目中第一个完工，并交付客户使用。房屋层高及净高北区：层高3.10米，净高2.85米；南区：层高3.05米，净高2.8米；有吊顶处不低于2.4米。社区交通区内道路人车分流，互不交叉，户不干扰。特有残疾人专用通道，体现");
		h6.setReviews("6");
		
		MongoDB db=new MongoDB(House.class);
		db.empty();
		db.insert(h1);db.insert(h2);db.insert(h3);db.insert(h4);db.insert(h5);db.insert(h6);
	}
}
