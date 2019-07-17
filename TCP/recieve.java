package comp3331;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;

//import Message;

/*
 * Server to process ping requests over UDP. 
 * The server sits in an infinite loop listening for incoming UDP packets. 
 * When a packet comes in, the server simply sends the encapsulated data back to the client.
 */

public class recieve {
	private static long time1;
	private static long time2;
	private static final double LOSS_RATE = 0.3;
	private static final int AVERAGE_DELAY = 100; // milliseconds
	private static String contentForPrint ;
	private static TreeMap<Integer, String> contentMap = new TreeMap<>();

	public static void main(String[] args) throws Exception {
		// Get command line argument.

		int port = Integer.parseInt("1035");

		// Create random number generator for use in simulating
		// packet loss and network delay.
		Random random = new Random();

		// Create a datagram socket for receiving and sending UDP packets
		// through the port specified on the command line.
		DatagramSocket socket = new DatagramSocket(port);
		DatagramPacket FirstHand = new DatagramPacket(new byte[1024], 1024);
		socket.receive(FirstHand);
		
		// message received.
		Message msg = new Message();
		msg.parse(new String(FirstHand.getData()));
		msg.printMsg();
		int randomInt = Math.abs(random.nextInt(100));
		
		System.out.println(new String(FirstHand.getData()));
		
		msg = new Message("rcv","SA",randomInt, 0,msg.getSequenceNum()+1, 1024);
		msg.printMsg();
		// send ack 
		DatagramPacket SecondHand = new DatagramPacket(msg.messageForSending().getBytes(),msg.messageForSending().getBytes().length, FirstHand.getAddress(), port+1);	
		socket.send(SecondHand);
		//msg.printMsg();
		DatagramPacket thirdHand = new DatagramPacket(new byte[1024], 1024);
		socket.receive(thirdHand);
		msg.printMsg();
		System.out.println(new String(thirdHand.getData()));
		
		/*
		InetAddress clientHost0 = FirstHand.getAddress();
		int clientPort0 = FirstHand.getPort();
		int sequenceNum = Math.abs(random.nextInt());
		*/

		//Message msg = new Message("S", sequenceNum, 0, 0, 1024);//
		
		//int ack = msg.getSequenceNum()+1;
		
     	//DatagramPacket SecondHand = new DatagramPacket(msg.messageForSending().getBytes(),msg.getlength(), clientHost0, clientPort0);	
     	//socket.send(SecondHand);
	
		    
     	      
     	//FileWriter fileWriter = new FileWriter("file1.txt");
    	//BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		StringBuilder sb = new StringBuilder();
		// recieve
		while (true) {
			// Create a datagram packet to hold incomming UDP packet.
			DatagramPacket request = new DatagramPacket(new byte[1024], 1024);
			socket.receive(request);
			printData(request);
			String s = new String(request.getData());
			//msg.printMsg();
			//System.out.println("content:"+s);
			msg = new Message();
			msg.parse(s);
			String str1 = msg.getContent().trim();
			msg.setContent(str1);
			
		//	bufferedWriter.write(msg.getContent());
		//	bufferedWriter.close();
			//System.out.println("====>"+(msg.getSequenceNum()+msg.getBytes()));
			System.out.println(msg.getContent().length());
			if(msg.getType().equals("F")){
				msg = new Message("rcv","FA",msg.getAck(), 0,msg.getSequenceNum()+msg.getBytes()+1, 1024);
			}
			else if(msg.getType().equals("A")){
				break;
			}
			else{
				
		    	String str = msg.getContent().replaceAll("\\s+$","");
		    	str = str.replaceAll("&","");
		    	
		    	int index = Integer.parseInt(str.split("=>")[0]);
		    	String content = str.split("=>")[1];
		    	
		    	contentMap.put(index, content);
		    	
		    	//System.out.println(str);
		    	msg.setContent(str); // get rid of &
		    	
		    	//System.out.println(msg.getContent());
		    	
		   
		    	
				msg = new Message("rcv","A",msg.getAck(), 0,msg.getSequenceNum()+msg.getBytes(), 1024);
				
			//	break;
			}
			
			DatagramPacket pkg = new DatagramPacket(msg.messageForSending().getBytes(),msg.messageForSending().getBytes().length, request.getAddress(), port+1);	
			socket.send(pkg);
			msg.printMsg();
			
			// write to file
			

			
			
			/*
			String s = new String(request.getData());
			//System.out.println(new String(request.getData()));
			String[] temp;
			temp = s.split("-",2);
			System.out.println(temp[0]);
			// Decide whether to reply, or simulate packet loss.
			if (random.nextDouble() < LOSS_RATE) {
				System.out.println("   Reply not sent.");
				continue;
			}

			// Simulate network delay.
			Thread.sleep((int) (random.nextDouble() * 2 * AVERAGE_DELAY));
			
			// Send reply.
			InetAddress clientHost = request.getAddress();
			int clientPort = request.getPort();
			byte[] buf = request.getData();
			DatagramPacket reply = new DatagramPacket(buf, buf.length, clientHost, clientPort);
			socket.send(reply);

			System.out.println("   Reply sent.");
			
			DatagramPacket finalHand = new DatagramPacket(new byte[1024], 1024);
			socket.receive(finalHand);
			
			DatagramPacket finalHand2 = new DatagramPacket(new byte[1024], 1024);
			socket.send(finalHand2);
			
			DatagramPacket finalHand3 = new DatagramPacket(new byte[1024], 1024);
			socket.receive(finalHand3);
			
			*/
		}
		
	 	FileWriter fileWriter = new FileWriter("file2.txt",true);
    	BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
    	for (Entry<Integer, String> entry: contentMap.entrySet()) {
    		//System.out.println(entry.getKey()+"=>"+entry.getValue());
    		bufferedWriter.write(entry.getValue());
		}
    	bufferedWriter.flush();
    	bufferedWriter.close();
		
	}

	/*
	 * Print ping data to the standard output stream.
	 */
	private static void writeFile(Message msg, int dropNumber){
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File("Sender_log.txt"), true)));
			
			pw.print(msg.msgForFile());
			//pw.write("\n");
			//pw.write("Number of (all) Packets Dropped  (by the PLD module): " + dropNumber );
			
			pw.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Write file error");
		}
		
	}
	
	private static void printData(DatagramPacket request) throws Exception {
		// Obtain references to the packet's array of bytes.
		byte[] buf = request.getData();

		// Wrap the bytes in a byte array input stream,
		// so that you can read the data as a stream of bytes.
		ByteArrayInputStream bais = new ByteArrayInputStream(buf);

		// Wrap the byte array output stream in an input stream reader,
		// so you can read the data as a stream of characters.
		InputStreamReader isr = new InputStreamReader(bais);

		// Wrap the input stream reader in a bufferred reader,
		// so you can read the character data a line at a time.
		// (A line is a sequence of chars terminated by any combination of \r
		// and \n.)
		BufferedReader br = new BufferedReader(isr);

		// The message data is contained in a single line, so read this line.
		String line = br.readLine();

		// Print host address and data received from it.
		//System.out.println(new String(line));
	}
}
