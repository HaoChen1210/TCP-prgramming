package comp3331;

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.Runtime;



class WebServer {
	public static void main(String[] args) throws Exception {
		int port = 1025;
		//int port = Integer.parseInt(args[0]);
	    ServerSocket server = new ServerSocket(port);
	    System.out.println("Server connect on port : " + port);
	    while (true) {
	        Socket client = server.accept();
	        System.out.println("connected");
	        BufferedReader inPut = new BufferedReader(new InputStreamReader(client.getInputStream()));
	        BufferedWriter outPut = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
	        String data = inPut.readLine();
	        String[] line0 = data.split(" ");
			String fileName = line0[1]; 
			fileName = fileName.substring(1);			
        	outPut.write("HTTP/1.1 200 OK\r\n");
        	outPut.write("Content-Type: text/html\r\n"); 
        	outPut.write("Connection: keep-alive\r\n"); 
        	outPut.write("\r\n");  
	        try{
	        	
	        	File file = new File(fileName);
	        	FileReader filereader = new FileReader(file);
	        	BufferedReader reader = new BufferedReader(filereader);	        
	        	String line;
				while((line = reader.readLine()) != null){
					outPut.write(line+"\r\n");
				}
				
	        } catch (FileNotFoundException e) { //when the file cannot present, print the error message
	        	outPut.write("<TITLE>404 Not Found</TITLE>");
	        	outPut.write("<h1>404 Not Found</h1>");

			} catch (IOException e) {
				outPut.write("<TITLE>404 Not Found</TITLE>");
	        	outPut.write("<h1>404 Not Found</h1>");

			}   
			
	        outPut.close();
	        inPut.close();
	        client.close();
	    }
	
	}
    public void deal(){
		
	}
}