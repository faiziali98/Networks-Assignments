import java.io.*;
import java.net.*;
import java.util.*;
import java.security.*;

class Client implements Runnable{

	private Socket socket;
	private String ip;
	private int port;
	DataInputStream in;
	DataOutputStream out;
	Scanner sc = new Scanner(System.in);
	public Client(int _port){
		port = _port;
	}
	public Client(String host_ip,int _port) throws Exception{
		ip = host_ip;
		port = _port;
		socket = new Socket(InetAddress.getByName(ip),port);	
	}
	public void SendMessage(String message) throws Exception{
		DataOutputStream out1 = new DataOutputStream(socket.getOutputStream());
		out1.writeUTF(message);
		out1.flush();
	}
	public String getMessage() throws Exception{
		DataInputStream in1 = new DataInputStream(socket.getInputStream());
		String message = in1.readUTF();
		return message;
	}
	public void SendMessageU(String message,String _ip,int _port) throws Exception{
		Socket socket1 = new Socket(InetAddress.getByName(_ip),_port);
		DataOutputStream out1 = new DataOutputStream(socket1.getOutputStream());
		out1.writeUTF(message);
		out1.flush();
	}
	public void AddFile(String value) throws Exception{
		String key=sha1(value);
		if (!(DHT.pred.equals("e") && DHT.succ.equals("e"))&&!(DHT.pred.equals(DHT.ID)&&DHT.succ.equals(DHT.ID))) {
			if (key.compareTo(DHT.pred)>0&&key.compareTo(DHT.ID)<0) {
				int g=1;
				for (int n=0;n<DHT.list.size();n++){
					if (DHT.list.get(n).equals(value)){
						g=0;
						break;
					}
				}
				if (g==1){
					System.out.println("\nFile Added\n");
					DHT.list.add(value);
				}else
					System.out.println("\nAlready added\n");
			}else if (key.compareTo(DHT.ID)>0 && DHT.ID.compareTo(DHT.succ)>0){
				SendMessageU("JustAdd "+value+" "+key+" "+DHT.ip+" "+DHT.port,DHT.sIp,DHT.sPort);
			}else if (key.compareTo(DHT.ID)<0 && DHT.ID.compareTo(DHT.succ)<0){
				SendMessageU("JustAdd "+value+" "+key+" "+DHT.ip+" "+DHT.port,DHT.pIp,DHT.pPort);
			}else if (key.compareTo(DHT.ID)>0){
				SendMessageU("Add "+value+" "+key+" "+DHT.ip+" "+DHT.port,DHT.sIp,DHT.sPort);
			}else if (key.compareTo(DHT.pred)<0){
				SendMessageU("Add "+value+" "+key+" "+DHT.ip+" "+DHT.port,DHT.pIp,DHT.pPort);
			}
		}else{
				int g=1;
				for (int n=0;n<DHT.list.size();n++){
					if (DHT.list.get(n).equals(value)){
						g=0;
						break;
					}
				}
				if (g==1){
					System.out.println("\nFile Added\n");
					DHT.list.add(value);
				}else
					System.out.println("\nAlready added\n");
		}
	}
	public void FindFile(String value) throws Exception{
		String key=sha1(value);
		if (!(DHT.pred.equals("e") && DHT.succ.equals("e"))&&!(DHT.pred.equals(DHT.ID)&&DHT.succ.equals(DHT.ID))){
			if (key.compareTo(DHT.pred)>0&&key.compareTo(DHT.ID)<0) {
				for (int n=0;n<DHT.list.size();n++){
					if (DHT.list.get(n).equals(value)){
						System.out.println("\nFile found\n");
						break;
					}
				}
			}else if (key.compareTo(DHT.ID)>0 && DHT.ID.compareTo(DHT.succ)>0){
				SendMessageU("JustFind "+value+" "+key+" "+DHT.ip+" "+DHT.port,DHT.sIp,DHT.sPort);
			}else if (key.compareTo(DHT.ID)<0 && DHT.ID.compareTo(DHT.succ)<0){
				SendMessageU("JustFind "+value+" "+key+" "+DHT.ip+" "+DHT.port,DHT.pIp,DHT.pPort);
			}else if (key.compareTo(DHT.ID)>0){
				SendMessageU("Find "+value+" "+key+" "+DHT.ip+" "+DHT.port,DHT.sIp,DHT.sPort);
			}else if (key.compareTo(DHT.pred)<0){
				SendMessageU("Find "+value+" "+key+" "+DHT.ip+" "+DHT.port,DHT.pIp,DHT.pPort);
			}
		}else{
			for (int n=0;n<DHT.list.size();n++){
				if (DHT.list.get(n).equals(value)){
					System.out.println("\nFile found\n");
					break;
				}
			}
		}
	}
	static String sha1(String input) throws Exception {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
       	}
         
