import java.io.*;
import java.net.*;
import java.util.*;

public class ServerBonus {
	
	static String[][] myFileList = new String[3][100];// To store file names and ip addess
	static ServerBThread[] arr=new ServerBThread[100];
	static int iterator=0;
	static int iter=0;
	static String clientIp;
	static ServerSocket welcomeSocket;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
				welcomeSocket= new ServerSocket(50501);
			 	while (true){
				 	System.out.println("Waiting for a client....");
					Socket clientConnect = welcomeSocket.accept(); // connect client to socket
					System.out.println("A connection has been made.");
					for (int i=0;i<iter;i++){
						String [][] myFileListtemp=arr[i].GetFileList();
						int tempit=myFileListtemp[0].length-myFileList[0].length;
						iterator=iterator+tempit;
						if (tempit!=0){
							for (int i1=myFileList[0].length;i1<myFileListtemp[0].length;i1++){
								if (iterator >= myFileList[0].length) {
									updateArray();
								}
								myFileList[0][i]=myFileListtemp[0][i];
								myFileList[1][i]=myFileListtemp[1][i];
								myFileList[2][i]=myFileListtemp[2][i];
							}
						}
					}
					clientIp=clientConnect.getInetAddress().getHostAddress();				
					BufferedReader inBuf = new BufferedReader(new InputStreamReader(clientConnect.getInputStream()));
					PrintWriter outBuf= new PrintWriter(new OutputStreamWriter(clientConnect.getOutputStream()),true);
					String clientport=inBuf.readLine();
					String name=inBuf.readLine();
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
					arr[iter]=new ServerBThread(clientConnect,clientport);
					for (int i=0;i<=iter;i++){
						arr[i].PutFileList(myFileList);
					}
					System.out.println("File List Updated");
					Thread T = new Thread(arr[iter]);
					T.start();
					iter++;
					if (iter >= arr.length) {
						updateArrayth();
					}
				} 	
			} catch (IOException e) {
				e.printStackTrace();
			}
			
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
	public static void updateArrayth() {
		int oldSize = arr.length;
		int newSize = oldSize * 2;

		ServerBThread[] newFileList = new ServerBThread[oldSize];
		for (int i = 0; i < oldSize; i++) {
			newFileList[i] = arr[i];
			newFileList[i] = arr[i];
			newFileList[i] = arr[i];
		}

		arr = new ServerBThread[newSize];

		for (int i = 0; i < oldSize; i++) {
			arr[i] = newFileList[i];
			arr[i] = newFileList[i];
			arr[i] = newFileList[i];
		}

	}
}
