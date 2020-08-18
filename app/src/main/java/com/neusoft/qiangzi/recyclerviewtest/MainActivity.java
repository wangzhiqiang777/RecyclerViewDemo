package com.neusoft.qiangzi.recyclerviewtest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    List<FruitBean> data;
    RecyclerView recyclerView;
    FruitRecyclerViewAdapter adapter;
    DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        initData();
        adapter = new FruitRecyclerViewAdapter(this, data);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void initData() {
        dbHelper = new DBHelper(this);
        dbHelper.open();
//        dbHelper.initData();//这个方法只在投一次运行时调用，生成数据库中初始数据，之后就可以注释掉
        data = dbHelper.getAllFruits();
    }

    public void onItemClick(View v){
        int position = recyclerView.getChildAdapterPosition(v);
        Toast.makeText(this,"你点击了："+data.get(position).name,Toast.LENGTH_SHORT).show();
    }

    /**
     * 选项菜单（右上角三个点的菜单）的创建
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 选项菜单的点击事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_add:
                makeAddFruitDialog();
//                Intent i = new Intent(MainActivity.this,AddFruitDialogActivity.class);
//                startActivityForResult(i, 1);
                break;
            case R.id.menu_item_horizontal: {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                layoutManager.setOrientation(RecyclerView.HORIZONTAL);
            }
            break;
            case R.id.menu_item_vertical: {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                layoutManager.setOrientation(RecyclerView.VERTICAL);
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 上下文菜单点击事件
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.context_menu_modify:
                Toast.makeText(this,"你选择了修改："+adapter.getSelectedItemData().name,Toast.LENGTH_SHORT).show();
                break;
            case R.id.context_menu_delete:
                Toast.makeText(this,"你选择了删除："+adapter.getSelectedItemData().name,Toast.LENGTH_SHORT).show();
                dbHelper.deleteFruitById(adapter.getSelectedItemData().id);
                adapter.removeSelectedItem();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1){//对话框按下确定
            FruitBean bean = (FruitBean)data.getSerializableExtra("fruit");
            dbHelper.insertFruit(bean);
            adapter.addItem(bean);
            Toast.makeText(this,"成功添加水果："+bean.name,Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 创建增加对话框
     * 使用AlertDialog类创建，但是有一个坑要注意！！
     * 布局文件中的descendantFocusability属性不能选择blocksDescendants，否则EditText不会弹出软键盘。
     */
    private void makeAddFruitDialog() {
        //下面一大段是创建一个对话框的过程
        //1.准备自定义视图，为spinner下拉框填装数据
        final View view = View.inflate(this, R.layout.add_fruit_dialog, null);
        final Spinner spinner = view.findViewById(R.id.spinnerFruitName);
        final EditText etPrice = view.findViewById(R.id.editText_price);
        final EditText etAmount = view.findViewById(R.id.editText_amount);
        final ImageView imageView = view.findViewById(R.id.imageView);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, DBHelper.names);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                imageView.setImageResource(DBHelper.icons[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //创建对话框
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("添加水果")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        FruitBean bean = new FruitBean();
                        int index = spinner.getSelectedItemPosition();
                        bean.name = DBHelper.names[index];
                        bean.icon = DBHelper.icons[index];
                        bean.price = Float.valueOf(etPrice.getText().toString());
                        bean.amount = Integer.valueOf(etAmount.getText().toString());
                        dbHelper.insertFruit(bean);
                        adapter.addItem(bean);
                    }
                })
                .setNegativeButton("取消", null)
                .create();
        dialog.show();
    }
}
