package comp3331;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

//import Message;

public class send {

	private static DatagramSocket socket;
	private static int MSS;
	private static int MWS;
	private static double pdrop;
	private static int timeout;
	private static long time1;
	private static long time2;
	private static int dropNumber = 0;
	private static int amountData=0;
	private static int needTotransimit=0;
	private static Random random;
	public static void main(String args[]) throws Exception {

		time1 = (new Date()).getTime();
		
		String ServerName = args[0];
		InetAddress ServerAddress = InetAddress.getByName(ServerName);
		int port = Integer.parseInt(args[1]);
		String fileName = args[2];
		MWS = Integer.parseInt(args[3]);
		MSS = Integer.parseInt(args[4]);
		timeout = Integer.parseInt(args[5]);
		pdrop = Double.parseDouble(args[6]);
		int seed = Integer.parseInt(args[7]);
		socket = new DatagramSocket(port + 1);
	    random = new Random(seed);

		int sequenceNum = Math.abs(random.nextInt(100));

		
		
		
		// setup message
		Message msg = new Message("snd", "S", sequenceNum, 0, 0, 0);
		
		//msg.setTime(time2-time1);
		sendMessage(msg, ServerAddress, port);
		
		// second
		socket.setSoTimeout(timeout);
		
		msg = receiveMessage();
		
		// third
		msg = new Message("snd", "A", msg.getAck(), 0, msg.getSequenceNum() + 1, 0);
		
		sendMessage(msg, ServerAddress, port);
		/*
		FileInputStream inputStream = new FileInputStream(fileName);
		byte[] buffer = new byte[MSS];
		int c = 0;
		String content = "";
		while ((c = inputStream.read(buffer, 0, MSS)) != -1) {
			content += new String(buffer).trim();
			buffer = new byte[MSS];
		}
		 */
		String content = "";
		Scanner fileIn = new Scanner(new File(fileName));
		//InputStream in= null;
		File file = new File(fileName);
		//System.out.println(file.length());
		while(fileIn.hasNextLine()){
			String line = fileIn.nextLine();
			content += line+"\n";
			//System.out.println(line);
		}
		content = content.substring(0, content.length()-1);
		fileIn.close();
		//System.out.println(content);

		int bytesCanSent = getBytesCanSent();

		String[] contentArr = getContentArray(content, bytesCanSent);

		int totalBytes = content.length();
		int lastBySent = msg.getSequenceNum();
		int lastByAcked = lastBySent;
		int index = 0;
		int ackNum = msg.getAck();
		ArrayList<Integer> pckList = new ArrayList<>();
		sequenceNum = msg.getSequenceNum();
		int lastack = sequenceNum;
		int lastSquence = ackNum;
		int lastPckBytes = 0;
		
		while(totalBytes > 0){
			int count_received = 0;
			pckList.clear();
			while(lastBySent - lastByAcked <= MWS){
				if(bytesCanSent < totalBytes){
					msg = new Message("snd", "D", sequenceNum + index * bytesCanSent, bytesCanSent, ackNum , 0, index+"=>"+contentArr[index]+"&");
				}
				else{
					msg = new Message("snd", "D", sequenceNum + index * bytesCanSent + lastPckBytes, lastPckBytes, ackNum , 0, index+"=>"+contentArr[index]+"&");
				}
				//System.out.println("content sent:"+contentArr[index]);
				
				lastBySent += contentArr[index].length();
				index++;

				sendMessage(msg, ServerAddress, port);
				//System.out.println(lastBySent+" == "+ lastByAcked);
				pckList.add(msg.getSequenceNum()+bytesCanSent);
				
				if(bytesCanSent >= totalBytes){
					lastPckBytes = totalBytes;
				}
				if(lastBySent >= sequenceNum + totalBytes){
					break;
				}
				
			}
			// wating ack 
			
			while(true){
				
				socket.setSoTimeout(timeout);
				
				msg = receiveMessage();
				
				if(msg!=null){
					lastack = msg.getSequenceNum();
					lastSquence = msg.getAck();
					count_received++;
					for(int i = 0; i < pckList.size(); i++){
						if(pckList.get(i) == msg.getAck()){
							pckList.set(i, 0);
						}
					}
				}
				else if(count_received == pckList.size()) break;
				else if(msg == null) break;
				needTotransimit++;
			}
			
			
			int index_not_received = -1;
			for(int i = 0; i < pckList.size(); i++){
				if(pckList.get(i) >0){
					index_not_received = i;
					break;
				}
			}
			
			if(index_not_received != -1){
				lastByAcked += index_not_received * bytesCanSent;
				lastBySent -= (pckList.size()-index_not_received) * bytesCanSent;
				index -= pckList.size()-index_not_received;
			}
			else{
				lastByAcked += count_received * bytesCanSent;
			}
			
			// substract total bytes
			//if(count_received*bytesCanSent > totalBytes)
				
			totalBytes -= count_received*bytesCanSent;
			
			//System.out.println("total bytes:"+totalBytes);
		}






		/*
			// time

			System.out.println(content);
			System.out.println();
			if (c != MSS) {
				content = content.substring(0, c);
				content.replace("\0", "END;");
			}
			String END = "END;";
			if(random.nextDouble() >= pdrop)
		    {
				msg = new Message("snd", "D", msg.getAck(), c, msg.getSequenceNum() , 0,content);
				sendMessage(msg, ServerAddress, port);

		    }
			//int packetSize = content.length();

			//msg = new Message("snd", "D", msg.getAck(), 0, msg.getSequenceNum() , 0);
		}
		 */
		//end
		msg = new Message("snd", "F", lastSquence, 0, lastack, 0);
		sendMessage(msg, ServerAddress, port);

		//end2
		socket.setSoTimeout(timeout);
		msg = receiveMessage();

		//msg = new Message("rcv", "FA", sequenceNum, 0, 0, 0);
		//sendMessage(msg, ServerAddress, port);

		//end3
		msg = new Message("snd", "A", msg.getAck(), 0, msg.getSequenceNum()+1, 0);
		sendMessage(msg, ServerAddress, port);
		writeFile2(dropNumber,file.length(),amountData,needTotransimit);

		// receive "SA"

		/*
		 * 
		 * //int tries = 0; while( true) { byte[] buf = new byte[1024]; Date now
		 * = new Date(); long SendTime = now.getTime(); String s = "PING " + " "
		 * + SendTime + "\n"; buf = s.getBytes(); DatagramPacket ping = new
		 * DatagramPacket(buf, buf.length, ServerAddress, Port);
		 * Sender.send(ping); try { Sender.setSoTimeout(Timeout); DatagramPacket
		 * PacketBack = new DatagramPacket(new byte[1024], 1024);
		 * Sender.receive(PacketBack);
		 * if(!PacketBack.getAddress().equals(ServerAddress)){ //error throw new
		 * IOException("Received packet from an umknown source"); } now = new
		 * Date(); long ReceiveTime = now.getTime(); long rtt = ReceiveTime -
		 * SendTime; System.out.println("ping to " + ServerAddress + ", seq = "
		 * + tries + ", rtt = " + rtt + "ms"); } catch (IOException e) {
		 * System.out.println("Timeout for Packet" + tries); } //tries++; }
		 */
	}



