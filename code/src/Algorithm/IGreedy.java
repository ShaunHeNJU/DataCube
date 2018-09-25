package Algorithm;

import java.util.List;

/*
    author: Shaun He
    date  : 2018-09-12
    email : 709278724@qq.com  
 */

public interface IGreedy {

    // 给点定节点获取此节点的后代节点
    public List<Node> getUnselectedDescendants(Node node, List<Node> selected);

    // 给定节点查询该节点的中已被选择的最小值的节点
    public Node getMinSelectedAncestor(Node node,List<Node> selected);
}
