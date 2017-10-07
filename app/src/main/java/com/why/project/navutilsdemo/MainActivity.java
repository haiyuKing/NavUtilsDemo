package com.why.project.navutilsdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.why.project.navutilsdemo.utils.NavUtils;

public class MainActivity extends AppCompatActivity {

	private static final String TAG = "MainActivity";

	private TextView tv_show;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tv_show = (TextView) findViewById(R.id.tv_show);

		int navigationBarHeight = NavUtils.getNavigationBarHeight(this);
		tv_show.setText("底部虚拟导航栏高度值为："+navigationBarHeight + "，单位为px");
	}
}
