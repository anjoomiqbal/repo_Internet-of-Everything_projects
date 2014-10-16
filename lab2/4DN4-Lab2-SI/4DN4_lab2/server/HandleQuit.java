package ftpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;

/**
 *
 * @author Syed Anjoom Iqbal
 * COMP ENG 4DN4 - 2014
 * Lab 2 - McNapster
 * McMaster University, Hamilton, ON, Canada
 * 
 * Class Name:  HandleQuit extends Thread
 * Description: This is the thread class to handle the quit command in FTP server.
 */
class HandleQuit extends Thread {
    	
    /**************************************************************
     *          FIELDS OF HandleQuit class
     *************************************************************/
    String cmd = "";                // To store server input command
    ServerSocket servSock;          // server socket
    static Boolean quit = false;    // Control flag for server QUIT command
    BufferedReader servCommand;     // To get input from Server Terminal
        
        
    /**************************************************************
     *          METHODS OF HandleQuit class
     *************************************************************/
    
    /** 
     * Method:  HandleQuit
     * @param:  ServerSocket servSock
     * @throws: <>
     * Description: This is the constructor of this class.
     */
    public HandleQuit(ServerSocket servSock) {
        this.servSock = servSock;   // get the server socket
        
        // create BufferedReader to take input from server terminal
        this.servCommand = 
                new BufferedReader(new InputStreamReader(System.in)); 
    }
    

    /** 
     * Method:  isServerRunning
     * @param:  <>
     * @throws: <>
     * return:  Boolean
     * Description: This method returns true if server is running,
     *              else it returns false.
     */
    public static Boolean isServerRunning() {
        return !HandleQuit.quit;
    }


    /** 
     * Method:  run
     * @param:  <>
     * @throws: <>
     * return:  void
     * Description: This is the run method of this thread. 
     *              When a thread object is created this method is executed.
     */
    public void run() {
        try {
            // Waits for QUIT command
            while (!cmd.equalsIgnoreCase("QUIT")) {
                    cmd = servCommand.readLine();
            }
            System.out.println("Server Command received:" 
                    + cmd.toUpperCase());

            System.out.println("Number of connected clients when server is getting closed = "
                    + FTPserver.connectedClientCount);

            HandleQuit.quit = true; // all client thread checks on this flag
            servSock.close();       // closing all socket connections
            System.out.println("All client connections has been closed");
            
            System.out.println("SERVER CLOSED SUCCESSFULLY");
            System.exit(0);         // exit the system safely
        } 
        catch (IOException e) {
            System.out.println("\n" + e.toString() + "\n");
        }
    }
    
}// end of class
