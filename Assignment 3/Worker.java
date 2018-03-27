import java.net.*;
import java.util.*;

public class Worker{

   	int magic = 15440;
   	String pwd;
   	String ip = "127.0.0.1";
   	int port;
   	int dong;
   	static Worker w;
  	String tested="";
   	public Worker (int _port){
   		port=_port;
   	}
   	public void sendRequest(){ 
		String message=magic+" Worker 127.0.0.1 "+port;
        try{
	         DatagramSocket socket = new DatagramSocket() ;
	         byte [] data = message.getBytes();
	         DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName("127.0.0.1"), 50505);
	         socket.send(packet);
	         Cracker();
      	}
      	catch(Exception e){
         	System.out.println(e);
      	}
	}
	public void sendMessage(String _ip,int _port,String message){ 
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
	public void pingAndAck(){

	}
	public void Cracker(){ // listen for messages from the server
		try{
				DatagramSocket socket = new DatagramSocket(port);

				while(true){
					System.out.println("Waiting for new request...");
					DatagramPacket packet = new DatagramPacket(new byte[100],100);
					socket.receive(packet);

					String message = new String(packet.getData(),0,packet.getLength());
					String[] parts = message.split(" ");

					System.out.println(message);

					if (Integer.parseInt(parts[0])==magic){
					    if(parts[1].equals("Job")){
					    	sendMessage(ip,50505,magic+" ACK_JOB "+ip+" "+port);
							pwd = parts[4];
							dong=Integer.parseInt(parts[5]);
							new Thread(()->{
								System.out.println("Started Cracking");
								Combinator c = new Combinator(w);
								c.cracking=true;
								boolean k=c.Cracker(parts[2],parts[3],pwd);
								if(k){ 
									sendMessage(ip,50505,magic+" Found "+ip+" "+port+" "+dong);
									tested="";
									System.out.println("Password Found");
									c.cracking=false;
								}else if(!k&&c.cracking==true){ 
									sendMessage(ip,50505,magic+" Not_Found "+ip+" "+port+" "+dong);
									System.out.println("Password not Found");
									c.cracking=false;
								}
							}).start();

						}else if(parts[1].equals("Stop")){
							Combinator.cracking = false;
							System.out.println("Job cancelled!\n\n");
							tested="";
						}else if(parts[1].equals("Ping")){
							sendMessage(ip,50505,magic+" I_AM_HERE "+ip+" "+port+" "+tested);
						}
					}
				}  
		 	}catch(Exception e){
				System.out.println(e);
		 	}
	}
	public static void main(String []args)throws Exception{
		Scanner sc1 = new Scanner(System.in);
		System.out.print("Enter port: ");
		w=new Worker(sc1.nextInt());
		w.sendRequest();
	}
}