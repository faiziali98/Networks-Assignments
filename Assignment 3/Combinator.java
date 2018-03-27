import java.io.*;
import java.net.*;
import java.util.*;
import java.security.*;

public class Combinator{
	static boolean cracking=false;
	static String tested="";
	Worker w;
	public Combinator(){
	}
	public Combinator(Worker _w){
		w=_w;
	}
	static String sha1(String input){
		StringBuffer sb = new StringBuffer();
		try {
	        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
	        byte[] result = mDigest.digest(input.getBytes());
	        
	        for (int i = 0; i < result.length; i++) {
	            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
	       	}
    	}catch(Exception e){
    		System.out.println(e);
    	}
    	return sb.toString();
    }
	public String Comninations(String aldone,int limit){
		String k="";
		long count=0;
		boolean first=true;
		for(char c1 = '0'; c1 <='z'; c1++){
			if (first){
				c1=aldone.charAt(0);
			}
			for(char c2 = '0'; c2 <='z'; c2++){
				if (first){
					c2=aldone.charAt(1);
				}
				for(char c3 = '0'; c3 <='z'; c3++){
					if (first){
						c3=aldone.charAt(2);
					}
					for(char c4 = '0'; c4 <='z'; c4++){
						if (first){
							c4=aldone.charAt(3);
						}
						for(char c5 = '0'; c5 <='z'; c5++){
							if (first){
								c5=aldone.charAt(4);
								first=false;
							}
							if(c1 == 58)
								c1 = 65;
							if(c2 == 58)
								c2 = 65;
							if(c3 == 58)
								c3 = 65;
							if(c4 == 58)
								c4 = 65;
							if(c5 == 58)
								c5 = 65;

							if(c1 == 91)
								c1 = 97;
							if(c2 == 91)
								c2 = 97;
							if(c3 == 91)
								c3 = 97;
							if(c4 == 91)
								c4 = 97;
							if(c5 == 91)
								c5 = 97;
							count++;
							k=c1+""+c2+""+c3+""+c4+""+c5;
							if (count==limit)
								break;
						}
						if (count==limit)
							break;
					}
					if (count==limit)
						break;
				}
				if (count==limit)
					break;
			}	
			if (count==limit)
				break;
		}
		return k;
	}

	public String Comninations(String aldone,String limit){
		String k="";
		boolean first=true;
		for(char c1 = '0'; c1 <='z'; c1++){
			if (first){
				c1=aldone.charAt(0);
			}
			for(char c2 = '0'; c2 <='z'; c2++){
				if (first){
					c2=aldone.charAt(1);
				}
				for(char c3 = '0'; c3 <='z'; c3++){
					if (first){
						c3=aldone.charAt(2);
					}
					for(char c4 = '0'; c4 <='z'; c4++){
						if (first){
							c4=aldone.charAt(3);
						}
						for(char c5 = '0'; c5 <='z'; c5++){
							if (first){
								c5=aldone.charAt(4);
								first=false;
							}
							if(c1 == 58)
								c1 = 65;
							if(c2 == 58)
								c2 = 65;
							if(c3 == 58)
								c3 = 65;
							if(c4 == 58)
								c4 = 65;
							if(c5 == 58)
								c5 = 65;

							if(c1 == 91)
								c1 = 97;
							if(c2 == 91)
								c2 = 97;
							if(c3 == 91)
								c3 = 97;
							if(c4 == 91)
								c4 = 97;
							if(c5 == 91)
								c5 = 97;
							k=c1+""+c2+""+c3+""+c4+""+c5;
							if (k.equals(limit))
								break;
						}
						if (k.equals(limit))
							break;
					}
					if (k.equals(limit))
						break;
				}
				if (k.equals(limit))
					break;
			}	
			if (k.equals(limit))
				break;
		}
		return k;
	}

	public boolean Cracker(String l1,String l2,String toFind){
		boolean first=true;
		for(char c1 = '0'; c1 <='z'; c1++){
			if (first){
				c1=l1.charAt(0);
			}
			for(char c2 = '0'; c2 <='z'; c2++){
				if (first){
					c2=l1.charAt(1);
				}
				for(char c3 = '0'; c3 <='z'; c3++){
					if (first){
						c3=l1.charAt(2);
					}
					for(char c4 = '0'; c4 <='z'; c4++){
						if (first){
							c4=l1.charAt(3);
						}
						for(char c5 = '0'; c5 <='z'; c5++){
							if (first){
								c5=l1.charAt(4);
								first=false;
							}
							if(c1 == 58)
								c1 = 65;
							if(c2 == 58)
								c2 = 65;
							if(c3 == 58)
								c3 = 65;
							if(c4 == 58)
								c4 = 65;
							if(c5 == 58)
								c5 = 65;

							if(c1 == 91)
								c1 = 97;
							if(c2 == 91)
								c2 = 97;
							if(c3 == 91)
								c3 = 97;
							if(c4 == 91)
								c4 = 97;
							if(c5 == 91)
								c5 = 97;

							tested=c1+""+c2+""+c3+""+c4+""+c5;
							w.tested=tested;
							if (sha1(tested).equals(toFind)||cracking==false||tested.equals(l2))
								break;
						}
						if (sha1(tested).equals(toFind)||cracking==false||tested.equals(l2))
							break;
					}
					if (sha1(tested).equals(toFind)||cracking==false||tested.equals(l2))
						break;
				}
				if (sha1(tested).equals(toFind)||cracking==false||tested.equals(l2))
					break;
			}	
			if (sha1(tested).equals(toFind)||cracking==false||tested.equals(l2))
				break;
		}
		if (cracking!=false)
			return sha1(tested).equals(toFind);
		else 
			return false;
	}
	public static void main(String[] args) {
		Combinator c = new Combinator();
		System.out.println(c.Comninations("00Q0t",916132832));
		System.out.println(c.Comninations("00000","00Q0t"));
		System.out.println(c.Cracker("00000","00Q0t",sha1("00323")));
	}
}