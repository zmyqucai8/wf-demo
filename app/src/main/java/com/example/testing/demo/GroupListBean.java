package com.example.testing.demo;

import java.util.List;

/**
 * Created by Yyyyyyy on 2017/4/19.
 */
public class GroupListBean  {
    /**
     * 公司name
     */
    public String name;
    /**
     * 子部门name 集合
     */
    public List<ChildListBean> childListBean;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ChildListBean> getChildListBean() {
        return childListBean;
    }

    public void setChildListBean(List<ChildListBean> childListBean) {
        this.childListBean = childListBean;
    }
}
