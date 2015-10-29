package com.mixiaoxiao.actionmenusample;

import com.mixiaoxiao.actionmenu.ActionMenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class SampleActivity extends Activity {
	
	private static final String[] SAMPLE_ACTIONS = new String[]{"Copy","Forward","Favorite","More"};
	
	private ActionMenu.ActionMenuListener actionMenuListener = new ActionMenu.ActionMenuListener() {
		@Override
		public void onAction(String action, int which) {
			Toast.makeText(SampleActivity.this, "action=" + action + " which=" + which, Toast.LENGTH_SHORT).show();
		}
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        final ListView listView = (ListView) findViewById(R.id.listView1);
        final String[] items = new String[]{"aaaaa","bbbbbb","cccc","ddddd","eeeee","fffff","gggggg","hhhh","iiiiii"};
        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.listitem, android.R.id.text1, items));
        listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				onClickSample(view);
			}
		});
    }
    
    public void onClickDialog(View v){
    	final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("Dialog").setView(View.inflate(this, R.layout.dialog_content, null))
    		.setPositiveButton(android.R.string.ok, null)
    		.setNegativeButton(android.R.string.cancel, null)
    		.create().show();
    }
    
    public void onClickSample(View v){
    	ActionMenu.build(SampleActivity.this, v).addActions(SAMPLE_ACTIONS).setListener(actionMenuListener).show();
    }
    
    public void onClickLongDesc(View v){
    	String desc = "I have a very very very long description. If set no ActionMenuListener, the menuItem is not clickable.";
    	ActionMenu.build(SampleActivity.this, v).addAction(desc).show();
    }
    
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.sample, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
