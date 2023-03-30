import java.net.*;
import java.io.*;

public class Client {
	public static void main(String args[]) throws Exception {
		Socket s = new Socket("localhost", 50000);
		BufferedReader bin = new BufferedReader(new InputStreamReader(s.getInputStream()));
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		DataOutputStream dout = new DataOutputStream(s.getOutputStream());
		String msg = "";
		String respond;

	while (!msg.equals("NONE")) {	
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

		msg = "REDY";
        dout.write((msg + "\n").getBytes());
        dout.flush();
        respond = bin.readLine();
        System.out.println(respond);

		msg = br.readLine();
        dout.write((msg + "\n").getBytes());
        dout.flush();
		
        
	}
	
	dout.close();
	bin.close();
	s.close();
	}
}