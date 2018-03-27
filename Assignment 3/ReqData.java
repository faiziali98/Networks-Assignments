import java.util.*;
import java.io.Serializable;
public class ReqData implements Serializable{
	String ip;
   	int port;
   	String request;
   	int clock;
   	String checkpoint="00000";
   	String done="00000";
   	Queue<ContJob> toCont;
   	boolean noprev=true;
   	public ReqData(String _ip,int _port){
		ip=_ip;
		checkpoint="00000";
		port=_port;
		toCont=new LinkedList<ContJob>();
	}
	public ReqData(String _ip,int _port, String _request){
		ip=_ip;
		port=_port;
		checkpoint="00000";
		request=_request;
		toCont=new LinkedList<ContJob>();
	}
	public boolean equals(ReqData wd){
		return (ip.equals(wd.ip)&&port==wd.port);
	}
}