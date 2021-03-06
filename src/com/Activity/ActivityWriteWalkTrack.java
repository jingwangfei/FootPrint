package com.Activity;


import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import Entity.TripWalkTrackEntity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.Adapter.GridAdapter;
import com.Bll.MinaSocket;
import com.Tool.Bimp;
import com.Tool.ImageItem;
import com.Tool.JSONtoDataSet;
import com.Tool.SPUtils;
import com.Tool.ToastUtil;
import com.muli_image_selector.onee.MultiImageSelector;
import com.muli_image_selector.onee.MultiImageSelectorActivity;

public class ActivityWriteWalkTrack extends Activity{
	
	private static final int REQUEST_IMAGE = 2;
	private static final int PREVIEW=1;
	private ImageView back;
	private TextView submit;
	private EditText content;
	private GridView gridview;
	private GridAdapter adapter;
	
	private int tripplanid,tripid,Participantsid;
	private double mLongtitude,mLatitude;
	
	TripWalkTrackEntity walktrackentity=new TripWalkTrackEntity();
	private ArrayList<String> path;
	
	//数据更新获取
	public static Handler mhander;
	
	
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_writewalktrack);
		Intent intent=getIntent();
		tripplanid=intent.getIntExtra("tripplanid",-1);
		tripid=intent.getIntExtra("tripid", -1);
		mLongtitude=intent.getDoubleExtra("lon", -1);
		mLatitude=intent.getDoubleExtra("lat",-1);
		Participantsid=intent.getIntExtra("Participantsid",-1);
		
		initView();
		setListen();
		updateUI();
		
		gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new GridAdapter(this);
		gridview.setAdapter(adapter);
		MultiImageSelector.create(ActivityWriteWalkTrack.this).origin(path)
        .start(ActivityWriteWalkTrack.this, REQUEST_IMAGE);
	}



	private void updateUI() {
		// TODO Auto-generated method stub
		mhander=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==1){
					if(((JSONtoDataSet)msg.obj).isFlag()){
						ToastUtil.show(ActivityWriteWalkTrack.this, "提交成功");
						Intent intent=new Intent();
						String time;
						try {
							time = ((JSONObject)(((JSONtoDataSet)msg.obj).getData())).getString("time");
							walktrackentity.setTripWalkTrackTime(time);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						intent.putExtra("return", walktrackentity);
						setResult(RESULT_OK, intent);
						finish();
					}
	
				}
				
			}
		};
	}



	private void initView() {
		// TODO Auto-generated method stub
		back=(ImageView) findViewById(R.id.back_image);
		submit=(TextView) findViewById(R.id.report_text);
		content=(EditText) findViewById(R.id.checkinfo);
		gridview=(GridView) findViewById(R.id.noScrollgridview);
		
	}



	private void setListen() {
		// TODO Auto-generated method stub
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(!content.getText().toString().equals("")){
					
					walktrackentity.setTripWalkTrackName(SPUtils.get(ActivityWriteWalkTrack.this,"userNickname","").toString()+"发了一条轨迹");
					walktrackentity.setTripWalkTrackLon(mLongtitude);
					walktrackentity.setTripWalkTrackLat(mLatitude);
					walktrackentity.setTripWalkTrackContent(content.getText().toString());
					walktrackentity.setTripId(tripid);
					walktrackentity.setUserid((Integer)SPUtils.get(ActivityWriteWalkTrack.this,"userId",-1));
					walktrackentity.setUsername(SPUtils.get(ActivityWriteWalkTrack.this,"userNickname","").toString());
					walktrackentity.setTripParticipantId(Participantsid);
					try {
						MinaSocket.SendMessage(new TripWalkTrackEntity().ToJSON(22, 2, tripplanid, walktrackentity));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}else{
					ToastUtil.show(ActivityWriteWalkTrack.this,"内容不能为空");
				}
				}
		});
		
		gridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == Bimp.tempSelectBitmap.size()) {
					MultiImageSelector.create(ActivityWriteWalkTrack.this).origin(path)
			        .start(ActivityWriteWalkTrack.this, REQUEST_IMAGE);
					
				} else {
					Intent intent = new Intent(ActivityWriteWalkTrack.this,
							ActivityPreview.class);
					intent.putExtra("position",arg2);
					intent.putStringArrayListExtra("path", path);
					startActivityForResult(intent,PREVIEW);
				}
			}
		});
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_IMAGE){
	        if(Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK){
	            // 获取返回的图片列表
	        	Bimp.tempSelectBitmap.clear();
	            path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
	           for(int i=0;i<path.size();i++){
	        	   ImageItem takePhoto = new ImageItem();
  					takePhoto.setImagePath(path.get(i));
  					Bimp.tempSelectBitmap.add(takePhoto);
	           }
	           adapter.notifyDataSetChanged();
	        }
	    }
		if(requestCode == PREVIEW){
			if(resultCode==RESULT_OK){
				path=data.getStringArrayListExtra("path");
			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		adapter.notifyDataSetChanged();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Bimp.tempSelectBitmap.clear();
		
	}
}
