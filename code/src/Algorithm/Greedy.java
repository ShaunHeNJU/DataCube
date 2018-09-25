package Algorithm;

import java.util.ArrayList;
import java.util.List;

/*
    author: Shaun He
    date  : 2018-09-12
    email : 709278724@qq.com
 */

public class Greedy implements IGreedy {

    @Override
    public List<Node> getUnselectedDescendants(Node node, List<Node> selected) {
        List<Node> descendants = new ArrayList<>();
        List<Node> children = node.getChildren();
        for (Node child : children) {
            if (!selected.contains(child)) {
                descendants.add(child);
                List<Node> childDescendants = getUnselectedDescendants(child, selected);
                descendants.removeAll(childDescendants);
                descendants.addAll(childDescendants);
            }
        }
        return descendants;
    }

    @Override
    public Node getMinSelectedAncestor(Node node, List<Node> selected) {
        Node minNode = null;
        return getMinNode(node, selected, minNode);
    }

    public Node getMinNode(Node node, List<Node> selected, Node minNode) {
        List<Node> parents = node.getParents();
        for (Node parent : parents ) {
            if (selected.contains(parent) && (minNode == null || minNode.getData() > parent.getData())) {
                minNode = parent;
            }
            minNode = getMinNode(parent, selected, minNode);
        }
        return minNode;
    }

    // 计算选取每个节点效益值
    public long benefit(Node node, List<Node> selected) {
        Node minSelectedAncestor = getMinSelectedAncestor(node,selected);
        long cost = minSelectedAncestor.getData() - node.getData();
        List<Node> descendants = getUnselectedDescendants(node, selected);
        int count = 1;
        long compensate = 0;
        for (Node descendant : descendants) {
            Node descendantMinSelectedAncestor = getMinSelectedAncestor(descendant,selected);
            if (descendantMinSelectedAncestor.getData() >= node.getData()) {
                if(descendantMinSelectedAncestor.getData() >= minSelectedAncestor.getData()) {
                    count++;
                } else {
                    compensate += descendantMinSelectedAncestor.getData() - node.getData();
                }
            } else {
                List<Node> minSelectedAncestorDescendants = getUnselectedDescendants(descendantMinSelectedAncestor, selected);
                descendants.removeAll(minSelectedAncestorDescendants);
                count += descendants.size();
                if (descendants.size() == 0) {
                    break;
                }
            }
        }
        return count * cost + compensate;
    }

    public List<Node> selectNode(Node[] lattice, Node start, int count) {
        List<Node> selected = new ArrayList<>();
        selected.add(start);

        for (int k = 0; k < count; k++) {
            long[] benefits = new long[lattice.length-1];
            int max_index = 1;

            for (int i = 1; i < lattice.length; i++) {
                if (!selected.contains(lattice[i])) {
                    benefits[i-1] = benefit(lattice[i], selected);
                }

                if (benefits[i-1] > benefits[max_index]) {
                    max_index = i-1;
                }
            }

            if (!selected.contains(lattice[max_index+1])) {
                selected.add(lattice[max_index+1]);
            }
        }
        selected.remove(start);
        return selected;
    }

    public static void main(String[] args) {
        Node a = new Node("a",100);
        Node b = new Node("b",50);
        Node c = new Node("c",75);
        Node d = new Node("d",20);
        Node e = new Node("e",30);
        Node f = new Node("f",40);
        Node g = new Node("g",1);
        Node h = new Node("h",10);

        a.addChild(b);
        a.addChild(c);

        b.addChild(d);
        b.addChild(e);
        b.addParent(a);

        c.addChild(e);
        c.addChild(f);
        c.addParent(a);

        d.addChild(g);
        d.addParent(b);

        e.addChild(g);
        e.addChild(h);
        e.addParent(b);
        e.addParent(c);

        f.addChild(h);
        f.addParent(c);

        g.addParent(d);
        g.addParent(e);

        h.addParent(e);
        h.addParent(f);

        Greedy test = new Greedy();

        Node[] lattice = {a, b, c, d, e, f, g, h};
        List<Node> selected = test.selectNode(lattice, a,3);

        for (Node node : selected) {
            System.out.println(node.getName());
        }

    }
}

class Node {
    public long data;
    public String name;
    public List<Node> children = new ArrayList<>();
    public List<Node> parents = new ArrayList<>();

    public Node(String name, long data) {
        this.name = name;
        this.data = data;
    }

    public Node(long data, List<Node> children, List<Node> parents) {
        this.data = data;
        this.children.addAll(children);
        this.parents.addAll(parents);
    }

    public void addChild(Node child) {
        this.children.add(child);
    }

    public void addParent(Node parent) {
        this.parents.add(parent);
    }

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public List<Node> getParents() {
        return parents;
    }

    public void setParents(List<Node> parents) {
        this.parents = parents;
    }
}
