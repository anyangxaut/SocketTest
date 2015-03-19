package edu.xaut.sockettest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * 本程序为socket测试程序，每隔30s建立socket连接，发送数据包，收到服务器端回复后断开连接，循环往复、
 * 发送数据包格式:{”Type”:”3”,” MessageContent”:””}
 * 回复数据包格式:{}
 * @author anyang
 *
 */
public class SocketTest extends ActionBarActivity {

	private static final String TAG = "SocketTest";
	private static final long TIME_INTERVAL = 30 * 1000;	
	
	public static final String HOST = "202.200.119.162";
	public static final int PORT = 12000;
	
	public Socket mSocket = null;
	private JSONObject jsonmsg = null;
	
	private TextView tv = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_socket_test);
		
		tv = (TextView) findViewById(R.id.tv);
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				initJsonMsg();
				
				Socket(jsonmsg.toString());
			}
			
		}, 0, TIME_INTERVAL);
	}

	// 初始化要发送的数据包，这里使用json字符串格式
	private void initJsonMsg(){
		
		jsonmsg = new JSONObject();
		
		try {
			jsonmsg.put("Type", 3);
			jsonmsg.put("MessageContent", "");
		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 测试代码
		Log.d(TAG, jsonmsg.toString());
	}
	
	// 初始化socket连接,并发送消息
	@SuppressLint("NewApi")
	private void Socket(String msg){
		
		// 初始化socket连接
		try {
			 mSocket = new Socket(HOST, PORT);
			// 在非主线程中修改UI设置
			 this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					 tv.setText("Socket is connecting!");
				}
			 });
			 
			// 发送消息
				try {
					if (!mSocket.isClosed() && !mSocket.isOutputShutdown()) {
						OutputStream os = mSocket.getOutputStream();
						String message = msg ;
						os.write(message.getBytes());
						os.flush();	
						
						Log.d("TAG", "已发送消息：" + message);
					} 
				} catch (IOException e) {
					e.printStackTrace();
				}
				
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if (null != mSocket) {
			
				try {
					InputStream is = mSocket.getInputStream();
					byte[] buffer = new byte[3 * 4];
					int length = 0;
					
					if(!mSocket.isClosed() && !mSocket.isInputShutdown()
							&& ((length = is.read(buffer)) != -1)) {
						if (length > 0) {
							String message = new String(buffer, "UTF-8");
							
							Log.d("TAG", "已接收消息：" + message);
							// 处理服务器端的回复信息
							if(message.indexOf("#") != -1){
								// 关闭socket连接
								if (!mSocket.isClosed()) {
									try {
										mSocket.close();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								mSocket = null;
								// 在非主线程中修改UI设置
								 this.runOnUiThread(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											tv.setText("Socket is disconnecting!");
										}
									 });
								
							}
							}
							}		
							
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
				
	}
	

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.socket_test, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
