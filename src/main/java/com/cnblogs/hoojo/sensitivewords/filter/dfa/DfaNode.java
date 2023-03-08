package com.cnblogs.hoojo.sensitivewords.filter.dfa;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Set;

/**
 * dfa多叉树模型
 */
public class DfaNode {

    private char _char;
    private DfaNode parent;
    private boolean word;
    private Map<Character, DfaNode> childs;

    public DfaNode() {
    }

    public DfaNode(char _char) {
        this._char = _char;
    }

    public static void main(String[] args) {
        DfaNode node = new DfaNode('中');

        DfaNode g = new DfaNode('国');
        g.addChild(new DfaNode('人'));

        DfaNode n = new DfaNode('男');
        n.addChild(new DfaNode('人'));
        g.addChild(n);

        node.addChild(g);
        node.addChild(new DfaNode('间'));

        node.print(node);
    }

    public boolean isWord() {
        return word;
    }

    public void setWord(boolean word) {
        this.word = word;
    }

    public boolean isLeaf() {
        return (childs == null || childs.isEmpty());
    }

    public char getChar() {
        return _char;
    }

    public void setChar(char _char) {
        this._char = _char;
    }

    public void addChild(DfaNode child) {
        if (this.childs == null) {
            childs = Maps.newHashMap();
        }

        this.childs.put(child.getChar(), child);
        //child.setParent(this);
    }

    public void removeChild(DfaNode child) {
        if (this.childs != null) {
            this.childs.remove(child.getChar());
        }
    }

    public DfaNode getParent() {
        return parent;
    }

    public void setParent(DfaNode parent) {
        this.parent = parent;
    }

    public Map<Character, DfaNode> getChilds() {
		/*if (this.childs == null) {
			this.childs = Maps.newHashMap();
		}*/
        return this.childs;
    }

    public void setChilds(Map<Character, DfaNode> childs) {
        this.childs = childs;
    }

    public void print(DfaNode node) {
        System.out.println(node.getChar());
        if (node.getChilds() != null) {
            Set<Character> keys = node.getChilds().keySet();
            for (Character _char : keys) {
                print(node.getChilds().get(_char));
            }
        }
    }
}
