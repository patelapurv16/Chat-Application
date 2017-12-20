import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	
	/*
	 * Making an arrayList to check if the user enters a unique name
	 */
	static ArrayList<String> USERNAME = new ArrayList<String>();
	static int MESSAGE_NUMBER = 0; 
	static File file; 
	static int IMAGE_NUMBER = 0;
	/*
	 * To save the messages from the clients, so that I can broadcast to other clients
	 */
	static ArrayList<PrintWriter> MESSAGE_ARRAY = new ArrayList<PrintWriter>();
	public static void main(String[] args) { 
		int PORT_NUMBER = 4444;
		
		/*
		 * Accepting INCOMING CLIENT CONNECTION
		 */
		int clientNumber = 0;
		try {
			System.out.println("WAITING FOR CLIENTS TO CONNECT.....");
			ServerSocket serVer = new ServerSocket(PORT_NUMBER);
			//int clientNumber = 0; 
			while(true) { 
			
			/*
			 * This will accept the incoming client connection
			 *
			 */
			Socket socket = serVer.accept();
					
			
			/*Starting the thread for every client connection*/
			Handler handler = new Handler(socket); 
			handler.start(); 
			
			//System.out.println("CLIENT "+clientNumber+" CONNECTION ACCEPTED....");
			//clientNumber= clientNumber+1; 
			}
			
		} catch (IOException e) {
			
			System.err.println("SERVER CONNECTION FAILED TO ESTABLISHED");;
		} 
		
		
	}
}
class Handler extends Thread{
	Socket soc; 
	BufferedReader in; 
	PrintWriter out; 
	PrintWriter file; 
	FileWriter file_Write ;
	BufferedWriter fWrite; 
	String name; 
	String fileName; 
	InputStream input = null; 
	OutputStream output = null;
	FileOutputStream fileOut = null; 
	
	public Handler(Socket soc) throws IOException{
		this.soc = soc; 
		file_Write= new FileWriter("Chat.txt", true); 
		fWrite = new BufferedWriter(file_Write);
		file = new PrintWriter(fWrite,true); 
	}
	public void sentImage() throws IOException {
		int bytesRead; 
//		System.out.println("My byteReader is on!");
		int current = 0; 
//		System.out.println("Current Counter is on too");
		InputStream in = soc.getInputStream();
//		System.out.println("Get  Up: "+ in); 
		DataInputStream clientData = new DataInputStream(in); 
//		System.out.println("GETTING HACK!!!! OBTAINED THE DATA");
		fileName =clientData.readUTF();
		OutputStream output = new DataOutputStream(soc.getOutputStream());
//		System.out.println("GOt the file name: ");
		long size = clientData.readLong(); 
//		System.out.println("Reading the longs");
		byte[] buffer = new byte[1024];
//		System.out.println("BUFFERING NOW");
		while(size > 0 &&(bytesRead = clientData.read(buffer, 0, (int)Math.min(buffer.length,  size)))!= -1) {
			output.write(buffer, 0, bytesRead);
			size -= bytesRead;
		}
		
	}
	public void run() { 
		try {
			// For reading data from the socket
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));		
			out = new PrintWriter(soc.getOutputStream(), true);
			
			
			int count = 0; 
			while(true) { 
				if(count > 0) { 
					out.println("Please, use a Unique name: The name you entered already exists");
					
				}else {
					out.println("Enter your name"); 
				}
				
				name = in.readLine(); 
//				System.out.println("STRING: "+ name);
				
				if (name == null) { 
					return;
				}
				if(!Server.USERNAME.contains(name)) {
					Server.USERNAME.add(name);
					break;
				}
				count++; 
				
				
			}
			out.println(name +": Accepted!"); 
			Server.MESSAGE_ARRAY.add(out);
			
			
		while(true) { 
				String MESSAGE = in.readLine();
				System.out.println(name+": "+ MESSAGE); 
				if(MESSAGE==null) {
					return;
				}
				
				else if(MESSAGE.contains(".jpg")||MESSAGE.contains(".JPG")||MESSAGE.contains(".png")||MESSAGE.contains(".PNG")){
					sentImage(); 

				}

				else {
					file.println(Server.MESSAGE_NUMBER+": " + name+": "+ MESSAGE); 
					Server.MESSAGE_NUMBER = Server.MESSAGE_NUMBER + 1; 
					for (PrintWriter writer : Server.MESSAGE_ARRAY) {
						writer.println( name+": "+ MESSAGE);
					}


			}
		}
		}catch(Exception e) {
			System.err.println(e.getStackTrace());
		}
	}
}
			
