import java.io.*;
import java.net.Socket;

public class Client {
  static Socket s;
  static BufferedReader bin;
  static DataOutputStream dout;

  public static void main(String[] args) throws Exception {
    // connect to the server
    s = new Socket("localhost", 50000);
    bin = new BufferedReader(new InputStreamReader(s.getInputStream()));
    dout = new DataOutputStream(s.getOutputStream());
    String userName = System.getProperty("user.name");
    String response;
    String fitServerType = "";
    int fitServerID = 0;

    // send handshake message to the ds-server
    // send HELO to ds-server and read the response
    writeMsg("HELO");
    response = bin.readLine();

    // send AUTH with username to ds-server and read the response
    writeMsg("AUTH " + userName);
    response = bin.readLine();

    while (!response.equals("NONE")) {
      boolean isFit = false;
      boolean firstFit = true;
      int serverWait = 0;
      int maxSeverWait = 0;

      // send REDY to ds-server and read the response
      writeMsg("REDY");
      response = bin.readLine();

      if (response.startsWith("JOBN")) {
        // array of strings containing JOBN and its details from ds-server
        String[] jobDetails = response.split(" ");
        // jobID is located on the second index of JOBN
        int jobID = Integer.parseInt(jobDetails[2]);
        int jobCore = Integer.parseInt(jobDetails[4]);
        int jobMemory = Integer.parseInt(jobDetails[5]);
        int jobDisk = Integer.parseInt(jobDetails[6]);

        // send GETS ALL to ds-server to get DATA and read the response
        writeMsg("GETS Capable " + jobCore + " " + jobMemory + " " + jobDisk);
        response = bin.readLine();

        // array of strings containing DATA and its details from ds-server
        String[] dataLoop = response.split(" ");
        int nServer = Integer.parseInt(dataLoop[1]);

        // send OK to ds-server and read the response
        writeMsg("OK");
        response = bin.readLine();

        for (int x = 0; x < nServer - 1; x++) {
          response = bin.readLine();

          // array of strings containing Server and its details from ds-server
          String[] serverDetails = response.split(" ");
          String serverType = serverDetails[0];
          int serverID = Integer.parseInt(serverDetails[1]);
          int serverCores = Integer.parseInt(serverDetails[4]);
          int serverMemory = Integer.parseInt(serverDetails[5]);
          int serverDisk = Integer.parseInt(serverDetails[6]);

          // find the first server encountered
          if (firstFit) {
            fitServerType = serverType;
            fitServerID = serverID;
            firstFit = false;
          }

          // check if the server is capable and available
          if (serverCores >= jobCore && serverMemory >= jobMemory && serverDisk >= jobDisk
              && isFit == false && serverWait <= maxSeverWait) {
            fitServerType = serverType;
            fitServerID = serverID;
            serverWait++;
            isFit = true;
          } else if (serverCores >= jobCore && serverMemory >= jobMemory && serverDisk >= jobDisk
              && isFit == false && serverWait > maxSeverWait) {
            fitServerType = serverType;
            fitServerID = serverID;
            isFit = true;
          }
        }

        // send OK to ds-server and read the response
        writeMsg("OK");
        response = bin.readLine();

        // send SCHD to ds-server to schedule jobs and read the response
        writeMsg("SCHD " + jobID + " " + fitServerType + " " + fitServerID);
        response = bin.readLine();

        // send REDY to ds-server and read the response
        writeMsg("REDY");
        response = bin.readLine();

      } else if (response.startsWith("JCPL")) {
        // send REDY to ds-server and read the response
        writeMsg("REDY");
        response = bin.readLine();
      }
    }

    // send QUIT to ds-server and read the response
    writeMsg("QUIT");
    response = bin.readLine();

    // closing the output-stream, input-stream, and socket
    writeClose();
  }

  // function for writing messages
  public static void writeMsg(String message) throws Exception {
    dout.write((message + "\n").getBytes());
    dout.flush();
  }

  // function for closing
  public static void writeClose() throws Exception {
    dout.close();
    bin.close();
    s.close();
  }
}
