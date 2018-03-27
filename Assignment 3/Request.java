import java.io.*;
import java.net.*;
import java.util.*;
import java.security.*;

public class Request {

	static int portSer;
	static int port;
	static int magic = 15440;
	static String sha1(String input) throws Exception {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
       	}
         
        return sb.toString();
    }

    public static String getID(String word)throws Exception{
		return sha1(word);
	}

	public static void sendMessage(String _ip,int _port,String message){ 
        try{
	         DatagramSocket socket = new DatagramSocket() ;
	         byte [] data = message.getBytes();
	         DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(_ip), _port);
	         socket.send(packet);
      	}
      	catch(Exception e){
         	System.out.println(e);
      	}
	}

	public static void sendRequest(String hash){ 
		String message=magic+" Client 127.0.0.1 "+port+" "+hash;
        try{
	         DatagramSocket socket = new DatagramSocket() ;
	         byte [] data = message.getBytes();
	         DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName("127.0.0.1"), 50505);
	         socket.send(packet);
      	}
      	catch(Exception e){
         	System.out.println(e);
      	}
	}
	public static void GetAnswer(){ 
		new Thread(()->{
			try{
				DatagramPacket data = new DatagramPacket(new byte[100],100);
				DatagramSocket socket= new DatagramSocket(port);
				while(true){		
					System.out.println("Waiting for result") ;
					socket.receive(data);
					String message = new String(data.getData(),0,data.getLength());
					String[] parts = message.split(" ");
					if (Integer.parseInt(parts[0])==magic){
						if (parts[1].equals("Ping")){
							System.out.println(parts[1]);
							sendMessage("127.0.0.1",50505,magic+" Req_Alive 127.0.0.1 "+port);	
						}else{
							System.out.println(parts[1]);
							break;
						}
					}
				}
			}catch(Exception e){
	         	System.out.println(e);
	      	}
      	}).start();
	}

	public static void main(String []args)throws Exception{

		Scanner sc = new Scanner(System.in);
		System.out.print("Enter Word: ");
		String word = sc.nextLine();
		Scanner sc1 = new Scanner(System.in);
		System.out.print("Enter port: ");
		port = sc1.nextInt();
		String hash=getID(word);
		sendRequest(hash);
		GetAnswer();
		System.out.print("Enter Cancel to cancel job");
		Scanner sc2 = new Scanner(System.in);
		word = sc2.nextLine();
		sendMessage("127.0.0.1",50505,magic+" "+word+" 127.0.0.1 "+port);
	}
}