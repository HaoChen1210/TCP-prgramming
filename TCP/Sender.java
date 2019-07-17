package comp3331;

import java.net.*;
import java.io.*;
import java.util.*;

//client
public class Sender {
	public static void main(String[] args) throws Exception {

		String ServerName = "127.0.0.1";
		// System.out.println("ttt");
		InetAddress ServerAddress = InetAddress.getByName(ServerName);
		System.out.println(ServerAddress);
		int Port = Integer.parseInt("1026");
		String fileName = "file.txt";
		int MWS = Integer.parseInt("10");
		int MSS = Integer.parseInt("10");
		int Timeout = Integer.parseInt("10");
		double pdrop = Double.parseDouble("0.5");
		int seed = Integer.parseInt("5");
		DatagramSocket sender = new DatagramSocket(Port + 1);
		Random random = new Random(seed);
		int SYN = 0;
		int ACK = 0;
		int FIN = 0;
		String Seq = "";
		SYN = 1;
		byte[] buffer = new byte[1024];
		String data = SYN + Seq + ACK;
		buffer = data.getBytes();
		System.out.println(buffer);
		System.out.println("ttt");
		// three way handshke
		long time1 = System.nanoTime();
		DatagramPacket first = new DatagramPacket(buffer, buffer.length, ServerAddress, Port);
		sender.send(first);
		long time2 = System.nanoTime();
		float Time = time2 - time1;
		System.out.println("time:"+Time);

		try {
			long time3 = System.nanoTime();
			sender.setSoTimeout(Timeout);
			DatagramPacket second = new DatagramPacket(new byte[1024], 1024);
			sender.receive(second);
			if (!second.getAddress().equals(ServerAddress)) { // error
				throw new IOException("Received packet from an umknown source");
			}
			long time4 =System.nanoTime();
			float Time1 = time4 - time3;
			System.out.println("time1:"+Time1);
			// now = new Date();
			// long ReceiveTime = now.getTime();
			// long rtt = ReceiveTime - SendTime;
			System.out.println();
		} catch (IOException e) {
			System.out.println("Connection fail!");
		}
		System.out.println("Connection Established!");
		
		byte[] buffer2 = new byte[1024];	    

		SYN = 0;
		ACK = 1;
		//FIN = 0;
		data = SYN + ACK + Seq;
		
	}


}
