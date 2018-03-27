import java.net.*;
import java.util.*;
import java.io.*;

public class Clientthread implements Runnable {
	ServerSocket welcomeSocket;
	Socket clientConnect ;
	int port;
	static String dirname=System.getProperty("user.dir")+"/directory";
	static File f=new File(dirname);
	static File fl[]=f.listFiles();
	static int len=fl.length;
	static String[] fileList=new String[len];

	Clientthread(int _p){
		port=_p;
	}
	public static void updateDir(){
		f=new File(dirname);
		fl=f.listFiles();
		len=fl.length;
		fileList=new String[len];
		for(int i=0;i<fl.length;i++){
				fileList[i]=fl[i].getName();
		}
	}
	public void run() {
		for(int i=0;i<fl.length;i++){
				fileList[i]=fl[i].getName();
		}
		try {
			welcomeSocket = new ServerSocket(port);
		while(true) {
			System.out.println("I am Waiting for other clients to get files");
			clientConnect = welcomeSocket.accept();	
			DataOutputStream prw = new DataOutputStream(clientConnect.getOutputStream());
			BufferedReader inBuf = new BufferedReader(new InputStreamReader(clientConnect.getInputStream()));
			String checkFile = inBuf.readLine();
			System.out.println(checkFile);
			FileInputStream out = null;
			for(int i=0;i<fl.length;i++){
				if (checkFile.equals(fileList[i])){
					// From this part I have to send document 
					out = new FileInputStream(fl[i]);
					byte[] bFile = new byte[(int) fl[i].length()];
					out.read(bFile);
					prw.writeLong(fl[i].length());
					prw.flush();
                    prw.write(bFile,0,bFile.length);
                    prw.flush();
					System.out.println("File sent");
				}
			}	
			clientConnect.close();		
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
