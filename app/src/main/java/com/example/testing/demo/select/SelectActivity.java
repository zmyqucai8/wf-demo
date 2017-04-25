package com.example.testing.demo.select;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.testing.demo.R;
import com.example.testing.demo.select.treeview.Element;
import com.example.testing.demo.upload.AmUtlis;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by win7 on 2017/4/20.
 * 选择列表页面
 */
public class SelectActivity extends Activity implements View.OnClickListener {
    private ArrayList<Element> elementsData;//部门总数据
    private ArrayList<Element> elements;//部门顶级父类数据
    private SelectPersonAdapter mAdapter; //列表数据的adapter适配器
    //  boolean  true=单选， false=多选
    private boolean tag;
    private Button btn_type;//人员type选择
    private Button btn_yes;//确定
    private Button btn_no;//取消
    private EditText et_input;//输入框
    //    private TextView tv_search;//搜索按钮
    private Button btn_select;//已选中的按钮，点击查看已选择的数据
    private RecyclerView recyclerView;//列表view
    private TextView tv_section;//部门选择
    private String[] typeItem = {"按部门", "按群组", "按人员", "按特定"};
    int index = 0;//类型选择的index
    private ProgressDialog dialog;//类型选择的dialog
    private String sectionAllName;//当前部门的全称
    private List<String> listData = new ArrayList<>();//列表数据data
    //已选中数据
    private ArrayList<String> checkDataList = new ArrayList<>();
    private SelectDataShowPop selectDataShowPop; //已选中的数据pop
    private boolean isShowSelectTypeDialog = true; //是否可以显示类型选择的dialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_activity);
        tag = getIntent().getBooleanExtra("TAG", true);
        Log.e("选择模式 = ", tag ? "单选" : "多选");
        initData();
        initView();
    }

    //设置已选中的信息
    public void setSelectInfo() {
        if (checkDataList.size() > 0) {
            btn_select.setText("已选" + checkDataList.size() + "个,点击修改");
        } else {
            btn_select.setText("已选" + checkDataList.size() + "个");
        }
    }


    //初始化view
    private void initView() {
        btn_type = (Button) findViewById(R.id.btn_type);
        btn_select = (Button) findViewById(R.id.btn_select);
        btn_no = (Button) findViewById(R.id.btn_no);
        btn_yes = (Button) findViewById(R.id.btn_yes);
        et_input = (EditText) findViewById(R.id.et_input);
//        tv_search = (TextView) findViewById(R.id.tv_search);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        tv_section = (TextView) findViewById(R.id.tv_section);
        tv_section.setOnClickListener(this);
//        tv_search.setOnClickListener(this);
        btn_select.setOnClickListener(this);
        btn_type.setOnClickListener(this);
        btn_yes.setOnClickListener(this);
        btn_no.setOnClickListener(this);
        et_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    AmUtlis.hideKeyboard(SelectActivity.this);
                    httpGetPersonBySearch();
                    // search pressed and perform your functionality.
                }
                return false;
            }
        });
        //adapter设置
        mAdapter = new SelectPersonAdapter(new ArrayList<String>(), tag);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.isFirstOnly(true);
        mAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mAdapter.setEmptyView(AmUtlis.getEmptyView(this, "暂无数据"));
        mAdapter.setOnItemCheckLitener(new SelectPersonAdapter.OnItemCheckLitener() {
            @Override
            public void onItemCheck(View view, int position, boolean isCheck) {
                if (position == -1) {//防止点击过快导致的postion为-1 数组越界
                    return;
                }
                String checkItem = listData.get(position);
                //listData
                if (tag) {
                    //单选
                    checkDataList.clear();
                    if (isCheck) {//选中
                        checkDataList.add(checkItem);
                    } else {//取消
                    }
                } else {
                    //多选
                    if (isCheck) {//选中 没添加才添加
                        if (!checkDataList.contains(checkItem)) {
                            checkDataList.add(checkItem);
                        }
                    } else {//取消
                        checkDataList.remove(checkItem);
                    }
                }
                setSelectInfo();
            }
        });
        recyclerView.setAdapter(mAdapter);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_type:
                //人员 、类型选择
                showSelectTypeDialog();
                break;
