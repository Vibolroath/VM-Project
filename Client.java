import java.io.*;
import java.net.Socket;

public class Client {
  public static void main(String[] args) throws IOException {
    // connect to the server
    Socket s = new Socket("localhost", 50000);
    BufferedReader bin = new BufferedReader(new InputStreamReader(s.getInputStream()));
    DataOutputStream dout = new DataOutputStream(s.getOutputStream());
    String userName = System.getProperty("user.name");
    String response;

    // send handshake message to the server
    dout.write(("HELO\n").getBytes());
    dout.flush();
    response = bin.readLine();

    dout.write(("AUTH " + userName + "\n").getBytes());
    dout.flush();
    response = bin.readLine();

    dout.write(("REDY\n").getBytes());
    dout.flush();
    response = bin.readLine();

    dout.write(("QUIT\n").getBytes());
    dout.flush();
    response = bin.readLine();

    dout.close();
    bin.close();
    s.close();
  
  }
}
