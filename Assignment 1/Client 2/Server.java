import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
	
	static String[][] myFileList = new String[3][100];// To store file names and ip addess
	static String iP;
	static String por="";
	static String clientIp;
	static int iterator=0;
	public static void main(String[] args) {
		// TODO Auto-generated method stub	
		while (true){
			try {
				ServerSocket welcomeSocket = new ServerSocket(50501); 
				System.out.println("Waiting for a client....");
				Socket clientConnect = welcomeSocket.accept(); // connect client to socket
				clientIp=clientConnect.getInetAddress().getHostAddress();
				System.out.println("A connection has been made with "+clientIp);			 
				String checkFunction; // To check which command is called by client
				String delims ="-"; // To seperate client put				
				BufferedReader inBuf = new BufferedReader(new InputStreamReader(clientConnect.getInputStream()));
				PrintWriter outBuf= new PrintWriter(new OutputStreamWriter(clientConnect.getOutputStream()),true);
				String clientport=inBuf.readLine();
				String name=inBuf.readLine();
				outBuf.flush();
				while (!(name.equals("-"))){
					myFileList[0][iterator]=name;
					myFileList[1][iterator]=clientIp;
					myFileList[2][iterator]=clientport;
					name=inBuf.readLine();
					iterator++;
					if (iterator >= myFileList[0].length) {
						updateArray();
					}
				}
				while (true) {
					checkFunction = inBuf.readLine();
					System.out.println(checkFunction);
					if (!(checkFunction.equals(null))) {
						if (checkFunction.contains("-")) { // collect first word
							String[] myWord = checkFunction.split(delims); // split client command/sentence into seperate words 
							if (myWord[0].equals("Lookup")) { // if first word is lookup
								int ch=lookUp(myWord[1]); // send second word (filename) in lookup function
								//System.out.println(ch);
								if (ch==1){
									outBuf.println(iP+"\n");
									outBuf.flush();
								}else{
									outBuf.println("-1");
									outBuf.flush();
								}
							} else if (myWord[0].equals("Get")) {
								int ch=get(myWord[1],myWord[2]); // send second word (filename) in lookup function
								//System.out.println(ch);
								if (ch==1){
									outBuf.println(por+"\n");
									outBuf.flush();
								}else{
									outBuf.println("-1");
									outBuf.flush();
								}
							} else if (myWord[0].equals("Put")) {
								name=myWord[1];
								myFileList[0][iterator]=name;
								myFileList[1][iterator]=clientIp;
								myFileList[2][iterator]=clientport;
								iterator++;
								System.out.println("One new file "+myWord[1]+" has been added");
								if (iterator >= myFileList[0].length) {
									updateArray();
								}

							}
						}else if (checkFunction.equals("End")) {
							break;
						}
					}
				}
				System.out.println("Client Disconnected");
				clientConnect.close();
				welcomeSocket.close();
			} catch (IOException e) {
				System.out.println("Client Disconnected Unexpectedly");
			}
		}
	}
	
	public static int lookUp(String fileName) {
		for (int i = 0; i < iterator; i++) {
			if (fileName.equals(myFileList[0][i])) {
				iP=myFileList[1][i]; // send ip address to client/output stream
				return 1;
			}
		}	
		return 0;
		// send -1 to client/outputstream
	}
	public static int get(String fileName , String ips) {
		int g=0;
		for (int i = 0; i < iterator; i++) {
			if (fileName.equals(myFileList[0][i])&& ips.equals(myFileList[1][i])) {
				por=por+myFileList[2][i]+" ";
				g++;
			}
		}	
		if (g!=0){
			return 1;
		}
		return 0;// send -1 to client/outputstream
	}
	public static void updateArray() {
		int oldSize = myFileList[0].length;
		int newSize = oldSize * 2;

		String[][] newFileList = new String[3][oldSize];
		for (int i = 0; i < oldSize; i++) {
			newFileList[0][i] = myFileList[0][i];
			newFileList[1][i] = myFileList[1][i];
			newFileList[2][i] = myFileList[2][i];
		}

		myFileList = new String[3][newSize];

		for (int i = 0; i < oldSize; i++) {
			myFileList[0][i] = newFileList[0][i];
			myFileList[1][i] = newFileList[1][i];
			myFileList[2][i] = newFileList[2][i];
		}

	}
}