//            case R.id.tv_search:
//                //搜索按钮
//                httpGetPersonBySearch();
//                break;
            case R.id.btn_select:
                //已选择点击查看。
                if (checkDataList.size() > 0) {
                    if (selectDataShowPop == null) {
                        selectDataShowPop = new SelectDataShowPop();
                    }
                    selectDataShowPop.showPop(SelectActivity.this, checkDataList, new SelectDataShowPop.onDeleteListener() {
                        @Override
                        public void onDelete(String selectStr, int postion) {
                            checkDataList.remove(postion);
                            //如果当前数据存在被移除的数据，并且是选中状态，那么切换选中状态
                            for (int i = 0; i < listData.size(); i++) {
                                if (listData.get(i).equals(selectStr) && mAdapter.isItemChecked(i)) {
                                    mAdapter.switchSelectedState(i);
                                }
                            }
                        }
                    });

                } else {
                    AmUtlis.showToast("你还没有选择");
                }
                break;
            case R.id.tv_section:
                //公司及部门选择
                SelectSectionPop.getInstance().showPop(SelectActivity.this, elements, elementsData, new SelectSectionPop.onSelectListener() {
                    @Override
                    public void onSelect(String selectStr) {
                        tv_section.setText(selectStr);
                        sectionAllName = selectStr;
                        String type = btn_type.getText().toString();
                        if ("按部门".equals(type)) {
                            httpGetPersonBySection(AmUtlis.containsStr("\\", sectionAllName), btn_type.getText().toString(), sectionAllName);
                        } else if ("按群组".equals(type)) {
                            httpGetPersonByGroup();
                        }
                        mAdapter.clearSelectedState();
                    }
                });
                break;

            case R.id.btn_yes:
                //确定
                if (checkDataList.size() > 0) {
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra("restut", checkDataList);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    AmUtlis.showToast("请先选择");
                }
                break;
            case R.id.btn_no:
                //取消
                finish();
                break;
        }
    }

    //根据搜索内容搜索人员
    private void httpGetPersonBySearch() {
        String str = et_input.getText().toString().trim();
        if (!TextUtils.isEmpty(str)) {
            final String name; //公司名
            String s = tv_section.getText().toString();
            name = s.contains("\\") ? s.substring(0, s.indexOf("\\")) : s;
//            String url = "http://192.168.0.12:8900/oanames.nsf/getSelectorTreeList?Openagent&getType=Search&Company=" + name + "&CategoryKey=按部门&NodeValue=" + name + "&AddressFrom=0&SearchName=" + str;
            OkGo.get(Constant.HOST + Constant.getSelectorTreeList).tag(this)
                    .params("Openagent", "")
                    .params("getType", "Search")
                    .params("Company", name)
                    .params("SearchName", str)
                    .params("NodeValue", name)
                    .params("CategoryKey", "按部门")
                    .params("AddressFrom", 0)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            //柳眉|是|/hrinfophoto/柳眉.jpg|,刘主龙|是||,刘红宇|是||,刘夏德|是||,刘丽慧|是||,刘斌|是||,刘世武|是||,刘小五|是||,刘小三|是||,刘4无|是||,刘建华|是||
                            mAdapter.clearSelectedState();

                            listData.clear();
                            listData.addAll(AmUtlis.splitStringByChar(",", s));
                            mAdapter.setNewData(listData);
                        }
                    });

        }
    }

    //显示选择类型的dialog
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void showSelectTypeDialog() {
        if (isShowSelectTypeDialog) {
            isShowSelectTypeDialog = false;
            new AlertDialog.Builder(this).setTitle("选择类型").setIcon(
                    android.R.drawable.ic_dialog_info).setSingleChoiceItems(
                    typeItem, index,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            index = which;
                            String s = typeItem[which];
                            btn_type.setText(s);
                            isShowSelectTypeDialog = true;
                            dialog.dismiss();
                            if (TextUtils.isEmpty(sectionAllName)) {
                                return;
                            }
                            if ("按部门".equals(s)) {
                                tv_section.setText(sectionAllName);
                                if (sectionAllName.contains("\\")) {//切换到部门选择的时候如果不包含子部门，默认当前公司下的第一个子部门
                                    httpGetPersonBySection(AmUtlis.containsStr("\\", sectionAllName), btn_type.getText().toString(), sectionAllName);
                                } else {
                                    for (int i = 0; i < elements.size(); i++) {
                                        if (sectionAllName.equals(elements.get(i).getContentText())) {
                                            int id = elements.get(i).getId();
                                            for (int y = 0; y < elementsData.size(); y++) {
                                                if (elementsData.get(y).getParendId() == id) {
                                                    httpGetPersonBySection(AmUtlis.containsStr("\\", sectionAllName), btn_type.getText().toString(), elementsData.get(y).getAllName());
                                                    tv_section.setText(elementsData.get(y).getAllName());
                                                    break;
                                                }
                                            }
                                            break;
                                        }
                                    }
                                }

                            } else if ("按群组".equals(s)) {
                                httpGetPersonByGroup();
                            }
                            mAdapter.clearSelectedState();//置空选中状态

                        }
                    }).setNegativeButton("取消", null)

                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            isShowSelectTypeDialog = true;
                        }
                    })
                    .show();
        }
    }

    /**
     * initData
     * http获取一二级列表  并且格式化数据
     */
    private void initData() {
        dialog = ProgressDialog.show(this, "", "加载中...");

        OkGo.get(Constant.HOST + Constant.getdeptxml).tag(this).execute(new StringCallback() {
            @Override
            public void onSuccess(final String s, Call call, Response response) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        groupBeanList = XmlUtlis.getXmlList(s);
                        elementsData = XmlUtlis.getXmlSectionList(s);
                        elements = XmlUtlis.elements;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //设置默认第一个公司第一个部门

                                if (elementsData != null && elementsData.size() > 0) {
                                    String allName = elements.get(0).getContentText() + "\\" + elementsData.get(1).getContentText();
                                    tv_section.setText(allName);
                                    sectionAllName = allName;
                                    //默认根据部门去获取人员
                                    httpGetPersonBySection(elements.get(0).getContentText(), btn_type.getText().toString(), allName);
                                } else {
                                    AmUtlis.showToast("没有数据");
                                }

                            }
                        });
                    }
                }).start();

            }
        });

    }

    /**
     * get请求， 获取人员 根据部门
     * http://192.168.0.12:8900/oanames.nsf/getSelectorTreeList?Openagent&getType=Item&Company=伟峰集团&CategoryKey=按部门&NodeValue=伟峰集团/系统维护&AddressFrom=0
     * 返回：admin||/hrinfophoto/admin.jpg|男,admin的部门第一负责人|是||,人事总监|是||,RachelLee|是||
     *
     * @param company     公司名字    ： 伟峰集团
     * @param categoryKey 查询类型 key   ： 按部门
     * @param nodeValue   部门全称  ：伟峰集团\系统维护
     */
    private void httpGetPersonBySection(String company, String categoryKey, String nodeValue) {
        String newNodeValue = nodeValue.replace("\\", "/");
        AmUtlis.showLog(newNodeValue);
//        String url = "http://192.168.0.12:8900/oanames.nsf/getSelectorTreeList?Openagent&getType=Item&Company=" + company + "&CategoryKey=" + categoryKey + "&NodeValue=" + newNodeValue + "&AddressFrom=0";
        OkGo.get(Constant.HOST + Constant.getSelectorTreeList).tag(this)
                .params("Openagent", "")
                .params("getType", "Item")
                .params("Company", company)
                .params("CategoryKey", categoryKey)
                .params("NodeValue", newNodeValue)
                .params("AddressFrom", 0)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
//                        admin||/hrinfophoto/admin.jpg|男,admin的部门第一负责人|是||,人事总监|是||,RachelLee|是||
                        listData = AmUtlis.splitStringByChar(",", s);
                        mAdapter.setNewData(listData);
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
    }


    /**
     * 按群组选择
     */
    private void httpGetPersonByGroup() {
        final String name; //公司名
        String s = tv_section.getText().toString();
        name = s.contains("\\") ? s.substring(0, s.indexOf("\\")) : s;
        OkGo.get(Constant.HOST + Constant.getSelectorTreeList)
                .tag(this)
                .params("Openagent", "")
                .params("getType", "Item")
                .params("Company", name)
                .params("CategoryKey", "选群组")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        //伟峰集团;test,123,rrrr,集团仓库管理群,测试群组,基金小组,群组12345,允许外发邮件的群1,允许外发邮件的群2,允许外发邮件总群,其他可编辑测试群组,公共资料修改人员,研发部普通技术人员,设计六所（华南区）,技术部组长,考勤-小组1,abc123,abc222,abc333,人力资源总监3,人力资源助理3,伟峰集团全体人员,伟峰集团-财务部,伟峰集团-采购部,伟峰集团-代理商管理部,伟峰集团-服装总部,伟峰集团-服装总部-服装部,伟峰集团-工程总部,伟峰集团-工程总部-工程上海分部,伟峰集团-工程总部-工程深圳分部,伟峰集团-技术测试部,伟峰集团-技术测试部-OA系统开发与维护部,伟峰集团-市场开发部,伟峰集团-物流部,伟峰集团-系统维护,伟峰集团-项目部,伟峰集团-综合部,伟峰集团-综合部-ISO办,伟峰集团-综合部-人事部,伟峰集团-综合部-行政部,伟峰集团-综合部-行政部-财务综合部,伟峰集团-综合部-行政部-财务综合部-品牌策划部,伟峰集团-综合管理部,伟峰集团-总经办,伟峰集团-总经办-ISO办,伟峰集团-总经办-导入部门,伟峰集团-总经办-技术部,伟峰集团-总经办-人力资源部,伟峰集团-总经办-销售部,伟峰集团-测试括号部门（test）,伟峰集团-test123abc,伟峰集团-总经办-人力资源部1,伟峰集团-综合管理部2
                        tv_section.setText(name);
                        String data = s.substring(s.indexOf(";") + 1, s.length());
                        listData = AmUtlis.splitStringByChar(",", data.replace(name + "-", ""));
                        //处理最后一条数据有换行符\n的问题
                        String s1 = listData.get(listData.size() - 1);
                        if (s1.contains("\n")) {
                            listData.remove(listData.size() - 1);
                            listData.add(s1.replace("\n", ""));
                        }
                        //设置新数据
                        mAdapter.setNewData(listData);
                    }
                });
    }
}
