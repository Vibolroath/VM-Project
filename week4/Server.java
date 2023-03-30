// import java.io.*;  
// import java.net.*;  

// public class Server {  
//     public static void main(String[] args){  
//         try{  
//             ServerSocket ss=new ServerSocket(5000);  
//             Socket s=ss.accept();//establishes connection   
//             DataInputStream dis=new DataInputStream(s.getInputStream());  
//             String  str=(String)dis.readUTF();  
//             System.out.println("G'Day"+str);  
//             ss.close();  
//         }
//         catch(Exception e){
//             System.out.println(e);
//             }  
//     }  
// }  

// import java.net.*;
// import java.io.*;

// public class Server {
//     public static void main(String args[]) throws Exception {
//         ServerSocket ss = new ServerSocket(5657);
//         Socket s = ss.accept();
//         DataInputStream din = new DataInputStream(s.getInputStream());
//         DataOutputStream dout = new DataOutputStream(s.getOutputStream());
//         BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

//         String str = "", str2 = "";
//         while (!str.equals("BYE")) {
//             str = din.readUTF();
//             System.out.println("client says: " + str);
//             str2 = br.readLine();
//             dout.writeUTF(str2);
//             dout.flush();
//         }
//         din.close();
//         s.close();
//         ss.close();
//     }
// }

import java.net.*;
import java.io.*;

public class Server {
	public static void main(String args[]) throws Exception {
		ServerSocket ss = new ServerSocket(5659);
        Socket s = ss.accept();
		BufferedReader bin = new BufferedReader(new InputStreamReader(s.getInputStream()));
		DataOutputStream dout = new DataOutputStream(s.getOutputStream());
		String msg = "";
		String respond;

        while (!msg.equals("QUIT")) {	
            msg = "HELO";
            dout.write((msg + "\n").getBytes());
            dout.flush();
            respond = bin.readLine();
            System.out.println(respond);
    
            String username = "Jay";
            msg = "AUTH " + username;
            dout.write((msg + "\n").getBytes());
            dout.flush();
            respond = bin.readLine();
            System.out.println(respond);
    
            msg = "QUIT";
            dout.write((msg + "\n").getBytes());
            dout.flush();
            respond = bin.readLine();
            System.out.println(respond);
        }
        
        dout.close();
        bin.close();
		s.close();
        ss.close();
	}
}