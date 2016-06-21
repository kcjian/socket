package com.kcj.mysocket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.hzj.mysocket.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private final String IP="192.168.1.68";
	
	
	private Timer timer;
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
				
				// 获取数据流【字节】
				InputStream inputStream = socket.getInputStream();
				
				// 转换字符流
				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream, "utf-8");

				// 套个连接流
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);

				StringBuffer stringBuffer = new StringBuffer();
				String tempinfo = "";
				//使用bufferedReader.readLine()在不关闭io的情况下必须加\r\n
				while ((tempinfo = bufferedReader.readLine()) != null) {
					stringBuffer.append(tempinfo);
					if(tempinfo.contains("/"))//加/作为结束符,跳出while循环
					break;
				}
				
				bufferedWriter.close();
				outputStreamWriter.close();
				outputStream.close();
				bufferedReader.close();
				inputStreamReader.close();
				inputStream.close();
				socket.close();
				// 返回的结果
				String result = stringBuffer.toString();
				/*去掉结束符号/*/
				final String string=result.substring(0, result.length()-1);
				runOnUiThread(new Runnable() {
					public void run() {
						myAdapter.add(string);
					}
				});
				Log.i("gavin", result);
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
			
		}
		
	}
	
	private ListView listView;
	private MyAdapter myAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listView=(ListView)findViewById(R.id.listView1);
		myAdapter=new MyAdapter(this);
		listView.setAdapter(myAdapter);
		singleThreadExecutor=Executors.newSingleThreadExecutor();
		new Thread()
		{
			int i=1;
			public void run() 
			{
				while(isSend){
					//使用bufferedReader.readLine()在不关闭io的情况下必须加\r\n
					//加/作为结束符,跳出while循环
					socketSend("b"+i+++"/\r\n");	
				}
			}
		}.start();
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
	private class MyAdapter extends BaseAdapter{
		private List<String> list;
		private Context context;
		public MyAdapter(Context context) {
			list=new ArrayList<String>();
			this.context=context;
		}
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView==null){
				convertView=new TextView(context);
			}
			((TextView)convertView).setText(list.get(position));
			return convertView;
		}
		public void add(String string){
			list.add(string);
			notifyDataSetChanged();
		}
	}
}
