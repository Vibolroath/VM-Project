import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {
  public static void main(String[] args) throws IOException {
    // connect to the server
    Socket s = new Socket("localhost", 50000);
    BufferedReader bin = new BufferedReader(new InputStreamReader(s.getInputStream()));
    DataOutputStream dout = new DataOutputStream(s.getOutputStream());
    String userName = System.getProperty("user.name");
    String response;

    // send handshake message to the ds-server
    // send HELO to ds-server and read the response
    dout.write(("HELO\n").getBytes());
    dout.flush();
    response = bin.readLine();

    // send AUTH with username to ds-server and read the response
    dout.write(("AUTH " + userName + "\n").getBytes());
    dout.flush();
    response = bin.readLine();

    while (!response.equals("NONE")) {
      // send REDY to ds-server and read the response
      dout.write(("REDY\n").getBytes());
      dout.flush();
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
        dout.write(("GETS Capable " + jobCore + " " + jobMemory + " " + jobDisk + "\n").getBytes());
        dout.flush();
        response = bin.readLine();

        // array of strings containing DATA and its details from ds-server
        String[] dataLoop = response.split(" ");
        int nServer = Integer.parseInt(dataLoop[1]);

        // send OK to ds-server
        dout.write(("OK\n").getBytes());
        dout.flush();
        response = bin.readLine();

        // create a list of servers
        ArrayList<Server> servers = new ArrayList<>();
        for (int x = 0; x < nServer; x++) {
          // array of strings containing Server and its details from ds-server
          String[] serverDetails = response.split(" ");
          String serverType = serverDetails[0];
          int serverID = Integer.parseInt(serverDetails[1]);
          int serverCores = Integer.parseInt(serverDetails[4]);
          int serverMemory = Integer.parseInt(serverDetails[5]);
          int serverDisk = Integer.parseInt(serverDetails[6]);

          // create a new Server object and add it to the list of servers
          Server server = new Server(serverType, serverID, serverCores, serverMemory, serverDisk);
          servers.add(server);

          response = bin.readLine();
        }

        // send OK to ds-server and read the response
        dout.write(("OK\n").getBytes());
        dout.flush();
        response = bin.readLine();

        // create a new Job object
        Job job = new Job(jobID, jobCore, jobMemory, jobDisk);

        // use the firstFit algorithm to find a server for the job to be scheduled on
        Server selectedServer = firstFit(job, servers);

        if (selectedServer != null) {
          // send SCHD to ds-server to schedule jobs and read the response
          dout.write(("SCHD " + jobID + " " + selectedServer.getType() + " " + selectedServer.getID() +
              "\n").getBytes());
          dout.flush();
          response = bin.readLine();
        }

        // // send SCHD to ds-server to schedule jobs and read the response
        // dout.write(("SCHD " + jobID + " " + serverType + " " + serverID +
        // "\n").getBytes());
        // dout.flush();
        // response = bin.readLine();

        // for (int x = 0; x < nServer - 1; x++) {
        // if (jobCore >= serverCores && jobMemory >= serverMemory && jobDisk >=
        // serverDisk) {
        // // send SCHD to ds-server to schedule jobs and read the response
        // dout.write(("SCHD " + jobID + " " + serverType + " " + serverID +
        // "\n").getBytes());
        // dout.flush();
        // response = bin.readLine();
        // }
        // }

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

  public static Server firstFit(Job j, List<Server> servers) {
    // search for a server s*, from the first one to last one in servers,
    // that first satisfies conditions of
    // 1. Having sufficient resources readily available for j and
    // 2. Not having running jobs and waiting jobs at the same time.
    for (Server s : servers) {
        if (s.hasSufficientResourcesFor(j) && !s.hasRunningJobs() && !s.hasWaitingJobs()) {
            return s;
        }
    }
    // If no s* found, select the first Active/Booting server with sufficient
    // resource capacity ('Capable') regardless of availability,
    // i.e., it is busy processing (running or waiting to run) one or more jobs.
    for (Server s : servers) {
        if (s.isActive() || s.isBooting() && s.hasSufficientResourcesFor(j)) {
            return s;
        }
    }
    return null;
}
}
