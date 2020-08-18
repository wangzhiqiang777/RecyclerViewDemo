package com.neusoft.qiangzi.recyclerviewtest;

/**
 * 文件用途：数据库访问封装类
 * 设计思想：
 * 作者：强子  12345
 * 联系方式：wangzhiqiang@neusoft.edu.cn
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DBHelper {

    Context context;
    SQLiteDatabase db;
    static final String DB_NAME = "fruit.db";
    static final String TABLE_NAME = "fruit_list";

    //初始化数据库用的数据
    public static final String[] names = {
            "苹果", "香蕉", "樱桃", "草莓", "柠檬", "橙子", "鸭梨",
    };
    public static final float[] prices = {
            7.5f, 5.0f, 20.0f, 18.0f, 22.5f, 24.6f, 12.0f,
    };
    public static final int[] icons = {
            R.drawable.apple,
            R.drawable.banana,
            R.drawable.cherry,
            R.drawable.strawberry,
            R.drawable.lemon,
            R.drawable.orange,
            R.drawable.pear,
    };

    /**
     * 数据库构造方法
     *
     * @param c
     */
    DBHelper(Context c) {
        context = c;
    }

    /**
     * 打开数据方法
     * 版权：强子  12345
     *
     * @return
     */
    public boolean open() {
        String path = context.getFilesDir() + "/" + DB_NAME;
        db = SQLiteDatabase.openOrCreateDatabase(path, null, null);
        db.execSQL("create table if not exists " + TABLE_NAME +
                "(id integer  primary key autoincrement, name vchar, price float, amount int,icon int)");
        return true;
    }

    /**
     * 关闭数据库
     */
    public void close() {
        db.close();
    }

    /**
     * 初始化数据库的数据
     * 这个方法只在投一次运行时调用，生成数据库中初始数据，之后就可以注释掉
     */
    public void initData() {
        deleteAllFruits();
        for (int i = 0; i < 7; i++) {
            FruitBean bean = new FruitBean();
            bean.icon = icons[i];
            bean.name = names[i];
            bean.amount = 1;
            bean.price = prices[i];
            insertFruit(bean);
        }
    }

    /**
     * 插入水果信息
     *
     * @param bean
     * @return
     */
    public boolean insertFruit(FruitBean bean) {
        ContentValues values = new ContentValues();
        values.put("name", bean.name);
        values.put("price", bean.price);
        values.put("amount", bean.amount);
        values.put("icon", bean.icon);
        long i = db.insert(TABLE_NAME, null, values);
        if (i > 0) return true;
        else return false;
    }

    /**
     * 获取所有水果信息列表
     *
     * @return
     */
    public List<FruitBean> getAllFruits() {
        List<FruitBean> data = new ArrayList<>();
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
        while (c.moveToNext()) {
            FruitBean bean = new FruitBean();
            bean.id = c.getInt(c.getColumnIndex("id"));
            bean.name = c.getString(c.getColumnIndex("name"));
            bean.price = c.getFloat(c.getColumnIndex("price"));
            bean.amount = c.getInt(c.getColumnIndex("amount"));
            bean.icon = c.getInt(c.getColumnIndex("icon"));
            data.add(bean);
        }
        return data;
    }

    public int deleteFruitById(int id) {
        return db.delete(TABLE_NAME, "id=?", new String[]{String.valueOf(id)});
    }

    public int deleteAllFruits() {
        return db.delete(TABLE_NAME, null, null);
    }

}
