package com.example.testing.demo.select;

/**
 * Created by Yyyyyyy on 2017/4/20.
 * 为了转换格式化xml解析出来的公司部门数据，自定义的对象类
 */
public class FormatCompanyBean {

    public int level;//根据/判断每个数据是第几层
    public String allName;//全称
    public String name;//简称
    public int pId;//父亲id
    public int mId;//自己的id
    public String topName; //上级部门的简称
    public String topAllName;//上级部门的全称

    @Override
    public String toString() {
        return "FormatCompanyBean{" +
                "level=" + level +
                ", allName='" + allName + '\'' +
                ", name='" + name + '\'' +
                ", pId=" + pId +
                ", mId=" + mId +
                ", topName='" + topName + '\'' +
                ", topAllName='" + topAllName + '\'' +
                '}';
    }
}
