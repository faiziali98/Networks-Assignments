import java.util.*;
import java.io.Serializable;
public class WorkerData implements Serializable{
	String ip;
   	int port;
   	String sjob=null;
   	String ejob=null;
   	int clock;
   	int clockAck;
   	String lastUpdated=null;
   	boolean free=true;
   	boolean done=false;
   	ReqData doing;
   	int toDo;
	public WorkerData(String _ip,int _port){
		ip=_ip;
		port=_port;
	}
	public void setJob(String _sjob,String _ejob){
		sjob=_sjob;
		ejob=_ejob;
	}
	public boolean equals(WorkerData wd){
		return (ip.equals(wd.ip)&&port==wd.port);
	}
}