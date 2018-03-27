import java.io.*;
import java.net.*;
import java.util.*;
import java.security.*;

public class DHT{

	public static ArrayList<String> list = new ArrayList<String>();
	public static String succ="e";
	public static String sIp;
	public static int sPort;
	public static String pred="e";
	public static String pIp;
	public static int pPort;
	public static String ip = "127.0.0.1";
	public static int port;
	public static int portNum;
	public static String ID;
	public static int end=1;
	public static ServerSocket ss;

	static String sha1(String input) throws Exception {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
       	}
         
        return sb.toString();
    }

	public static String getID()throws Exception{
		return sha1(ip+port);
	}

	public static void create()throws Exception{
		Scanner sc = new Scanner(System.in);
		System.out.print("\nPort number for server: ");
		port = sc.nextInt();
		ID = getID();
		new Thread(new Server(port,ID,ip)).start();
		System.out.println("Your ID is " + getID() + ".");
		System.out.println("Network created.\n");
		Client c1 = new Client(port);
		new Thread(c1).start();
	}

	public static void join(int portNum)throws Exception{
		Scanner sc = new Scanner(System.in);
		System.out.print("Port number for server: ");
		port = sc.nextInt();
		System.out.println("Your ID is " + getID() + ".");
		ID = getID();
		Thread T=new Thread(new Server(port,ID,ip));
		T.start();
		Client c1 = new Client("127.0.0.1",portNum);
		new Thread(c1).start();
		//System.out.println("Successfully joined the Network\n");
	}

	public static void main(String []args)throws Exception{

		Scanner sc = new Scanner(System.in);
		System.out.print("\nMenu\n\n1. Create a Network\n2. Join a Network\n\n");
		System.out.print("Select an option to continue: ");
		int option = sc.nextInt();

		if(option==1)
			create();
		else if(option==2){
			System.out.print("\nPort number of the node you know: ");
			portNum = sc.nextInt();
			join(portNum);
		}

	}
}