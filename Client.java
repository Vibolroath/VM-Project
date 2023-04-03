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
    int schedJob;
    String largestServerType = " ";
    int largestServerCores = 0;
    int largestServerID;
    int largestServerAvail = 0;
    ArrayList<String> largestServers = new ArrayList<String>();

    // send handshake message to the ds-server
    // send HELO to ds-server and read the response
    dout.write(("HELO\n").getBytes());
    dout.flush();
    response = bin.readLine();

    // send AUTH with username to ds-server and read the response
    dout.write(("AUTH " + userName + "\n").getBytes());
    dout.flush();
    response = bin.readLine();

    // send REDY to ds-server and read the response
    dout.write(("REDY\n").getBytes());
    dout.flush();
    response = bin.readLine();

    while (!response.equals("NONE")) {
      // array of strings containing JOBN and its details from ds-server
      String[] jobDetails = response.split(" ");
      // jobID is located on the second index of JOBN
      int jobID = Integer.parseInt(jobDetails[2]);

      if (response.startsWith("JOBN")) {
        // send GETS ALL to ds-server to get DATA and read the response
        dout.write(("GETS All\n").getBytes());
        dout.flush();
        response = bin.readLine();

        // send OK to ds-server
        dout.write(("OK\n").getBytes());
        dout.flush();

        // array of strings containing DATA and its details from ds-server
        String[] dataLoop = response.split(" ");
        int nServer = Integer.parseInt(dataLoop[1]);

        // if there are jobs available, read the response
        // if not, read and add the response to arraylist
        if (jobAvail) {
          for (int x = 0; x < nServer; x++) {
            response = bin.readLine();
          }
        } else if (!jobAvail) {
          for (int y = 0; y < nServer; y++) {
            response = bin.readLine();
            largestServers.add(response);

            // array of strings containing Server and its details from ds-server
            String[] serverDetails = response.split(" ");
            // assigning server type, id, cores based on their index location of server
            String serverType = serverDetails[0];
            int serverID = Integer.parseInt(serverDetails[1]);
            int serverCores = Integer.parseInt(serverDetails[4]);

            // finding largest server based on cores
            if (serverCores > largestServerCores) {
              largestServerType = serverType;
              largestServerCores = serverCores;
              largestServerID = serverID;
            }
          }
        }

        // check if there is an available largest server and count it
        if (!jobAvail) {
          for (String serverAvail : largestServers) {
            String[] serverDetails = serverAvail.split(" ");
            String serverType = serverDetails[0];
            int serverCores = Integer.parseInt(serverDetails[4]);
            if (serverType.equals(largestServerType) && serverCores == largestServerCores) {
              largestServerAvail++;
              jobAvail = true;
              break;
            }
          }
        }

        // send OK to ds-server and read the response
        dout.write(("OK\n").getBytes());
        dout.flush();
        response = bin.readLine();

        // use modulo to find largestserverid and start scheduling the jobs
        schedJob = jobID % largestServerAvail;
        largestServerID = schedJob;
        // send SCHD to ds-server to schedule jobs and read the response
        dout.write(("SCHD " + jobID + " " + largestServerType + " " + largestServerID + "\n").getBytes());
        dout.flush();
        response = bin.readLine();

        // send REDY to ds-server and read the response
        dout.write(("REDY\n").getBytes());
        dout.flush();
        response = bin.readLine();

      } else if (response.startsWith("JCPL")) {
        // send REDY to ds-server and read the response
        dout.write(("REDY\n").getBytes());
        dout.flush();
        response = bin.readLine();
      }
    }
    // send QUIT to ds-server and read the response
    dout.write(("QUIT\n").getBytes());
    dout.flush();
    response = bin.readLine();

    // closing the output-stream, input-stream, and socket
    dout.close();
    bin.close();
    s.close();
  }
}
