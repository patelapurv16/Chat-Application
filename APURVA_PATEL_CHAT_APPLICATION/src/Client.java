import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JFileChooser; 


public class Client {
	
	
	static Socket socket;

	//Setting up the GUI Window
	static JFrame window_chat = new JFrame("ISUCHAT"); 
	
	// Text Area 
	static JTextArea text_area = new JTextArea(22,40); 
	
	// Text Field
	static JTextField text_Field = new JTextField(40); 
	
	//Label area between the chat area and text field. 
	static JLabel display_white = new JLabel("    ");
	
	static JFileChooser fileChooser= new JFileChooser();
	//Button
	static JButton send_button = new JButton("Send"); 
	static JButton send_pic;
	static JButton delete_mess = new JButton("Delete Message"); 
	static JButton pre_message = new JButton("Previous Message");
	
	static BufferedReader in; 
	static PrintWriter out; 
	static int returnVal; 

	static File file; 
	static String filename; 
	
	static DataOutputStream os;
	static FileInputStream fis; 

	
	Client(){
		
		try {
			socket = new Socket("localhost",4444);
			
		}catch(Exception e) { 
			System.err.println(e);
		}
		
		send_pic = new JButton("PICTURE");
		
		/*
		 * Adding components to the GUI Frame
		 */
		
		//Setting a layout
		window_chat.setLayout(new FlowLayout());
		
		//Adding a scroller, in order to scroll down and up
		window_chat.add(new JScrollPane(text_area)); 
		
		//Adding the text field to the frame
		window_chat.add(text_Field);
		
		//Adding the display_white
		window_chat.add(display_white);
		
		//Adding Send Button on the frame
		window_chat.add(send_button);
		
		//Adding PIC Button on the frame
		window_chat.add(send_pic);
		
		//Adding Delete Button on the frame
		window_chat.add(delete_mess);
		
		//Closes the application
		window_chat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//set the size of the screen
		window_chat.setSize(475, 500);
		
		//setting the visibility of the screen
		window_chat.setVisible(true);
		
		//setting previous message button
		window_chat.add(pre_message);
		
		/*
		 * No client can edit the screen, unless it makes a successful connection with the server
		 * Once the client makes a successful connection, it will be set to true and the client
		 * type in the message. 
		 */
		text_area.setEditable(false);
		
		text_area.setEditable(false);
		
		/* When the user clicks on send button. it sends the message
		 * when the user enters, it sends the message
		 */
		
	
		send_button.addActionListener(new Listener());
		text_Field.addActionListener(new Listener());
		send_pic.addActionListener(new Listener());
		delete_mess.addActionListener(new Listener());
		pre_message.addActionListener(new Listener());
		
		
		
	}
	
	void chatting() throws Exception { 
		
		/*
		 * Display a dialog for the client to input the ip address it want to connect it with. 
		 * Ip address is necessary for the connection to be made 
		 */
		String IPADDRESS = JOptionPane.showInputDialog(window_chat, "Enter IP Address: ", "IP Address Required!", JOptionPane.PLAIN_MESSAGE);
		Socket socket = new Socket(IPADDRESS, 4444); 
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(),true); 
		
		while(true) { 
			String str = in.readLine(); 
//			System.out.println("STRING: "+str);
			if (str.equals("Enter your name")) {
				String NAME = JOptionPane.showInputDialog(window_chat, "Enter a Unique Name: ", "Name Required!", JOptionPane.PLAIN_MESSAGE);
				out.println(NAME);
			}else if(str.equals("Please, use a Unique name: The name you entered already exists")) {
				String NAME = JOptionPane.showInputDialog(window_chat, "Enter a another name: ", "Name Already Exists!", JOptionPane.WARNING_MESSAGE);
				out.println(NAME);
			}else if(str.equals("Name Accepted!")) {
				
				text_Field.setEditable(true); 
			}else {
				text_area.append(str+"\n");
				if(str.contains(".jpg")|| str.contains(".png")||str.contains(".JPG")||str.contains(".PNG")) {
//					System.out.println("GOT THE FILE: "+ Client.filename); 
					File new_file = new File(Client.filename);
//					System.out.println("New File Created"); 
					byte[] bytes = new byte[(int)new_file.length()];
//					System.out.println("New array Created: ");
					FileInputStream fis = new FileInputStream(new_file); 
//					System.out.println("FileInputStream: " + fis); 
					BufferedInputStream bis = new BufferedInputStream(fis); 
//					System.out.println("BufferedInputStream: "+ bis); 
					DataInputStream dis = new DataInputStream(bis);
//					System.out.println("Writing Data to the Socket: "+ dis);
					dis.readFully(bytes, 0, bytes.length);
//					System.out.println("Read the file");
					OutputStream os = Client.socket.getOutputStream();
//					System.out.println("OutputStream: "+ os);
					DataOutputStream dos = new DataOutputStream(os);
//					System.out.println("Sending the file: "+ dos); 
					dos.writeUTF(new_file.getName());
//					System.out.println("File Name: Writting"); 
					dos.writeLong(bytes.length);
//					System.out.println("Writting Bytes");
					dos.write(bytes,0,bytes.length);
					dos.flush();
//					System.out.println("Sending the Stream: FLUSH FLUSH FLUSH");
				}
			}
			
		}
		
	}

	
	public static void main(String[] args) { 
		/*
		 * Try to connect to the SEVRER
		 */
		//Client client = new Client(); 
		
		try {
			System.out.println("CLIENT STARTING TO CONNECT....");
			Socket clientSoc = new Socket("localhost",4444);
			Client client = new Client(); 
			client.chatting();
			clientSoc.close(); 
			
		}  catch (IOException e) {
			
			System.err.println("SYSTEM FAILED TO CONNECT:");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}


class Listener implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()== Client.send_button|| e.getSource()== Client.text_Field) {
			Client.out.println(Client.text_Field.getText());
			Client.text_Field.setText("");
		}
		if(e.getSource()==Client.send_pic ) { 
			Client.returnVal = Client.fileChooser.showOpenDialog(null); 
				if(Client.returnVal== JFileChooser.APPROVE_OPTION) {
					Client.file = Client.fileChooser.getSelectedFile(); 
					Client.filename= Client.file.getAbsolutePath(); 
					Client.out.println(Client.filename);
					Client.text_Field.setText("");
				}
		}
		if(e.getSource() == Client.pre_message) {
			
			
			try {
				FileReader reader = new FileReader("Chat.txt");
				BufferedReader br = new BufferedReader(reader);
				Client.text_area.read(br, null);
				br.close();
				Client.text_area.requestFocus();
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		}
	}
}
		

