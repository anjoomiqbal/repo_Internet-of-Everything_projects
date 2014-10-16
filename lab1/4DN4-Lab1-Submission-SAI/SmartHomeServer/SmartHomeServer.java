package smarthomeserver;

import java.net.*;  // for Socket, ServerSocket, and InetAddress
import java.io.*;   // for IOException and Input/OutputStream
import java.util.*;


/**
 *
 * @author Syed Anjoom Iqbal
 * 
 * COMP ENG 4DN4 - 2014
 * McMaster University
 * Hamilton, ON, Canada
 * 
 * Class Name:  SmartHomeServer
 * Description: This is the class to handle the server side of 
 *              Internet-of-Everything (IoE) simplified model. 
 */
public class SmartHomeServer {
    
    /**************************************************************
     *          PRIVATE FIELDS OF SmartHomeServer class
     *************************************************************/
    private static final int BUFSIZE = 128;   // Size of receive buffer
    private static final ArrayList<Device> HomeDevices = new ArrayList<>(); 
    private static InputStream in;
    private static OutputStream out;
    private static byte[] byteBuffer;
    private static boolean clientExitStatus = false;
    private static int invalidTry = 1;
    private static final int MAX_INVALID_TRY = 3;

    
    /**************************************************************
     *          PRIVATE METHODS OF SmartHomeServer class
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
      if (args.length != 1)  
        throw new IllegalArgumentException("Parameter(s): <Port>");
    }

       
    /** 
     * Method:  createServerSocket
     * @param:  int servPort
     * @throws: java.io.IOException
     * return:  ServerSocket
     * Description: This method creates a ServerSocket object 
     *              on the port servPort and returns the ServerSocket object.
     */
    private static ServerSocket createServerSocket(int servPort) 
            throws IOException{
        ServerSocket servSock = new ServerSocket(servPort);
        System.out.println("Smart Home Server started at port " + servPort);
        return servSock;
    }
    
    
    /** 
     * Method:  getCommand
     * @param:  String s
     * @throws: <>
     * return:  String[]
     * Description: This method parses the user's input to get the command
     *              and returns a String[] with the command texts.
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
     * Method:  SendClientString
     * @param:  OutputStream out, String s
     * @throws: java.io.IOException
     * return:  void
     * Description: This method sends the string s to the connected client 
     *              through the OutputStream object out. It also prints 
     *              the String s in server terminal.
     */
    private static void SendClientString(OutputStream out, String s) 
            throws IOException{
        System.out.println(s);
        out.write(s.getBytes());
    }
  
         
    /** 
     * Method:  initializeDatabase
     * @param:  <>
     * @throws: <>
     * return:  void
     * Description: This method initializes the HomeDevice database in the server
     *              with previously known Device objects with their attributes.
     */
    private static void initializeDatabase(){
        HomeDevices.add(new Device("Thermostat-Main", "19", "23"));
        HomeDevices.add(new Device("Thermostat-Living-Room", "18", "22"));
    }
    
           
    /** 
     * Method:  establishSocket
     * @param:  ServerSocket servSock
     * @throws: java.io.IOException
     * return:  Socket
     * Description: This method waits on blocking 'accept' method of the 
     *              ServerSocket object 'servSock' that was passed as parameter.
     *              When 'servSock' accepts a connection from a client then 
     *              this method returns the Socket object 'clntSock'.
     */
    private static Socket establishSocket(ServerSocket servSock) 
            throws IOException{
            // Get client connection
            System.out.println("Waiting on blocking accept...");
            Socket clntSock = servSock.accept();
            in  = clntSock.getInputStream();
            out = clntSock.getOutputStream();

            System.out.println("Handling client "
                    + "at " + clntSock.getInetAddress().getHostAddress() 
                    + " on port " + clntSock.getPort());

            // Receive buffer
            byteBuffer = new byte[BUFSIZE];
            
            return clntSock;
    }
    
       
    /** 
     * Method:  handleAddCommand
     * @param:  OutputStream out, String[] commands
     * @throws: java.io.IOException
     * return:  void
     * Description: When a socket is created 
     *              and the client sends command to ADD a device 
     *              then this method handles the required action.
     */
    private static void handleAddCommand (OutputStream out, String[] commands) 
            throws IOException{
        int i;
        for (i=0; i<HomeDevices.size() && (i != -1); i++){
            if (HomeDevices.get(i).getDeviceName().equalsIgnoreCase(commands[1])){
                String OutStr = "ADD failed: '" + 
                        commands[1].toUpperCase() + "' already exists.";
                SendClientString(out, OutStr);
                i = -2;
            }
        }
        if (i != -1){
            HomeDevices.add(new Device(commands[1]));
            String OutStr = "ADD success: '" + 
                    commands[1].toUpperCase() + "' has been added.";
            SendClientString(out, OutStr);
        }        
    }
    
       
    /** 
     * Method:  handleRemoveCommand
     * @param:  OutputStream out, String[] commands
     * @throws: java.io.IOException
     * return:  void
     * Description: When a socket is created 
     *              and the client sends command to REMOVE a device 
     *              then this method handles the required action.
     */
    private static void handleRemoveCommand (OutputStream out, String[] commands) 
            throws IOException{
        int i;
        for (i=0; i<HomeDevices.size() && (i != -1); i++){
            if (HomeDevices.get(i).getDeviceName().equalsIgnoreCase(commands[1])){
                HomeDevices.remove(HomeDevices.get(i));
                String OutStr = "REMOVE success: '" + 
                        commands[1].toUpperCase() + "' has been removed.";
                SendClientString(out, OutStr);
                i = -2;
            }
        }
        if (i != -1){
            String OutStr = "REMOVE failed: '" + 
                        commands[1].toUpperCase() + "' does not exist.";
                SendClientString(out, OutStr);
        }
    }
    
       
    /** 
     * Method:  handleReadCommand
     * @param:  OutputStream out, String[] commands
     * @throws: java.io.IOException
     * return:  void
     * Description: When a socket is created 
     *              and the client sends command to READ from a device 
     *              then this method handles the required action.
     */
    private static void handleReadCommand (OutputStream out, String[] commands) 
            throws IOException{
        int i;
        for (i=0; i<HomeDevices.size() && (i != -1); i++){
            if (HomeDevices.get(i).getDeviceName().equalsIgnoreCase(commands[1])){
                String OutStr = "READ success: '" + 
                        commands[1].toUpperCase() + "' \nread value = " + 
                        HomeDevices.get(i).getRdValue() + 
                        "\nwrite value = " + HomeDevices.get(i).getWrValue();
                SendClientString(out, OutStr);
                i = -2;
            }
        }
        if (i != -1){
            String OutStr = "READ failed: '" + 
                        commands[1].toUpperCase() + "' does not exist.";
                SendClientString(out, OutStr);
        }
    }
    
      
    /** 
     * Method:  handleWriteCommand
     * @param:  OutputStream out, String[] commands
     * @throws: java.io.IOException
     * return:  void
     * Description: When a socket is created 
     *              and the client sends command to WRITE to a device 
     *              then this method handles the required action.
     */
    private static void handleWriteCommand (OutputStream out, String[] commands) 
            throws IOException{
        int i;
        for (i=0; i<HomeDevices.size() && (i != -1); i++){
            if (HomeDevices.get(i).getDeviceName().equalsIgnoreCase(commands[1])){
                HomeDevices.get(i).setWrValue(commands[2]);
                HomeDevices.get(i).setRdValue(commands[2]);
                String OutStr = "WRITE success: '" + 
                        commands[1].toUpperCase() + "' written value = " + 
                        commands[2];
                SendClientString(out, OutStr);
                i = -2;
            }
        }
        if (i != -1){
            String OutStr = "WRITE failed: '" + 
                        commands[1].toUpperCase() + "' does not exist.";
            SendClientString(out, OutStr);
        }
    }
    
    
    
