package com.chat;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class JChat {
	public static void main(String[] args) {
		new ChatGui();
	}
}
class ChatGui extends Frame{
	private Panel chatArea,buttonArea;
	private TextArea record,message;
	private TextField usr;
	private Button send,close,clear;
	private Label target;

	ChatGui(){
		super("欢迎使用java聊天");
		init();
		try{
			new Thread(new JSocket(10086)).start();
		}catch (Exception e){
			record.append("未成功构造接收器\r\n");
		}
	}

	private void init(){
		chatArea = new Panel(new BorderLayout());
		buttonArea = new Panel(new FlowLayout());
		usr = new TextField("127.0.0.1",15);
		send = new Button("发送");
		close = new Button("关闭");
		clear = new Button("清屏");
		record = new TextArea("",14,1,TextArea.SCROLLBARS_VERTICAL_ONLY);
		message = new TextArea("",8,1,TextArea.SCROLLBARS_NONE);
		target = new Label("目标用户IP");

		chatArea.add(record,BorderLayout.NORTH);
		chatArea.add(message,BorderLayout.SOUTH);

		buttonArea.add(target);
		buttonArea.add(usr);
		buttonArea.add(send);
		buttonArea.add(close);
		buttonArea.add(clear);

		add(chatArea,BorderLayout.NORTH);
		add(buttonArea,BorderLayout.SOUTH);

		setAppearance(454,490);
		setEvent();
		setVisible(true);
		setResizable(false);
	}

	private void setEvent(){
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				System.exit(0);
		}});
		close.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.exit(0);
		}});
		clear.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				record.setText("");
		}});
		send.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				send();
				message.setText("");
		}});
		message.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				if(e.getKeyCode()==KeyEvent.VK_ENTER){
					send();
				}
			}
			public void keyReleased(KeyEvent e){
				message.setText("");
			}
		});
	}
	private void send(){
		try{
			new JSocket().sendMsg(message.getText(),usr.getText());
		}catch (Exception ie){
			record.append("未成功构造发送器\r\n\r\n");
		}
		record.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ").format(new Date())+
		"我对"+usr.getText()+"说："+message.getText()+"\r\n\r\n");
	}

	private void setAppearance(int width,int height){
		Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(((int)scr.getWidth()-width)/2,((int)scr.getHeight()-height)/2);
		setSize(width,height);
		setBackground(Color.GRAY);
		record.setBackground(Color.WHITE);
		Font style = new Font("",Font.PLAIN,15);
		record.setFont(style);
		message.setFont(style);
		usr.setFont(style);
		target.setFont(style);
		send.setFont(style);
		close.setFont(style);
		clear.setFont(style);
		record.setEditable(false);
	}
	private class JSocket extends DatagramSocket implements Runnable{
		JSocket(int port)throws SocketException{
				super(port);
		}
		JSocket()throws SocketException{}
		public void sendMsg(String content,String ip){
			byte[] buf = content.getBytes();
			try{
				DatagramPacket pack = new DatagramPacket(buf,buf.length,InetAddress.getByName(ip),10086);
				send(pack);
			}catch (Exception e){
				record.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ").format(new Date())+
					content+"  发送失败\r\n\r\n");
			}finally{
				close();
			}	
		}
		public void run(){
			DatagramPacket dp = new DatagramPacket(new byte[1024],1024);
			try{
				while(true){
					receive(dp);
					record.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ").format(new Date())+
			dp.getAddress().getHostAddress()+"对我说："+new String(dp.getData(),0,dp.getLength())+"\r\n\r\n");
				}
			}catch (Exception e){
				record.append("接收包失败\r\n\r\n");
			}
		}
	}
}
