# 高效创建Data Cube的算法与实现

一、算法框架

    S = {top view}
    for i=1 to k do begin
		select that view v not in S such that B(v, S) is maximized
		S = S uniou {v}
二、算法介绍
	
   算法的目标是通过选取物化一些视图，在时间空间上做权衡。既没有向物化全部视图浪费空间，又没有想只物化base cube查询时间慢的问题。
   算法分为两个步骤：
	
1）通过抽样估计不同属性取值的个数：
   通过卡方检验属性取值的分布进行检验，如果每个属性的取值个数大致在一个范围则认定为无偏的分布，如果属性的取值在一些属性上比较多，在另一些上面取值较少，则认为是有偏的分布。
	
（1）对于无偏的分布，采用sjack估计方法

（2）对于有偏的分布，采用shloosser估计方法


2）选取带来最大利益的视图

   cube之间是具有层级关系的，处于上层的cube可以用来回答下层cube查询。因此如果物化cube,会减少下一层及与此相关cube的查询代价。通过选取每一次能够物化视图后减少最大查询代价的视图。

三、代码介绍
   该项目分为两块

1）选取最大利益视图的实现使用Java实现，数据结构是Node类:

	  data:视图的行数
      name:视图的名称
      children:与此视图直接相关的下一层级视图
      parents:与此视图直接相关的上一层级视图

  该算法的入口是selectNode(Node[] lattice, Node start, int count)

	lattice:cube的集合
    start:指定basecube
    count:指定要选取物化的视图的个数（不包含start,因为start是base cube是必须物化的视图）

  算法的返回值是一个有序的Node集合，顺序越靠前，代表选取物化该cube减少的查询代价越大。

2）估计属性取不同值的个数，此模块使用了python实现（主要是python在科学计算方面表现的十分出色）

  该算法的入口是estimation(n, N, d, f, attributes)
  
  	n:采样的样本数量
    N:总的记录数
    d:属性取不同值的个数
    f:是一个映射函数，属性值出现个数的统计
	attributes (list) : 每个属性值出现的个数

  算法返回值是一个代表估计出来的不同属性取值的个数数字