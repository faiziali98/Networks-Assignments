import java.util.*;
import java.net.*;
import java.io.*;

public class Client {
	public static void main(String args[]) {
		// Takes command line arguments
		
		try {
			// Connect to the given IP and Port and store in the sock variable
			Scanner user1 = new Scanner(System.in);
			System.out.println("Type the Ip of desired Server");			
			String ipods=user1.nextLine();
			System.out.println("Type the port of desired server");
			int pods=user1.nextInt();
			System.out.println("Type the port where other users can join with you");
			int portc=user1.nextInt();

			Socket sock = new Socket(InetAddress.getByName(ipods), pods);
			System.out.println("Connected to server.");
			
			//for the output
			PrintWriter pw= new PrintWriter(new OutputStreamWriter(sock.getOutputStream()),true);
			
			//for file list
			String dirname=System.getProperty("user.dir")+"/directory";
			//int portc = Integer.parseInt(args[0]);
			String ports=Integer.toString(portc);
			File f=new File(dirname);
			File fl[]=f.listFiles();
			pw.println(ports);
			pw.flush();
			//for sending file names
			for(int i=0;i<fl.length;i++){
				pw.println(fl[i].getName());
			}
			pw.println("-");
			pw.flush();
			Clientthread clientfile=new Clientthread(portc);
			Thread T=new Thread(clientfile);
			T.start();
			String retIp=null;
			
			// Taking input from socket
			while (true){
				Scanner fromUser = new Scanner(System.in);
				String output=fromUser.nextLine();
				PrintWriter prw= new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
				InputStreamReader isr = new InputStreamReader(sock.getInputStream());
				BufferedReader br = new BufferedReader(isr);
				String[] myWord = output.split("-");
				if (output.contains("-")){
					if (myWord[0].equals("Lookup")){
						prw.println(output);
						prw.flush();		
						retIp= br.readLine();
						System.out.println(retIp);
					}if (myWord[0].equals("Get")){	
						prw.println(output);
						prw.flush();					
						String input = br.readLine();
						System.out.println(input);
						if (!input.equals ("-1")) {
							System.out.println("Input put port number of where to get file");
							int pors=fromUser.nextInt();
							Socket socks = new Socket(InetAddress.getByName(retIp), pors);
							PrintWriter prwc= new PrintWriter(new OutputStreamWriter(socks.getOutputStream()));
							DataInputStream is = new DataInputStream(socks.getInputStream());
							prwc.println(myWord[1]);
							prwc.flush();
							Long num=is.readLong();
							byte[] b=new byte[num.intValue()];
							is.read(b,0,b.length);
							System.out.println("Type where to put files");
							Scanner fUser = new Scanner(System.in);
							fUser = new Scanner(System.in);
							FileOutputStream ff=new FileOutputStream(System.getProperty("user.dir")+"/"+fUser.nextLine()+"/"+myWord[1]);
							ff.write(b);
							System.out.println("File recieved");
							socks.close();
						} else {
							System.out.println("File not found");
						}
					} if (myWord[0].equals("Put")) {
						byte[] bFile = myWord[2].getBytes();
						FileOutputStream ff=new FileOutputStream(System.getProperty("user.dir")+"/directory/"+myWord[1]);
						ff.write(bFile);
						pw.println(output);
						pw.flush();
						System.out.println("File has been put");
						clientfile.updateDir();
					}
				}
				else if (output.equals("End")){
					prw.println(output);
					prw.flush();
					System.out.println("Dissconnected from server but waiting for Clients to connect");
					break;
				}
			}
			sock.close();
		} catch (Exception e) {
			// Exception printed on console in case of error
			e.printStackTrace();
		}
	}

}
