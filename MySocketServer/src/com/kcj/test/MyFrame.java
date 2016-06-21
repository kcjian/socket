package com.kcj.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.kcj.db.DBUtil;


public class MyFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JPanel jPanel1;
	JPanel jPanel2;

	JTextArea jTextArea;

	JScrollPane jScrollPane;

	JButton button1;
	JButton button2;
	private  ExecutorService cachedThreadPool = Executors.newCachedThreadPool();  
	private ExecutorService singleThreadExecutor=Executors.newSingleThreadExecutor();
	
	 ServerSocket serverSocket;
	 
		private final String IP="192.168.1.68";
	public MyFrame() {
		super();
		jPanel1 = new JPanel();

		jPanel2 = new JPanel();

		button1 = new JButton("����");
		button1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				button1();

			}
		});
		button2 = new JButton("ֹͣ");
		button2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				button2();

			}
		});

		jPanel2.setLayout(new BorderLayout());
		jTextArea = new JTextArea();
		jScrollPane = new JScrollPane();
		this.setTitle("xxxxx");
		this.setSize(new Dimension(400, 400));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.getContentPane().add(jPanel1, BorderLayout.NORTH);
		this.getContentPane().add(jPanel2, BorderLayout.CENTER);
		jScrollPane.setViewportView(jTextArea);
		jPanel2.add(jScrollPane, BorderLayout.CENTER);

		jPanel1.add(button1);
		jPanel1.add(button2);
		// ���������socket����ͨ�˿�8899
		 try {
			serverSocket = new ServerSocket(8899);
		} catch (IOException e) {
			e.printStackTrace();
		}
		 this.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					isOk=false;
					serverSocket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	protected void button2() {
		button1.setEnabled(true);
		button2.setEnabled(false);
		
		isOk = false;
		
		// �Լ����Լ�һ����Ϣ
		new Thread() {
			public void run() {
				socketSend("!bye!");
			};

		}.start();
		
		
	}
	
	
	//�ͻ���
		public void socketSend(String info)
		{
			try {
				//��ȡ���ͷ���ַ
				InetAddress inetAddress=InetAddress.getByName(IP);
				//ʵ����socket�ͻ���
				Socket socket=new Socket(inetAddress, 8899);
				//��������
				OutputStream outputStream=socket.getOutputStream();
				//ת����Ϊ�ַ���
				OutputStreamWriter outputStreamWriter=new OutputStreamWriter(outputStream, "utf-8");
				//��һ��������
				BufferedWriter bufferedWriter=new BufferedWriter(outputStreamWriter);
				bufferedWriter.write(info);
				bufferedWriter.flush();
				bufferedWriter.close();
				outputStreamWriter.close();
				outputStream.close();
				socket.close();
				
			} catch (Exception e) {
				e.printStackTrace();
				
			}
			
		}
	
	
	
	
	
	
	
	

	protected void button1() {
		button1.setEnabled(false);
		button2.setEnabled(true);
		
		
		isOk = true;
		
		cachedThreadPool.execute(new Runnable() {
				public void run() {
					while (isOk) {
					socketListener();
				}
				}
			});
			
				
		
			
		
	}

	boolean isOk = true;


	// �����
	public  void socketListener() {
		
		try {			
			// ��ʼ����accept()�������������
			 Socket socket = serverSocket.accept();
			singleThreadExecutor.execute(new MyRunnable(socket));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	class MyRunnable implements Runnable{
		
		private Socket socket;
		public MyRunnable(Socket socket){
			this.socket=socket;
		}
		@Override
		public void run() {
			InputStream inputStream = null;
			InputStreamReader inputStreamReader = null;
			BufferedReader bufferedReader = null;
			try {
				final String result;
				// ��ȡ���������ֽڡ�
				 inputStream = socket.getInputStream();
				
				// ת���ַ���
				 inputStreamReader = new InputStreamReader(
						inputStream, "utf-8");

				// �׸�������
				 bufferedReader = new BufferedReader(
						inputStreamReader);

				StringBuffer stringBuffer = new StringBuffer();
				String tempinfo = "";
				while ((tempinfo = bufferedReader.readLine()) != null) {
					stringBuffer.append(tempinfo);
					if(tempinfo.contains("/"))break;//���յ�"/"����ѭ��
				}
				result = stringBuffer.toString();
				System.out.println(result);
				String table=result.substring(0,1);//��ȡһ���ַ�,���벻ͬ���ݿ�
				String content=result.substring(1, result.length()-1);
				if("a".equals(table)){
					DBUtil.getInstances().insertAMessage(content);
				}else if("b".equals(table)){
					DBUtil.getInstances().insertBMessage(content);
				}else if ("c".equals(table)) {
					DBUtil.getInstances().insertCMessage(content);
				}
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						jTextArea.setText(jTextArea.getText()+result+"\r\n");
					}
				});
				response(result+"\r\n");//����ԭ������result����"/"
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					bufferedReader.close();
					inputStreamReader.close();
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		private  void response(String response)
		{ 
			try {
				//��������
				OutputStream outputStream=socket.getOutputStream();
				//ת����Ϊ�ַ���
				OutputStreamWriter outputStreamWriter=new OutputStreamWriter(outputStream, "utf-8");
				//��һ��������
				BufferedWriter bufferedWriter=new BufferedWriter(outputStreamWriter);
				bufferedWriter.write(response);
				bufferedWriter.flush();
				bufferedWriter.close();
				outputStreamWriter.close();
				outputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
