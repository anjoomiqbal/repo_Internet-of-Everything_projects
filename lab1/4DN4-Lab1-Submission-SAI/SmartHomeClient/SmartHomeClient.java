package smarthomeclient;

import java.net.*;  // for Socket
import java.io.*;   // for IOException and Input/OutputStream
import java.util.*;

/*
 *
 * @author Syed Anjoom Iqbal
 * COMP ENG 4DN4 - 2014
 * McMaster University, Hamilton, ON, Canada
 * 
 * Class Name:  SmartHomeClient
 * Description: This is the class to handle the client sides of 
 *              Internet-of-Everything (IoE) simplified model.
 */
public class SmartHomeClient {
    /**************************************************************
     *          PRIVATE FIELDS OF SmartHomeClient class
     *************************************************************/
    private static final int    BUFSIZE = 256;   // Size of receive buffer
    
    private static Socket       socket;
    private static String       ServerIP;
    private static int          ServerPort;
    private static boolean      ClientConnected = false;
    
    private static InputStream  in;
    private static OutputStream out;
    private static byte[]       byteBuffer;
    private static String       ReceivedMsg;
    private static boolean      exitStatus;
      
    
    /**************************************************************
     *          PRIVATE METHODS OF SmartHomeClient class
     *************************************************************/    
      /** 
     * Method:  checkArguments
     * @param:  String[] args
     * @throws: <>
     * return:  void
     * Description: This method checks for correctness of the argument(s)
     *              passed to the main function.
     */
    private static void checkArguments(String[] args){
        if (args.length > 0){  // Test for correct # of args
            throw new IllegalArgumentException("Parameter(s): None");
        }
    }
    