	private static String[] getContentArray(String content, int bytesCanSent) {
		int size = content.length()/bytesCanSent;

		int last = size*bytesCanSent;

		String[] strArr = new String[size+1];
		int i = 0;

		while(i < size){

			strArr[i] = content.substring(i*bytesCanSent, (i+1)*bytesCanSent);

			//System.out.println(i+"==>"+strArr[i]+"===");
			i++;
			//ii()
		}
		amountData=i;
		//System.out.println(last);
		if(last != content.length()){
			strArr[i] = content.substring(last, content.length());
			//System.out.println(last);
		}
		//System.out.println(last);
		return strArr;
	}



	private static int getBytesCanSent() {
		if(MWS > MSS)
			return MSS;
		else
			return MWS;
	}



	private static void sendMessage(Message msg, InetAddress ServerAddress, int port) {
		//needTotransimit++;
		time2 = (new Date()).getTime();
		msg.setTime(time2-time1);
		
		DatagramPacket pkg = new DatagramPacket(msg.messageForSending().getBytes(),
				msg.messageForSending().getBytes().length, ServerAddress, port);
		try {
			//msg.setTime(Date1);
			//(msg.getType().equals("D"))? true:false;//
			boolean drop = ( random.nextDouble() <= pdrop && msg.getType().equals("D"))? true:false;
			if(drop){
				//msg = new Message("drop", "D", sequenceNum + times * bytesCanSent, bytesCanSent, msg.getAck() , 0);
				msg.setTitle("drop");
				dropNumber++;
				//msg.setDropNumber();
				
			}
			msg.printMsg();
			writeFile(msg,dropNumber);
			if(!drop) socket.send(pkg);
			
		} catch (IOException e) {
			e.printStackTrace();
			//System.out.println("Connection fail!");
			//System.exit(0);
		}
		//long Date2 = System.nanoTime();
		
		
		
	}

