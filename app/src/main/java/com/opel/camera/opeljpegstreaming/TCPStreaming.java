package com.opel.camera.opeljpegstreaming;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import android.os.Handler;

public class TCPStreaming extends Thread {
	private Socket tcpSocket;
	private SocketAddress sock_addr;
	private InputStream mInStream;
	String ip;
	int port;
	private short stat;
	static public short STAT_DISCON = 0;
	static public short STAT_CONNECTING = 1;
	static public short STAT_CONNECTED = 2;

	static public final int MSG_TYPE_STAT_CHANGED = 0;
	static public final int MSG_TYPE_STAT_READ = 1;
	private Handler mHandler;
	
	public TCPStreaming(String ip, int port, Handler handler){
		mHandler = handler;
		this.ip = new String(ip);
		this.port = port;
		stat = STAT_DISCON;
		//Socket tmpSock = null;
		sock_addr = null;
		/*try {
			Log.d("What the", "Msg1");
			tmpSock = new Socket(ip, port);
			sock_addr = tmpSock.getRemoteSocketAddress();
			if(tmpSock.isConnected()){
				stat = STAT_CONNECTED;
				mInStream = tmpSock.getInputStream();
				onStatChanged(stat);
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
		tcpSocket = tmpSock;
		*/
	}

	public void connect(){
		Socket tmpSock = null;
		try {
			Log.d("What the", "Msg1");
			tmpSock = new Socket(ip, port);
			sock_addr = tmpSock.getRemoteSocketAddress();
			if(tmpSock.isConnected()){
				stat = STAT_CONNECTED;
				mInStream = tmpSock.getInputStream();
				onStatChanged(stat);
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tcpSocket = tmpSock;
	}
	public void connect(String ip, int port){
		if(stat != STAT_DISCON){
			Log.d("TCPStreaming", "Already connecting or connected");
			return;
		}
		if(tcpSocket == null){
			Socket tmpSock = null;
			try {
				stat = STAT_CONNECTING;
				tmpSock = new Socket(ip, port);
				sock_addr = tmpSock.getRemoteSocketAddress();
				if(tmpSock.isConnected()){
					stat = STAT_CONNECTED;
					mInStream = tmpSock.getInputStream();
					onStatChanged(stat);
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();			
			}
			tcpSocket = tmpSock;
		}
		else{
			stat = STAT_CONNECTING;
			try {				
				tcpSocket.connect(sock_addr);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(tcpSocket.isConnected()){
				stat = STAT_CONNECTED;
				try {
					mInStream = tcpSocket.getInputStream();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				onStatChanged(stat);
			}
		}
		
	}
	public void onStatChanged(short stat){
		/*
		 * Should implement this
		 */

	}
	public void onReceived(byte[] frame){
		/*
		 * Should implement this
		 */
		mHandler.obtainMessage(MSG_TYPE_STAT_READ, frame).sendToTarget();


	}
	
	public void run(){

		short prev_stat = stat;
		while(true){
			if(tcpSocket == null) {
				connect();
				continue;
			}
			if(prev_stat != stat){
				prev_stat = stat;
				onStatChanged(stat);
			}
			byte[] frame = null;
			byte[] buff = new byte[1460];
			while(stat == STAT_CONNECTED){
				if(tcpSocket.isConnected() == false){
					stat = STAT_DISCON;
					break;					
				}

				
				try {
					int bts = 0;
					int off = 0;
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					DataInputStream bis = new DataInputStream(mInStream);
					bis.readFully(buff, 0, 1460);
					byte[] lengthBuf = Arrays.copyOfRange(buff, 0, 4);
					byte[] offsetBuf = Arrays.copyOfRange(buff, 4, 8); 
					ByteOrder order = ByteOrder.BIG_ENDIAN;
					ByteBuffer buf = ByteBuffer.allocate(4);
					buf.order(order);
									
					buf.put(lengthBuf);
					buf.flip();
					
					int totalLen = buf.getInt();
					
					buf.clear();
					buf.order(order);
					buf.allocate(4);
					buf.put(offsetBuf);
					buf.flip();
					
					int offset = buf.getInt();
					Log.d("TCPStreaming", offset+"/"+totalLen);
					if(frame == null){
						if(offset != 0)
							Log.d("TCPStreaming", offset+" ???!!");
						frame = new byte[totalLen];
					}
					int len = (offset+1452 <= totalLen)? 1452:totalLen-offset; 
					
					for(int i=0; i<len; i++){
						frame[offset+i] = buff[8+i];
					}
					
					if(totalLen <= offset+1452){
						onReceived(frame);
						frame = null;
					}
					
					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			try {
				sleep(3000, 0);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