    /** 
     * Method:  handleUserInput
     * @param:  String UserInput
     * @throws: java.io.IOException
     * return:  void
     * Description: This method takes input from user. 
     *              If the user wants to connect to server then calls 'handleConnectCommand'.
     *              After establishing connection, any other user input is 
     *              handled by 'handleSocketTransactionsCommands'. 
     *              Also it clears byteBuffer and calls 'handleExitStatus'
     *              in case the server sent back the flag to exit the client.
     */
    private static void handleUserInput(String UserInput) 
            throws IOException{
        exitStatus = false;
        byteBuffer = new byte[BUFSIZE];     // clearing the byte buffer
        
        for(int i=0; i<byteBuffer.length; i++){
            byteBuffer[i]=0;
        }
        
        byteBuffer = UserInput.getBytes();
        
        String[] commands = getCommand(UserInput);  // getting the commands from input

        if (commands[0].equalsIgnoreCase("CONNECT")){
            handleConnectCommand (commands);
        } 
        else {    // for all other commands
            if (ClientConnected == false){
                System.out.println("Sorry! You are not connected."
                    + "\nConnect to the server before sending other commands.");
            } else {
                handleSocketTransactionsCommands (commands);
            }
        }
        
        byteBuffer = null;
        handleExitStatus();        
    }
    
    
    /** 
     * Method:  getCommand
     * @param:  String s
     * @throws: <>
     * return:  String[]
     * Description: This method parses the user's input to get the command
     *              and returns a String[] with the command texts
     */
    private static String[] getCommand (String s){
        // splitting input string based on white spaces
        String[] OutStrArray = s.trim().split("\\s+");
        // trimming white space and keeping only the text in the command
        for (int i=0; i<OutStrArray.length; i++){
            OutStrArray[i] = OutStrArray[i].trim();
        }
            
        return OutStrArray;
    }
  
    
    /** 
     * Method:  handleConnectCommand
     * @param:  String[] commands
     * @throws: java.io.IOException
     * return:  void
     * Description: When client tries to connect with the server then this
     *              this method tries to create a socket with the given parameters
     *              in the client's command
     */
    private static void handleConnectCommand (String[] commands) 
            throws IOException{
        if (ClientConnected == false){
            if (commands.length == 3){
                try {
                    ServerIP = commands[1];
                    ServerPort = Integer.parseInt(commands[2]);

                    socket = new Socket(ServerIP, ServerPort);

                    in = socket.getInputStream();
                    out = socket.getOutputStream();

                    System.out.println("Connected to server at:");
                    System.out.println("IP Address : " + ServerIP);
                    System.out.println("Port       : " + ServerPort);

                    ClientConnected = true;
                } 
                catch (ConnectException err) {
                    ServerIP = null;
                    ServerPort = -1;
                    System.out.println("FAILED to connect to server!");
                    System.out.println("Quick troubleshooting tips: \n"
                            + "Please check the followings \n"
                            + "\t 1. Server IP address is correct \n"
                            + "\t 2. Server Port number is correct \n"
                            + "\t 3. Server is running \n"
                            + "______________________________________");
                }
            } 
            else {
                    System.out.println("You have entered incorrect number of "
                            + "parameters for connecting to the server. \n");
                    printUsage();
            }
        } 
        else {
            System.out.println("This device is already connected");
        }
    }
  
    
    /** 
     * Method:  handleExitStatus
     * @param:  String[] commands
     * @throws: java.io.IOException
     * return:  void
     * Description: after connection is established with the server, 
     *              this method handles different commands from client 
     *              and sends that to the server. Also it listens to what
     *              server has sent back and shows that to the client. 
     *              Based on the reply from server, it can also close the socket.
     */
    private static void handleSocketTransactionsCommands (String[] commands) 
            throws IOException{
        
        out.write(byteBuffer);                // Send the encoded string to the server
        byteBuffer = new byte[BUFSIZE];       // re-initialize the buffer to full BUFSIZE
        in.read(byteBuffer);                  // reading input stream from socket
        ReceivedMsg = new String(byteBuffer); // generating string ReceivedMsg
        
        System.out.println("Received: " + ReceivedMsg);
        
        if (ReceivedMsg.startsWith("QUIT")){
            exitStatus = true;
            System.out.println("Thank you for using the Smart Home Service.");
        }
        else if (ReceivedMsg.startsWith("MAXIMUM INVALID COMMANDS")){
            exitStatus = true;
        }
        else if (ReceivedMsg.startsWith("INVALID COMMAND")){
            printUsage();
        }
    }

    
    /** 
     * Method:  handleExitStatus
     * @param:  void
     * @throws: java.io.IOException
     * return:  void
     * Description: perform the necessary operation to safely close client
     */
    private static void handleExitStatus() 
            throws IOException{
        if (exitStatus == true){
            System.out.println("Closing socket . . . ");
            ClientConnected = false;
            socket.close();
            System.out.println("Socket closed");
            System.exit(0);
        }
    }

    
    /** 
     * Method:  printUsage
     * @param:  void
     * @throws: <>
     * return:  void
     * Description: prints the supported command from client
     */
    private static void printUsage(){
        System.out.println("Here are the supported commands "
                + "(seperated by white space) ");
        System.out.println("----------------------------------------");
        System.out.println("CONNECT  <IP_address>  <Port>");
        System.out.println("ADD      <Device_name>");
        System.out.println("REMOVE   <Device_name>");
        System.out.println("READ     <Device_name>");
        System.out.println("WRITE    <Device_name>  <Write_value>");
        System.out.println("QUIT");
        System.out.println("----------------------------------------\n");
    }    
    


    /**************************************************************
     *          MAIN METHOD OF SmartHomeClient class
     * @param args
     * @throws java.io.IOException
     *************************************************************/
     
    /** 
     * Method:  main
     * @param:  args
     * return:  void
     * @throws: java.io.IOException
     */
    public static void main(String[] args) 
            throws IOException {

        checkArguments(args);

        System.out.println("Welcome, Smart Home Users!");

        Scanner commandInput;

        // tell the users about the valid usage
        printUsage();

        while(true){
//            System.out.println("main().while START");
            System.out.print("Please enter your command here : ");
            commandInput = new Scanner(System.in);
            String UserInput = commandInput.nextLine(); // taking user input
            handleUserInput(UserInput);  // taking action based on command

        }
    }



} // end of class : SmartHomeClient
