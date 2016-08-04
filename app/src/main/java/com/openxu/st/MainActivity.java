package com.openxu.st;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private SlideTab slideTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        slideTab = (SlideTab)findViewById(R.id.slideTab);

        //tabNames.length must > = 2
        slideTab.setTabNames(new String[]{"tab1","tab2","tab3","tab4"});
        //设置选中序号，从0开始，要小于tabNames的长度-1
        slideTab.setSelectedIndex(2);
        //获取选中的序列号
        int selectIndex = slideTab.getSelectedIndex();
        Log.e("openxu","您当前选中的索引是"+selectIndex);

    }
}
