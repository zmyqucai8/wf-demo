package com.example.testing.demo;

/**
 * Created by Yyyyyyy on 2017/4/19
 * 二级部门 bean
 */
public class ChildListBean {
    @Override
    public String toString() {
        return "ChildListBean{" +
                "name='" + name + '\'' +
                '}';
    }
    /**
     * 子部门name
     */
    public String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
