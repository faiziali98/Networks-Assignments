import java.net.*;
import java.util.*;
import java.io.*;
import java.security.*;

class Server implements Runnable{

	private static int port;
	private static Socket client;
	private static String ID;
	private static String Ip;

	public Server(int port, String _ID,String _Ip) throws Exception{
		this.port = port;
		ID=_ID;
		Ip=_Ip;
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
			DHT.ss = new ServerSocket(port);
			while(true){
				client = DHT.ss.accept();
				DataInputStream in = new DataInputStream(client.getInputStream());
				DataOutputStream out = new DataOutputStream(client.getOutputStream());
				String message = in.readUTF();
				String[] parts = message.split(" ");
				int g=1;
				if (message.equals("List")){
					String toSend="e";
					for (int n=0;n<DHT.list.size();n++){
						if (n==0){
							toSend=null;
							toSend=toSend+" ";}
						toSend=toSend+DHT.list.get(n)+" ";
					}
					if (!(toSend.equals("e"))) {
						System.out.println(toSend);
						out.writeUTF(toSend);
						out.flush();
					}else{
						out.writeUTF("Empty");
						out.flush();
					}
				}else if (message.equals("Leaving")){
					System.out.println("Client Left");
					g=0;
				}else if (parts[0].equals("Pred")){
					DHT.pred=parts[1];
					DHT.pIp=parts[2];
					DHT.pPort=Integer.parseInt(parts[3]);
					System.out.println("Succesor: "+DHT.succ+" "+DHT.sPort+" "+DHT.sIp);
					System.out.println("Predeccesor: "+DHT.pred+" "+DHT.pPort+" "+DHT.pIp+"\n");
					g=0;
				}else if (parts[0].equals("Succ")){
					DHT.succ=parts[1];
					DHT.sIp=parts[2];
					DHT.sPort=Integer.parseInt(parts[3]);
					System.out.println("Succesor: "+DHT.succ+" "+DHT.sPort+" "+DHT.sIp);
					System.out.println("Predeccesor: "+DHT.pred+" "+DHT.pPort+" "+DHT.pIp+"\n");
					g=0;
				}else if (parts[0].equals("JustFind")){
					String key=parts[2];
					String value=parts[1];
					for (int n=0;n<DHT.list.size();n++){
						if (DHT.list.get(n).equals(value)){
							System.out.println("\nFile found\n");
							Client c=new Client(parts[3],Integer.parseInt(parts[4]));
							c.SendMessage("Found "+DHT.ip+" "+DHT.port);
							break;
						}
					}
					g=0;
				}else if (parts[0].equals("Alonel")) {
					DHT.succ=parts[1];
					DHT.sIp=parts[2];
					DHT.sPort=Integer.parseInt(parts[3]);
					DHT.pred=parts[1];
					DHT.pIp=parts[2];
					DHT.pPort=Integer.parseInt(parts[3]);
					System.out.println("Succesor: "+DHT.succ+" "+DHT.sPort+" "+DHT.sIp);
					System.out.println("Predeccesor: "+DHT.pred+" "+DHT.pPort+" "+DHT.pIp+"\n");
					g=0;
				}else if (parts[0].equals("JustAdd")){
					String key=parts[2];
					int k=1;
					for (int n=0;n<DHT.list.size();n++){
						if (DHT.list.get(n).equals(parts[1])){
							k=0;
							break;
						}
					}
					if (k==1){
						System.out.println("\nFile Added\n");
						DHT.list.add(parts[1]);
						Client c=new Client(parts[3],Integer.parseInt(parts[4]));
						c.SendMessage("Added "+DHT.ip+" "+DHT.port);
					}else
						System.out.println("\nAlready added\n");		
					g=0;
				}else if (parts[0].equals("Add")){
					String key=parts[2];
					if (!(DHT.pred.equals("e") && DHT.succ.equals("e"))&&!(DHT.pred.equals(DHT.ID)&&DHT.succ.equals(DHT.ID))) {
						if (key.compareTo(DHT.pred)>0&&key.compareTo(DHT.ID)<0){
							int k=1;
							for (int n=0;n<DHT.list.size();n++){
								if (DHT.list.get(n).equals(parts[1])){
									k=0;
									break;
								}
							}
							if (k==1){
								System.out.println("\nFile Added\n");
								Client c=new Client(parts[3],Integer.parseInt(parts[4]));
								c.SendMessage("Added "+DHT.ip+" "+DHT.port);
								DHT.list.add(parts[1]);
							}else
								System.out.println("\nAlready added\n");
						}else if (key.compareTo(DHT.ID)>0 && DHT.ID.compareTo(DHT.succ)>0){
							Client c=new Client(DHT.sIp,DHT.sPort);
							c.SendMessage("JustAdd "+parts[1]+" "+parts[2]+" "+parts[3]+" "+parts[4]);
						}else if (key.compareTo(DHT.ID)<0 && DHT.ID.compareTo(DHT.succ)<0){
							Client c=new Client(DHT.sIp,DHT.sPort);
							c.SendMessage("JustAdd "+parts[1]+" "+parts[2]+" "+parts[3]+" "+parts[4]);
						}else if (key.compareTo(DHT.ID)>0){
							Client c=new Client(DHT.sIp,DHT.sPort);
							c.SendMessage(message);
						}else if (key.compareTo(DHT.pred)<0){
							Client c=new Client(DHT.pIp,DHT.pPort);
							c.SendMessage(message);
						}
					}else{
						int k=1;
						for (int n=0;n<DHT.list.size();n++){
							if (DHT.list.get(n).equals(parts[1])){
								k=0;
								break;
							}
						}
						if (k==1){
							System.out.println("\nFile Added\n");
							DHT.list.add(parts[1]);
						}else
							System.out.println("\nAlready added\n");
					}
					g=0;
				}else if (parts[0].equals("Added")){
					System.out.println("File added on: "+parts[1]+" "+parts[2]);
					g=0;
				}else if (parts[0].equals("Find")){
					String key=parts[2];
					String value=parts[1];
					if (!(DHT.pred.equals("e") && DHT.succ.equals("e"))&&!(DHT.pred.equals(DHT.ID)&&DHT.succ.equals(DHT.ID))) {
						if (key.compareTo(DHT.pred)>0&&key.compareTo(DHT.ID)<0){
							for (int n=0;n<DHT.list.size();n++){
								if (DHT.list.get(n).equals(value)){
									System.out.println("\nFile found\n");
									Client c=new Client(parts[3],Integer.parseInt(parts[4]));
									c.SendMessage("Found "+DHT.ip+" "+DHT.port);
									break;
								}
							}
						}else if (key.compareTo(DHT.ID)>0 && DHT.ID.compareTo(DHT.succ)>0){
							Client c=new Client(DHT.sIp,DHT.sPort);
							c.SendMessage("JustFind "+parts[1]+" "+parts[2]+" "+parts[3]+" "+parts[4]);
						}else if (key.compareTo(DHT.ID)<0 && DHT.ID.compareTo(DHT.succ)<0){
							Client c=new Client(DHT.sIp,DHT.sPort);
							c.SendMessage("JustFind "+parts[1]+" "+parts[2]+" "+parts[3]+" "+parts[4]);
						}else if (key.compareTo(DHT.ID)>0){
							Client c=new Client(DHT.sIp,DHT.sPort);
							c.SendMessage(message);
						}else if (key.compareTo(DHT.pred)<0){
							Client c=new Client(DHT.pIp,DHT.pPort);
							c.SendMessage(message);
						}
					}else{
						for (int n=0;n<DHT.list.size();n++){
							if (DHT.list.get(n).equals(parts[1])){
								System.out.println("\nFile found\n");
								break;
							}
						}
					}
					g=0;
				}else if (parts[0].equals("Found")){
					System.out.println("File found on: "+parts[1]+" "+parts[2]);
					g=0;
				}else if (DHT.pred.equals("e") && DHT.succ.equals("e")) {
					DHT.succ=parts[0];
					DHT.sIp=parts[1];
					DHT.sPort=Integer.parseInt(parts[2]);
					DHT.pred=parts[0];
					DHT.pIp=parts[1];
					DHT.pPort=Integer.parseInt(parts[2]);
					String toSend=null;
					for (int n=0;n<DHT.list.size();n++){
						if (n==0)
							toSend=toSend+" ";
						toSend=toSend+DHT.list.get(n)+" ";
					}
					out.writeUTF("NULL"+" "+DHT.ID+" "+DHT.ip+" "+DHT.port+" "+toSend);
					out.flush();
				}else if (parts[0].compareTo(DHT.ID)>0&&(parts[0].compareTo(DHT.succ)<0||DHT.ID.compareTo(DHT.succ)>0)) {
					Client c=new Client(DHT.sIp,DHT.sPort);
					c.SendMessage("Pred"+" "+message);
					c=new Client(DHT.sIp,DHT.sPort);
					c.SendMessage("List");
					String list=c.getMessage();
					out.writeUTF("Pred"+" "+DHT.ID+" "+DHT.ip+" "+DHT.port+" "+DHT.succ+" "+DHT.sIp+" "+DHT.sPort+" "+list);
					out.flush();
					DHT.succ=parts[0];
					DHT.sIp=parts[1];
					DHT.sPort=Integer.parseInt(parts[2]);
				}else if (parts[0].compareTo(DHT.ID)<0&&(parts[0].compareTo(DHT.pred)>0||DHT.ID.compareTo(DHT.pred)<0)) {
					Client c=new Client(DHT.pIp,DHT.pPort);
					c.SendMessage("Succ"+" "+message);
					String toSend=null;
					for (int n=0;n<DHT.list.size();n++){
						if (n==0)
							toSend=toSend+" ";
						toSend=toSend+DHT.list.get(n)+" ";
					}
					out.writeUTF("Succ"+" "+DHT.ID+" "+DHT.ip+" "+DHT.port+" "+DHT.pred+" "+DHT.pIp+" "+DHT.pPort+" "+toSend);
					out.flush();
					DHT.pred=parts[0];
					DHT.pIp=parts[1];
					DHT.pPort=Integer.parseInt(parts[2]);
				}else {
					out.writeUTF("CantTell"+" "+DHT.sIp+" "+DHT.sPort);
					out.flush();
					g=0;
				}
				if (g==1){
					System.out.println("client connected");
					System.out.println("Succesor: "+DHT.succ+" "+DHT.sPort+" "+DHT.sIp);
					System.out.println("Predeccesor: "+DHT.pred+" "+DHT.pPort+" "+DHT.pIp+"\n");
				}
			}
		} catch(Exception e){
			System.out.println("Connection closed");
		} finally{
			try{
				DHT.ss.close();
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}