    /**************************************************************
     *          MAIN METHOD OF SmartHomeClient class
     *************************************************************/
    
    /** 
     * Method:  main
     * @param:  args
     * return:  void
     * @throws: java.io.IOException
     */
    public static void main(String[] args) 
            throws IOException {
        
        // Testing for correct # of args
        checkArguments(args);   

        // getting the port number 
        int servPort = Integer.parseInt(args[0]);

        // Creating a server socket to accept client connection requests
        ServerSocket servSock = createServerSocket(servPort);
        
        // initializing database with some dummy values
        initializeDatabase();
            
        // Get client connection
        Socket clntSock = establishSocket(servSock);
        while (true){
            try{
                // Receive until client closes connection, indicated by -1 return  
                while (in.read(byteBuffer) != -1){ 

                    // generating String array from the white space seperated user input
                    String[] commands = getCommand(new String(byteBuffer));       

                    if      (commands[0].equalsIgnoreCase("ADD")    && commands.length == 2){
                        handleAddCommand (out, commands);
                    }
                    else if (commands[0].equalsIgnoreCase("REMOVE") && commands.length == 2){
                        handleRemoveCommand (out, commands);
                    }
                    else if (commands[0].equalsIgnoreCase("READ")   && commands.length == 2){
                        handleReadCommand (out, commands);
                    }  
                    else if (commands[0].equalsIgnoreCase("WRITE")  && commands.length == 3){
                        handleWriteCommand (out, commands);
                    }
                    else if (commands[0].equalsIgnoreCase("QUIT")   && commands.length == 1){
                        SendClientString(out, "QUIT");
                        clientExitStatus = true; 
                        }
                    else {
                        SendClientString(out, "INVALID COMMAND");
                        if (invalidTry >= MAX_INVALID_TRY){ 
                            SendClientString(out, "Socket connection has been forced to close as ");
                            SendClientString(out, "MAXIMUM INVALID COMMANDS "
        //                            + "(" +  MAX_INVALID_TRY + ") SENT by"
                                + " client at " + clntSock.getInetAddress().getHostAddress() 
                                + " on port " + clntSock.getPort());
                            clientExitStatus = true;
                        } else {
                            invalidTry++;

                        }
                    }

                    // closing existing connection and waiting for a new one
                    if (clientExitStatus == true){
                        clientExitStatus = false;
                        invalidTry = 1;
                        clntSock.close();           // Closing the socket
                        establishSocket(servSock);
                    }

                    // clearing buffer
                    byteBuffer = new byte[BUFSIZE];
                }
            }
            catch (SocketException Se){
                clntSock.close();
                System.out.println("Socket has been closed due to termination of client");
                establishSocket(servSock);
            }
        }
    }
}
