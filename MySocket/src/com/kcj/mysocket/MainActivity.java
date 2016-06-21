package com.kcj.mysocket;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.hzj.mysocket.R;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	private final String IP="192.168.1.68";
	
	
	EditText editText1;
	Button button1;
	Timer timer;
	private ExecutorService singleThreadExecutor;
	private boolean isSend=true;
	//客户端
	public  void socketSend(String info)
	{
		Log.e("kcj", "send:"+info);
		Socket socket = null;
		try {
			//获取发送方地址192.168.1.68
			InetAddress inetAddress=InetAddress.getByName(IP);
			//实例化socket客户端
			 socket=new Socket(inetAddress, 8899);
				//发送数据
				OutputStream outputStream=socket.getOutputStream();
				//转换流为字符流
				OutputStreamWriter outputStreamWriter=new OutputStreamWriter(outputStream, "utf-8");
				//套一个缓存流
				BufferedWriter bufferedWriter=new BufferedWriter(outputStreamWriter);
				bufferedWriter.write(info);
				bufferedWriter.flush();
				bufferedWriter.close();
				outputStreamWriter.close();
				outputStream.close();
				socket.close();
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("kcj", e.getMessage());
			if(socket!=null&&!socket.isClosed()){
				try {
					socket.close();
					socket=null;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			Log.e("kcj", "send:"+info+"XXX");
			if(isSend)
			socketSend(info);
//			if(timer!=null){
//				timer.cancel();
//				timer=null;
//			}
			
		}
		
	}
	
	private void sendInfo(final String info)
	{
		singleThreadExecutor.execute(new Runnable() {
			public void run() {
				socketSend(info);
			}
		});
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		singleThreadExecutor=Executors.newSingleThreadExecutor();
		button1=(Button)this.findViewById(R.id.button1);
		button1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				button1();
				
			}
		});
		editText1=(EditText)this.findViewById(R.id.editText1);
		new Thread()
		{
			int i=1;
			public void run() 
			{
				while(isSend){
					socketSend("b"+i++);	
				}
			}
		}.start();
	}

	protected void button1() {
		
		new Thread()
		{
			public void run() 
			{
				socketSend(editText1.getText().toString());		
			}
		}.start();
		editText1.setText("");
	}
	@Override
	protected void onDestroy() {
		isSend=false;
		if(timer!=null){
			timer.cancel();
			timer=null;
		}
		singleThreadExecutor.shutdown();
		super.onDestroy();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
