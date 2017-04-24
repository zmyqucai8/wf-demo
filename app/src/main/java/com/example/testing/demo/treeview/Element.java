package com.example.testing.demo.treeview;

/**
 * Element类
 *
 * @author zmy
 */
public class Element {
    @Override
    public String toString() {
        return "Element{" +
                "allName='" + allName + '\'' +
                ", contentText='" + contentText + '\'' +
                ", level=" + level +
                ", id=" + id +
                ", parendId=" + parendId +
                ", hasChildren=" + hasChildren +
                ", isExpanded=" + isExpanded +
                '}';
    }

    public String getAllName() {
        return allName;
    }

    public void setAllName(String allName) {
        this.allName = allName;
    }

    //全称
    private String allName;
    /**
     * 文字内容
     */
    private String contentText;
    /**
     * 在tree中的层级
     */
    private int level;
    /**
     * 元素的id
     */
    private int id;
    /**
     * 父元素的id
     */
    private int parendId;
    /**
     * 是否有子元素
     */
    private boolean hasChildren;
    /**
     * item是否展开
     */
    private boolean isExpanded;

    /**
     * 表示该节点没有父元素，也就是level为0的节点
     */
    public static final int NO_PARENT = -1;
    /**
     * 表示该元素位于最顶层的层级
     */
    public static final int TOP_LEVEL = 0;

    /**
     * @param contentText
     * @param level
     * @param id
     * @param parendId
     * @param hasChildren
     * @param isExpanded
     */
    public Element(String contentText, String allName, int level, int id, int parendId,
                   boolean hasChildren, boolean isExpanded) {
        super();
        this.allName = allName;
        this.contentText = contentText;
        this.level = level;
        this.id = id;
        this.parendId = parendId;
        this.hasChildren = hasChildren;
        this.isExpanded = isExpanded;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParendId() {
        return parendId;
    }

    public void setParendId(int parendId) {
        this.parendId = parendId;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }
}