package com.newland.homecontrol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.newland.homecontrol.bean.GridViewEntity;
import com.newland.homecontrol.R;

import java.util.ArrayList;
import java.util.List;

/**
 * gridview适配器
 * Created by yizhong.xu on 2017/8/3.
 */

public class GridViewAdapter extends BaseAdapter {


    private Context context;
    private List<GridViewEntity> list = new ArrayList<>();

    public  GridViewAdapter(Context context, List<GridViewEntity> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;//临时存储容器 不用每次到布局文件中去拿view
        if (view == null) {
            //LayoutInflater.from从一个context中获取一个布局填充器，来把xml布局文件转为view对象
            view = LayoutInflater.from(context).inflate(R.layout.item_gridview, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.llBackGround = (LinearLayout) view.findViewById(R.id.llBackGround);
            viewHolder.ivClass = (ImageView) view.findViewById(R.id.ivClass);
            view.setTag(viewHolder);//给view设置格外的数据
        } else {
            viewHolder = (ViewHolder) view.getTag();//取出数据 复用
        }

        if (list.get(i).isOffOn()) {//是否 开关灯
            viewHolder.llBackGround.setBackgroundResource(list.get(i).getImgon());//设置关灯图片
        } else {
            viewHolder.llBackGround.setBackgroundResource(list.get(i).getImgoff());//设置开灯图片
        }
        viewHolder.ivClass.setImageResource(list.get(i).getImgClass());
        return view;
    }

    class ViewHolder {
        ImageView ivClass;//item类型 用来显示 书房 客厅图标
        LinearLayout llBackGround;//布局的背景图片
    }
}
