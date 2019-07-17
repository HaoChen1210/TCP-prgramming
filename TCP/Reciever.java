package comp3331;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Random;
//sever
public class Reciever {
	private static final double LOSS_RATE = 0.3;
	private static final int AVERAGE_DELAY = 100; // milliseconds

	public static void main(String[] args) throws Exception {
		int port = Integer.parseInt("1025");//args[0]
		// Create random number generator for use in simulating
		// packet loss and network delay.
		Random random = new Random();
		// Create a datagram socket for receiving and sending UDP packets
		// through the port specified on the command line.
		DatagramSocket socket = new DatagramSocket(port);

		DatagramPacket first = new DatagramPacket(new byte[1024],1024);
		socket.receive(first);
	 
		int SYN = 1;
		int ACK = 1;
		SYN = SYN + ACK;//syn=2
		int data = SYN;
		byte[] buffer = new byte[1024];	
		
		
		
	}

}