	private static Message receiveMessage() {
		
		
		//long Date1 = /(10*10*10*10*10*10);
		DatagramPacket pkg = new DatagramPacket(new byte[1024], 1024);
		
		try {
			socket.setSoTimeout(timeout);
			//timeOutNumber++;
			socket.receive(pkg);
			Message msg = new Message();
			msg.parse(new String(pkg.getData()));
			//long Date2 = System.nanoTime();
			//msg.setTime(Date2 - Date1);
		
			time2 = (new Date()).getTime();
			msg.setTime(time2-time1);
			msg.printMsg();
			writeFile(msg,dropNumber);
			return msg;
		} catch (IOException e) {
			//System.out.println("no received");
			return null;
		}
	}
	
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
/*	private static void writeFile2( int dropNumber) throws IOException{
		FileWriter fileWriter = new FileWriter("Sender_log.txt",true);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		bufferedWriter.write("ttt\n");
		bufferedWriter.write("Number of (all) Packets Dropped  (by the PLD module): " + dropNumber );
		//bufferedWriter.flush();
		bufferedWriter.close();
	}
	
*/
	private static void writeFile2( int dropNumber,long amount,int amountData,int needTotransimit) {
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File("Sender_log.txt"), true)));
			
			
			pw.write("\n");
			pw.write("Amount of (original) Data Transferred  " + amount );
			pw.write("\n");
			pw.write("Number of Data Segments Sent " + (amountData - dropNumber) );
			pw.write("\n");
			pw.write("Number of (all) Packets Dropped  (by the PLD module): " + dropNumber );

			pw.write("\n");
			pw.write("Number of Retransmitted Segments: " + (needTotransimit ) );
			pw.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Write file error");
		}
	}
}
class Message {

	private String title;
	private int sequenceNum;
	private int bytes;
	private int ackNum;
	private String type;
	private double time;
	private int length;
	private String delimeter = "-";
	private String content="";
	private int dropNumber;
	
	public int getDropNumber() {
		return dropNumber;
	}

	public void setDropNumber(int dropNumber) {
		this.dropNumber = dropNumber;
	}

	public String getDelimeter() {
		return delimeter;
	}

	public void setDelimeter(String delimeter) {
		this.delimeter = delimeter;
	}

	public Message() {

	}
	
	public Message(String title, String type, int sequenceNum, int bytes, int ack, int len){
		this.title = title;
		this.type = type;
		this.sequenceNum = sequenceNum;
		this.bytes = bytes;
		this.ackNum = ack;
		this.length = len;
	}
	public Message(String title, String type, int sequenceNum, int bytes, int ack, int len, String content){
		this.title = title;
		this.type = type;
		this.sequenceNum = sequenceNum;
		this.bytes = bytes;
		this.ackNum = ack;
		this.length = len;
		this.content = content;
	}
	
	
	
	// parse String format: type+","+sequenceNum+","+bytes+","+"ack"
	public void parse(String parseString){
		//System.out.println(parseString+"---"+parseString.split(",")[3]);
		String[] str = parseString.split("<");
		//System.out.println(str.length);
		title = parseString.split("<")[0];
		type = parseString.split("<")[1];
		sequenceNum = Integer.parseInt(parseString.split("<")[2]);
		bytes = Integer.parseInt(parseString.split("<")[3]);
		ackNum = Integer.parseInt(str[4]);
		if(str.length >=6){
			content = str[5];
		}
		//String t = str[4];
	}
	
	public void printMsg(){
		System.out.println(title+"\t"+time+"\t"+type+"\t"+sequenceNum+"\t"+bytes+"\t"+ackNum);
	}
	
	public String msgForFile(){
		return title+"\t"+time+"\t"+type+"\t"+sequenceNum+"\t"+bytes+"\t"+ackNum+"\n";
	}
	
	
	public int getlength() {
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	public String messageForSending(){
		return title+"<"+type+"<"+sequenceNum+"<"+bytes+"<"+ackNum+"<"+content;//q
	}
	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getSequenceNum() {
		return sequenceNum;
	}

	public void setSequenceNum(int sequenceNum) {
		this.sequenceNum = sequenceNum;
	}

	public int getBytes() {
		return bytes;
	}

	public void setBytes(int bytes) {
		this.bytes = bytes;
	}

	public int getAck() {
		return ackNum;
	}

	public void setAck(int ack) {
		this.ackNum = ack;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String str){
		this.content = str;
	}
	

}

