import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
  public static void main(String[] args) throws IOException {
    // connect to the server
    Socket s = new Socket("localhost", 50000);
    BufferedReader bin = new BufferedReader(new InputStreamReader(s.getInputStream()));
    DataOutputStream dout = new DataOutputStream(s.getOutputStream());
    String userName = System.getProperty("user.name");
    String response;
    boolean jobAvail = false;
    String largestServerType = " ";
    int largestServerCores = 0;
    int largestServerID;
    int largestServerCount = 0;
    ArrayList<String> largestServers = new ArrayList<String>();

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

    while (!response.equals("NONE")) {
      String[] jobDetails = response.split(" ");
      int jobID = Integer.parseInt(jobDetails[2]);

      if (response.startsWith("JOBN")) {
        dout.write(("GETS All\n").getBytes());
        dout.flush();
        response = bin.readLine();

        String[] dataLoop = response.split(" ");
        int nServer = Integer.parseInt(dataLoop[1]);

        dout.write(("OK\n").getBytes());
        dout.flush();

        if (jobAvail == true) {
          for (int x = 0; x < nServer; x++) {
            response = bin.readLine();
          }
        } else if (jobAvail == false) {
          for (int y = 0; y < nServer; y++) {
            response = bin.readLine();
            largestServers.add(response);

            String[] serverDetails = response.split(" ");
            String serverType = serverDetails[0];
            int serverID = Integer.parseInt(serverDetails[1]);
            int serverCores = Integer.parseInt(serverDetails[4]);

            if (serverCores > largestServerCores) {
              largestServerType = serverType;
              largestServerCores = serverCores;
              largestServerID = serverID;
            }
          }
        }
      } else if (response.startsWith("JCPL")) {
        dout.write(("REDY\n").getBytes());
        dout.flush();
        response = bin.readLine();
      }
    }
    dout.write(("QUIT\n").getBytes());
    dout.flush();
    response = bin.readLine();

    dout.close();
    bin.close();
    s.close();

  }
}
