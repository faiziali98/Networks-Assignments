import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

	int port=50505;
	int magic = 15440;
	String ip="127.0.0.1";
	boolean started=true;
	DatagramSocket socket;
	ArrayList<WorkerData> workers = new ArrayList<WorkerData>();
	ArrayList<ReqData> clients = new ArrayList<ReqData>();
	boolean running=true;
	int toDo=0;

	static FileOutputStream out;
	static ObjectOutputStream s ;

	public Server(){
	}

	public void sendMessage(String message,WorkerData wd){ 
        try{
	         DatagramSocket socket = new DatagramSocket() ;
	         byte [] data = message.getBytes();
	         DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(wd.ip), wd.port);
	         socket.send(packet);
      	}
      	catch(Exception e){
         	System.out.println(e);
     
      	}
	}

	public void sendMessage(String message,ReqData rq){ 
        try{
	         DatagramSocket socket = new DatagramSocket() ;
	         byte [] data = message.getBytes();
	         DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(rq.ip), rq.port);
	         socket.send(packet);
      	}
      	catch(Exception e){
         	System.out.println(e);
      	}
	}

	public void letPing(){
		new Thread(()->{
			while (true){
				for(int i=0 ;i<workers.size();i++){
					WorkerData wd=workers.get(i);
					System.out.println("Pinging");
					sendMessage(magic+" Ping",wd);
					wd.clock=wd.clock+1;
				}
				try {
			    	Thread.sleep(3000);
				} catch(InterruptedException ex) {
			    	Thread.currentThread().interrupt();
				}
				for(int i=0 ;i<workers.size();i++){
					WorkerData wd=workers.get(i);
					if(wd.clock==3){
						System.out.println("Client removed");
						ContJob temp=new ContJob(wd.sjob,wd.ejob);
						if (!wd.free){
							wd.doing.noprev=false;
							wd.doing.toCont.add(temp);
							System.out.println(wd.sjob);
							System.out.println("Prev job added");
						}
						workers.remove(wd);
					}
				}
			}
		}).start();
	}
	public void letPingReq(){
		new Thread(()->{
			while (true){
				for(int i=0 ;i<clients.size();i++){
					ReqData rq=clients.get(i);
					System.out.println("Pinging Req");
					sendMessage(magic+" Ping",rq);
					rq.clock=rq.clock+1;
				}
				try {
			    	Thread.sleep(3000);
				} catch(InterruptedException ex) {
			    	Thread.currentThread().interrupt();
				}
				if(clients.size()!=0){
					for(int i=0 ;i<clients.size();i++){
						ReqData rq=clients.get(i);
						if(rq.clock==3){
							System.out.println("Request removed");
							rq.toCont.clear();
							rq.checkpoint="00000";
							clients.remove(rq);
							for(int j=0 ;j<workers.size();j++){
								WorkerData wd=workers.get(j);
								if (wd.doing.equals(rq)){
									sendMessage(magic+" Stop",wd);
									wd.free=true;
								}
							}
						}
					}
				}
			}
		}).start();
	}
	public void serailiztion(){
		new Thread(()->{
			while (true){
				try {
					out= new FileOutputStream("Serialized.ser");
					s = new ObjectOutputStream(out);
					//s.writeObject(toCont);
					s.writeObject(workers);
					s.writeObject(clients);
					//s.writeObject(checkpoint);
					s.flush();
				} catch(IOException ex){
					ex.printStackTrace();
				}
				try {
					Thread.sleep(1000);
				} catch(InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
			}
		}).start();
	}
	public void dhaka(){
		new Thread(()->{
				try {
					if (clients.size()!=0){
						for(int i=0 ;i<workers.size();i++){
							WorkerData wd=workers.get(i);
							System.out.println("Sending Job");
							assignJobs(wd,clients.get(toDo),true);
						}
						toDo=(toDo+1)%clients.size();
					}
				} catch(Exception ex){
					ex.printStackTrace();
				}
		}).start();
	}
	/*public void assJobs(WorkerData wd,ReqData rq) throws Exception{
		new Thread(()->{
			while (!wd.done){
				assignJobs(wd,clients.peek());
				wd.clockAck=wd.clockAck+1;
				try {
			    	Thread.sleep(1000);
				} catch(InterruptedException ex) {
			    	Thread.currentThread().interrupt();
				}
				if (wd.clockAck==3){
					workers.remove(wd);
					ContJob temp=new ContJob(wd.sjob,wd.ejob);
					toCont.add(temp);
				}
			}
			//Thread.join();
		}).start();
	}*/
	public void listner(){
		try {
			socket = new DatagramSocket(port);
			System.out.println("Server Started");

			while(true){
				DatagramPacket data = new DatagramPacket(new byte[100],100);
				socket.receive(data);
				String message = new String(data.getData(),0,data.getLength());
				String[] splited = message.split(" ");
				if (Integer.parseInt(splited[0])==magic){
					if (splited[1].equals("Worker")){
						System.out.println("Worker Added");
						WorkerData temp=new WorkerData(splited[2],Integer.parseInt(splited[3]));
						workers.add(temp);
					}else if (splited[1].equals("Client")){
						System.out.println("Request Added");
						ReqData temp=new ReqData(splited[2],Integer.parseInt(splited[3]),splited[4]);
						clients.add(temp);
					}else if (splited[1].equals("Not_Found")){
						int ind=Integer.parseInt(splited[4]);
						if (clients.size()!=0){
							ReqData rq=clients.get(ind);
							if (!(rq.checkpoint.equals("zzzzz"))){
								WorkerData temp=new WorkerData(splited[2],Integer.parseInt(splited[3]));
								for(int i=0 ;i<workers.size();i++){
									WorkerData wd=workers.get(i);
									if (temp.equals(wd)){
										rq.done=wd.ejob;
										wd.done=false;
										System.out.println("Sending Job");
										toDo=(wd.toDo+1)%clients.size();
										assignJobs(wd,clients.get(toDo),false);
										break;
									}
								}
							}else{
								System.out.println("Word not Found");
								rq.checkpoint="00000";
								sendMessage(magic+" Not_Found",rq);
								clients.remove(rq);
								WorkerData temp=new WorkerData(splited[2],Integer.parseInt(splited[3]));
						        for(int i=0 ;i<workers.size();i++){
						        	WorkerData wd=workers.get(i);
						        	if (!(temp.equals(wd))){
										if (wd.free==false && wd.doing.equals(rq)){
											sendMessage(magic+" Stop",wd);
											wd.free=true;
										}
									}else
										wd.free=true;
								}
							}
						}
					}else if (splited[1].equals("Found")){
						System.out.println("Word Found");	
						int ind=Integer.parseInt(splited[4]);
						ReqData rq=clients.get(ind);
						sendMessage(magic+" Found",rq);
						clients.remove(rq);
						WorkerData temp=new WorkerData(splited[2],Integer.parseInt(splited[3]));
				        for(int i=0 ;i<workers.size();i++){
				        	WorkerData wd=workers.get(i);
				        	if (!(temp.equals(wd))){
								if (wd.free==false && wd.doing.equals(rq)){
									sendMessage(magic+" Stop",wd);
									wd.free=true;
								}
							}else
								wd.free=true;
						}
					}else if (splited[1].equals("I_AM_HERE")){
						WorkerData temp=new WorkerData(splited[2],Integer.parseInt(splited[3]));
						System.out.println(message);
						for(int i=0 ;i<workers.size();i++){
							WorkerData wd=workers.get(i);
				        	if (temp.equals(wd)){
								wd.clock=0;
							}
						}
					}else if (splited[1].equals("Req_Alive")){
						ReqData temp=new ReqData(splited[2],Integer.parseInt(splited[3]));
						ReqData rq;
						System.out.println(message);
						for(int i=0 ;i<clients.size();i++){
							rq=clients.get(i);
							if (temp.equals(rq)){
								rq.clock=0;
								break;
							}
						}
					}else if (splited[1].equals("ACK_JOB")){
						WorkerData temp=new WorkerData(splited[2],Integer.parseInt(splited[3]));
						System.out.println(message);
						for(int i=0 ;i<workers.size();i++){
							WorkerData wd=workers.get(i);
				        	if (temp.equals(wd)){
								wd.clockAck=0;
								wd.done=true;
							}
						}
					}else if (splited[1].equals("Cancel")){
						ReqData temp=new ReqData(splited[2],Integer.parseInt(splited[3]));
						clients.remove(temp);
						System.out.println("Request removed");
						for(int i=0 ;i<workers.size();i++){
							WorkerData wd=workers.get(i);
							if (wd.doing.equals(temp)){
								sendMessage(magic+" Stop",wd);
								wd.free=true;
							}
						}
					}
					if (clients.size()!=0){
						for(int i=0 ;i<workers.size();i++){
							WorkerData wd=workers.get(i);
							if (wd.free){
								System.out.println("Sending Job");
								assignJobs(wd,clients.get(toDo),false);
							}
						}
						toDo=(toDo+1)%clients.size();
					}
					if (started){
						letPing();
						letPingReq();
						started=false;
					}
				}
			}
		}catch(Exception e){
				e.printStackTrace();
		}
	}	

	private synchronized void assignJobs(WorkerData wd,ReqData rq,boolean flag){
		if (!flag){
			if (rq!=null){
				if (rq.noprev){
					String temp=rq.checkpoint;
					Combinator c=new Combinator();
					rq.checkpoint=c.Comninations(rq.checkpoint,500000);
					String message=magic+" Job "+temp+" "+rq.checkpoint+" "+rq.request+" "+toDo;
					wd.setJob(temp,rq.checkpoint);
					wd.free=false;
					wd.doing=rq;
					wd.toDo=toDo;
					sendMessage(message,wd);
				}else{
					System.out.println("Previous Job Sent");
					ContJob j=rq.toCont.remove();
					System.out.println(j.jobs);
					String message=magic+" Job "+j.jobs+" "+j.jobe+" "+rq.request+" "+toDo;
					wd.setJob(j.jobs,j.jobe);
					wd.free=false;
					wd.doing=rq;
					wd.toDo=toDo;
					if (rq.toCont.size()==0)
						rq.noprev=true;
					sendMessage(message,wd);
				}
			}
		}else{
			if (rq!=null){
				if (rq.noprev){
					String temp=rq.done;
					Combinator c=new Combinator();
					rq.checkpoint=c.Comninations(rq.done,500000);
					String message=magic+" Job "+temp+" "+rq.checkpoint+" "+rq.request+" "+toDo;
					wd.setJob(temp,rq.checkpoint);
					wd.free=false;
					wd.doing=rq;
					wd.toDo=toDo;
					sendMessage(message,wd);
				}else{
					System.out.println("Previous Job Sent");
					ContJob j=rq.toCont.remove();
					System.out.println(j.jobs);
					String message=magic+" Job "+j.jobs+" "+j.jobe+" "+rq.request+" "+toDo;
					wd.setJob(j.jobs,j.jobe);
					wd.free=false;
					wd.doing=rq;
					wd.toDo=toDo;
					if (rq.toCont.size()==0)
						rq.noprev=true;
					sendMessage(message,wd);
				}
			}
		}
	} 
	public static void main(String[] args) {
		Server s=new Server();
		System.out.print("Enter What to do: \nStart new or Start Previous one? : ");
		Scanner sc2 = new Scanner(System.in);
		int i = sc2.nextInt();
		try {	
			if (i==1){
				s.started=false;
				s.serailiztion();
				s.letPing();
				s.letPingReq();
				s.listner();
			}else{
				try {
					FileInputStream in = new FileInputStream("Serialized.ser");
					ObjectInputStream is = new ObjectInputStream(in);

					ArrayList tempList= (ArrayList)is.readObject(); 
					for (int im = 0; im < tempList.size(); im++) 
					   s.workers.add(im, (WorkerData)tempList.get(im));
					ArrayList tempList2= (ArrayList)is.readObject(); 
					for (int im = 0; im < tempList2.size(); im++)
					   s.clients.add(im, (ReqData)tempList2.get(im));

					in.close();
					is.close();

					s.serailiztion();
					s.dhaka();
					s.listner();
				}catch(ClassNotFoundException ex){
					Thread.currentThread().interrupt();
				}
			}
		} catch(IOException ex) {
			Thread.currentThread().interrupt();
		}
	}
}