        return sb.toString();
    }

	public void run(){
		try{
			System.out.println("\nSuccessfully joined the Network!\n");
			if (DHT.port!=port){
				while (true){
					in = new DataInputStream(socket.getInputStream());
					out = new DataOutputStream(socket.getOutputStream());		
					String serId=sha1(ip+port);	
					out.writeUTF(DHT.ID+" "+DHT.ip+" "+DHT.port+" "+port);
					out.flush();
					String message = in.readUTF();
					String[] parts = message.split(" ");
					if (parts[0].equals("NULL")){
						DHT.succ=parts[1];
						DHT.sIp=parts[2];
						DHT.sPort=Integer.parseInt(parts[3]);
						DHT.pred=parts[1];
						DHT.pIp=parts[2];
						DHT.pPort=Integer.parseInt(parts[3]);
						for (int n=4;n<parts.length;n++){
							if (!(parts[n].equals("null"))) {
								AddFile(parts[n]);
							}
						}
						break;
					}else if (parts[0].equals("Pred")){
						DHT.pred=parts[1];
						DHT.pIp=parts[2];
						DHT.pPort=Integer.parseInt(parts[3]);
						DHT.succ=parts[4];
						DHT.sIp=parts[5];
						DHT.sPort=Integer.parseInt(parts[6]);
						for (int n=7;n<parts.length;n++){
							if (!(parts[n].equals("Empty"))){
								AddFile(parts[n]);
							}
						}
						break;
					}else if (parts[0].equals("Succ")){
						DHT.succ=parts[1];
						DHT.sIp=parts[2];
						DHT.sPort=Integer.parseInt(parts[3]);
						DHT.pred=parts[4];
						DHT.pIp=parts[5];
						DHT.pPort=Integer.parseInt(parts[6]);
						for (int n=7;n<parts.length;n++){
							if (!(parts[n].equals("null"))) {
								AddFile(parts[n]);
							}
						}
						break;
					}else {
						socket = new Socket(InetAddress.getByName(parts[1]),Integer.parseInt(parts[2]));
					}
				}
				System.out.println("Succesor: "+DHT.succ+" "+DHT.sPort+" "+DHT.sIp);
				System.out.println("Predeccesor: "+DHT.pred+" "+DHT.pPort+" "+DHT.pIp+"\n");
			}
				System.out.println("You are connected now what do you want to do: ");
				while(true){
					System.out.println("1. Leave");
					System.out.println("2. Get");
					System.out.println("3. Put\n");
					int p = sc.nextInt();
					if (p==1){
						if (DHT.pred.equals(DHT.succ)&&!(DHT.pred.equals(DHT.ID)&&DHT.succ.equals(DHT.ID))){
							SendMessageU("Leaving",DHT.pIp,DHT.pPort);
							SendMessageU("Alonel "+DHT.succ+" "+DHT.sIp+" "+DHT.sPort,DHT.pIp,DHT.pPort);
							for (int n=0;n<DHT.list.size();n++){
								String key=sha1(DHT.list.get(n));
								SendMessageU("Add "+DHT.list.get(n)+" "+key+" "+DHT.ip+" "+DHT.port,DHT.sIp,DHT.sPort);
							}
						}else if (!(DHT.pred.equals("e") && DHT.succ.equals("e"))&&!(DHT.pred.equals(DHT.ID)&&DHT.succ.equals(DHT.ID))) {
							SendMessageU("Leaving",DHT.sIp,DHT.sPort);
							SendMessageU("Pred "+DHT.pred+" "+DHT.pIp+" "+DHT.pPort,DHT.sIp,DHT.sPort);
							SendMessageU("Leaving",DHT.pIp,DHT.pPort);
							SendMessageU("Succ "+DHT.succ+" "+DHT.sIp+" "+DHT.sPort,DHT.pIp,DHT.pPort);
							for (int n=0;n<DHT.list.size();n++){
								String key=sha1(DHT.list.get(n));
								SendMessageU("Add "+DHT.list.get(n)+" "+key+" "+DHT.ip+" "+DHT.port,DHT.sIp,DHT.sPort);
							}
						}
						DHT.ss.close();
						break;
					}else if (p==3){
						System.out.println("Enter the file name to add:");
						sc.nextLine();
						String value=sc.nextLine();
						AddFile(value);
					}else if (p==2){
						System.out.println("Enter the file name to Find:");
						sc.nextLine();
						String value=sc.nextLine();
						FindFile(value);
					}
				}
				System.out.println("Dissconnected from server");
		} catch(Exception e){
			e.printStackTrace();
		} 
	}
}