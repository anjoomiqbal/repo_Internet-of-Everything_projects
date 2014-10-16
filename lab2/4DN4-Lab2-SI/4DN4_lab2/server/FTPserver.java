package ftpserver;

import java.net.*;  // for Socket, ServerSocket, and InetAddress
import java.io.*;   // for IOException and Input/OutputStream


/**
 *
 * @author Syed Anjoom Iqbal
 * COMP ENG 4DN4 - 2014
 * Lab 2 - McNapster
 * McMaster University, Hamilton, ON, Canada
 * 
 * Class Name:  FTPserver
 * Description: This is the class to handle the FTP server.
 */
public class FTPserver {
    
    /**************************************************************
     *          FIELDS OF FTPserver class
     *************************************************************/
    static int connectedClientCount;    // # of clients connected to the server
    private static int servPort;        // server port number
    
    
    /**************************************************************
     *          METHODS OF FTPserver class
     *************************************************************/ 
        
        
    /** 
     * Method:  isPortNumberValid
     * @param:  int portNum
     * @throws: <>
     * return:  void
     * Description: This method checks if the port number is valid or not.
     *              If it is invalid then it closes the application.
     */
    private static void isPortNumberValid(int portNum){
        if (portNum < 1001         // reserved ports
         || portNum > 65535)       // max port number
        {
            System.out.println(
                      "The port number you have entered is invalid \n"
                    + "Allowed range of port number: [1001, 65535] \n"
                    + "Next time, please enter a port number within the valid range.\n"
                    + "Closing FTPserver application ... \n");
            
            System.exit(0);
        }
        else {
            System.out.println("Port number is within valid range: [1001, 65535]");
        }
    }
    
    
    /** 
     * Method:  checkArguments
     * @param:  String[] args
     * @throws: <>
     * return:  void
     * Description: This method checks for correctness of the argument(s)
     *              passed to the main function of FTPserver.
     */
    private static void checkArguments(String[] args){
        
        // checking arguments and getting port number 
        servPort = 2014;                // default port number
        
        // Test for correct # of args. 
        // Max # of args is 1 as only server port number is needed
        if (args.length > 1) {
          throw new IllegalArgumentException("Parameter(s): [Port]");
        } 
        
        // when 1 arg has been passed and it is the server port number 
        // then store that in the 'servPort' field. 
        // if anything other than a number has been passed as argument then 
        // it throws a NumberFormatException
        else if (args.length == 1) {
            servPort = Integer.parseInt(args[0]); // Server port
        } 
        // When no arg has been passed for port number
        else {                        
            System.out.print(
                      "Valid range for port number = [1001, 65535] \n"
                    + "Enter port number for the server : ");
            
            // try to get the port number from user
            try{
                BufferedReader bufferRead = 
                        new BufferedReader(new InputStreamReader(System.in));
                String s = bufferRead.readLine();
                servPort = Integer.parseInt(s); // Server port
                
                isPortNumberValid(servPort);
            }
            catch(IOException e){
                System.out.println("\n" + e.toString() + "\n");
                e.printStackTrace();
            }
            // catch exception if anything other than integer has been passed as argument
            catch(NumberFormatException e){
                System.out.println("\n" + e.toString() + "\n");
                System.out.println(
                          "You have enterd a 'port number' which is not an integer. \n"
                        + "Next time, please enter a pure integer value for port number. \n"
                        + "Closing FTPserver application ... \n");
                
                // close the application after displaying appropriate message
                System.exit(0);
            }
        }
    }
    
            
    /** 
     * Method:  printUsage
     * @param:  void
     * @throws: <>
     * return:  void
     * Description: prints the supported command from server terminal
     */
    static void printUsage(){
        System.out.println(
                "\nHere is the supported command from FTP Server terminal:\n"
                + "------------------------------------------------\n"
                + "<COMMAND>     <DESCRIPTION> \n"
                + "QUIT          Exit from the server after closing \n"
                + "              the connection(s) with the \n"
                + "              clients, if there are any.\n"
                + "------------------------------------------------\n\n");
    }   
    
    
    
    /** 
     * Method:  main
     * @param:  String[] args
     * @throws: java.io.IOException
     * return:  void
     * Description: This is the main method of the FTP server
     */
    public static void main(String[] args) throws IOException {
        
        // check arguments and get the server port number
        checkArguments(args);
        System.out.println("Argument check passed and obtained server port : "
                + servPort);
        
        // Create a server socket to accept client connection requests
        ServerSocket servSock = new ServerSocket(servPort);   
        System.out.println("Server started at port : " + servPort);
        
        // initialize the connected client count to zero when the FTP server is started
        FTPserver.connectedClientCount = 0;
        
        // thread for checking server terminal input for quit command
        HandleQuit handleQuit = new HandleQuit(servSock);
        handleQuit.start();     // start the thread
        
        // show the available commands from server terminal
        printUsage();
        
        // Run as long as the server is running
        while (HandleQuit.isServerRunning()) {
            try {

                // create a new thread when a client connection is accepted in
                // the blocking accept method of the constructor of FTPserverProtocol
                FTPserverProtocol clntThread = new FTPserverProtocol(servSock);            
                clntThread.start();     // start the thread

                // increment the total connected client count
                FTPserver.connectedClientCount++;

                System.out.println("\n------------------------------------\n");
                System.out.println("Created & started Thread : " 
                        + clntThread.getName());
            } 
            catch (IOException e) {
//                System.out.println("\n" + e.toString() + "\n");
            }
        }
    } // end of main method
    
}// end of class
