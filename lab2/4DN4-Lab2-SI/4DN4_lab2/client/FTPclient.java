package ftpclient;

import java.net.*;  // for Socket
import java.io.*;   // for IOException and Input/OutputStream

/**
 *
 * @author Syed Anjoom Iqbal
 * COMP ENG 4DN4 - 2014
 * Lab 2 - McNapster
 * McMaster University, Hamilton, ON, Canada
 * 
 * Class Name:  FTPclient
 * Description: This is the class to handle the FTP client
 */
public class FTPclient {
    
    /**************************************************************
     *          FIELDS OF FTPclient class
     *************************************************************/
    private static final int    BUFSIZE = 1024;   // Size of receive buffer
    private static final String CLNT_DIR = "C:\\Users\\Anjoom\\Desktop\\client\\pub\\MUSIC\\";
    
    private Socket       socket;
    private String       ServerIP;
    private int          ServerPort;
    
    private InputStream  clntIn;
    private OutputStream clntOut;
    
    private boolean      fileTransferInProgress;
    
    
    /**************************************************************
     *          METHODS OF FTPclient class
     *************************************************************/    
        
    /** 
     * Method:  FTPclient
     * @param:  <>
     * @throws: <>
     * Description: This is the constructor of this class
     */
    public FTPclient(){
        this.fileTransferInProgress = false;
    }
    
    
    /** 
     * Method:  checkArguments
     * @param:  String[] args
     * @throws: <>
     * return:  void
     * Description: This method checks for correctness of the argument(s)
     *              passed to the main function of FTPclient.
     */
    private void checkArguments(String[] args){
        ServerIP = "";
        ServerPort  = 7;        // default echo port
        
        // Test for correct # of args
        // if args # is more than 2 
        // then something is wrong.
        // so throw an exception
        if (args.length > 2) {
          throw new IllegalArgumentException("Parameter(s): [Server_IP] [Port]");
        } 
        
        // if 2 args has been passed then
        else if (args.length == 2) {
            // set the first arg as serverIP
            ServerIP   = args[0];
            
            // set the second arg as the serverPort
            ServerPort = Integer.parseInt(args[1]);
        }
        
        // when less than 2 arg 
        // or no arg has been passed 
        // then get the arg from user
        else {
            try{
                // get the server IP address from user
                System.out.print("Enter Server IP Address : ");
                BufferedReader bufferRead;
                bufferRead = new BufferedReader(new InputStreamReader(System.in));
                ServerIP   = bufferRead.readLine();
                
                // get the server port number from user
                System.out.print("Enter Server Port Number : ");
                bufferRead = new BufferedReader(new InputStreamReader(System.in));
                String s   = bufferRead.readLine();
                ServerPort = Integer.parseInt(s);
            }
            catch(IOException e){
                System.out.println("\n" + e.toString() + "\n");
                e.printStackTrace();
            }
            
            // when something other than number
            // has been entered as port number
            // then throw a NumberFormatException
            catch(NumberFormatException e){
                System.out.println("\n" + e.toString() + "\n");
                System.out.println(
                          "You have enterd a 'port number' which is not an integer. \n"
                        + "Next time, please enter a pure integer value for port number. \n"
                        + "Closing FTPserver application ... \n");
                System.exit(0);     // close the application
            }
        }
    }
    
    
    /** 
     * Method:  connectToServer
     * @param:  <>
     * @throws: java.io.IOException
     * return:  void
     * Description: This method tries to establish connection with the server
     */
    private void connectToServer () throws IOException{
        try {
            socket = new Socket(ServerIP, ServerPort);  // create new socket object
            socket.setSendBufferSize(BUFSIZE);          // set the socket send buffer to BUFFSIZE
            socket.setReceiveBufferSize(BUFSIZE);       // set the socket receive buffer to BUFFSIZE
            
            clntIn  = socket.getInputStream();      // get the InputStream of the socket
            clntOut = socket.getOutputStream();     // get the OutputStream of the socket
            
            // show the client the information of
            // connected server ip and port number 
            System.out.println("Connected to server at:");
            System.out.println("IP Address : " + ServerIP );
            System.out.println("Port       : " + ServerPort);
            
            // show all the allowed commands in 
            // client terminal after the connecttion 
            // has ben establised with the server
            this.printUsage();

        } 
        // catch the exception when client 
        // tries to connect to an IP address
        // that is not in correct format
        catch (UnknownHostException e){
            System.out.println("\n" + e.toString() + "\n");
            System.out.println("The server IP address entered, is in incorrect format. \n"
                    + "Correct format for server IP: <int>.<int>.<int>.<int> \n"
                    + "For example: 1.2.3.4 \n"
                    + "Next Time, please try with a valid server IP address. \n"
                    + "Closing application... \n\n");
            System.exit(0);     // close the application
        }
        
        // catch any ConnectException and 
        // print the possible reasons as tips
        catch (ConnectException err) {
            System.out.println("\n" + err.toString() + "\n");
            ServerIP = null;
            ServerPort = -1;
            System.out.println("FAILED to connect to server!");
            System.out.println("Quick troubleshooting tips: \n"
                    + "Please check the followings \n"
                    + "\t 1. Server is running \n"
                    + "\t 2. Server IP address is correct \n"
                    + "\t 3. Server Port number is correct \n"
                    + "Please try to start the client after troubleshooting.\n"
                    + "Thank you.\n"
                    + "______________________________________\n\n");
            
            System.exit(0);     // close the application
        }
                // catch any ConnectException and 
        // print the possible reasons as tips
        catch (NoRouteToHostException e) {
            System.out.println("\n" + e.toString() + "\n");
            System.out.println("FAILED to connect to server!");
            System.out.println("There is no route to host : " 
                    + ServerIP
                    + ":"
                    + ServerPort
                    + "\n"
                    + "Please try with a IP address where a server is hosted. \n"
                    + "Thank you.\n"
                    + "______________________________________\n\n");
            
            ServerIP = null;
            ServerPort = -1;
            System.exit(0);     // close the application
        }
    }
    
        
    /** 
    * Method:  getCommand
    * @param:  String s
    * @throws: <>
    * return:  String[]
    * Description: This method parses the user's input to get the command
    *              and returns a String[] with the command texts.
    */
    private String[] getCommand (String s){
        // split the string s by white space
        String[] OutStrArray = s.trim().split("\\s+");
        
        // trimming white space and keeping only the text in the command
        for (int i=0; i<OutStrArray.length; i++){
            OutStrArray[i] = OutStrArray[i].trim();
        }
        
        return OutStrArray;
    }
    

    /** 
     * Method:  getFileSize
     * @param:  String filePath
     * @throws: java.io.IOException
     * return:  int fileSizeInBytes
     * Description: This method returns the size (in bytes) 
     *              of the file located in 'filePath'
     */
    private int getFileSize(String filePath) throws IOException {
        FileInputStream fileByteStream = null;
        int fileSizeInBytes = 0;
        try {
            // create new FileInputStream with the file
            fileByteStream = new FileInputStream(filePath);

            // keep reading the FileInputStream 
            // and keep incrementing the filesize
            // as long as it can read another byte
            while ((fileByteStream.read()) != -1) {
                fileSizeInBytes++;
            }
        } catch (IOException e) {
            System.out.println("\n" + e.toString() + "\n");
            e.printStackTrace();
        }

        // close the file if it was opened successfully
        if (fileByteStream != null) {
            fileByteStream.close();
        }

        return fileSizeInBytes;    
    }
    
    
    /** 
     * Method:  fileExists
     * @param:  String fileName, String directory
     * @throws: <>
     * return:  boolean
     * Description: This method checks if the file with name 'fileName' 
     *              exists in the location 'directory'. 
     *              If yes then returns true,
     *              else returns false.
     */
    private boolean fileExists(String fileName, String directory){
        boolean fileExists = false;
        
        // create a file array with the directory
        File[] fileArray = new File(directory).listFiles();

        // go through all the files in the directory
        for (int i = 0; i < fileArray.length; i++){
            
            // if the element in the array is a file
            if (fileArray[i].isFile()) {
                
                // get the name of the file
                String s = fileArray[i].getName();
                System.out.println( "client file: '" + s + "'");
                
                // when the name of the file matches 
                // the name of the element in file array
                if (s.equalsIgnoreCase(fileName)){
                    fileExists = true;  // return true and exit
                    break;
                }
            }
        }
        
        return fileExists;
    }
    
                  
    /** 
     * Method:  SendServerString
     * @param:  String s
     * @throws: java.io.IOException
     * return:  void
     * Description: This method sends the string s to the connected server
     */
    private boolean SendServerString(String s) throws IOException{
        // write the stream in the OutputStream
        clntOut.write(s.getBytes());
        return true;
    }
    
    
    /** 
     * Method:  processListCommand
     * @param:  String[] commands
     * @throws: java.io.IOException
     * return:  void
     * Description: This method handles 
     *              LIST-ALL, LIST-DETAILS, LIST-NAMES commands from client
     */
    private void processListCommand (String[] commands) throws IOException{
        try {
            System.out.println("--> processListCommand START");

            // send LIST_ALL command to server thread
            SendServerString(commands[0]);
            this.fileTransferInProgress = true;
            
            // initialize book keeping fields
            int bytesReceived = BUFSIZE;
            byte[] byteBuffer = new byte[BUFSIZE]; 
            String fs = "";

            // keep receiving file list data from server
            while (bytesReceived >= BUFSIZE){
                bytesReceived = clntIn.read(byteBuffer);
                
                // keep adding the string created 
                // by the received bytes 
                //to get the full list of files
                fs += new String(byteBuffer);   
                
                // clear buffer
                byteBuffer = new byte[BUFSIZE]; 
            }

            this.fileTransferInProgress = false;

            fs = fs.trim();

            System.out.println();
            System.out.println(commands[0].toUpperCase() + " :");
            System.out.println("_________________");
            System.out.println(fs);
            System.out.println("_________________\n");

            System.out.println("<-- processListCommand DONE");
        }
        
        // catch socket exception 
        catch (SocketException e){
            System.out.println("\n" + e.toString() + "\n");
            
            if (this.fileTransferInProgress == true){
                System.out.println("Connection terminated before receiving the list");
            }
            socket.close(); // close socket if there is an exception
            System.exit(0); // close the application
        } 
    }
     
    
    /** 
     * Method:  processReadCommand
     * @param:  String[] commands
     * @throws: java.io.IOException
     * return:  void
     * Description: This method handles the READ command from client
     */
    private void processReadCommand (String[] commands) throws IOException{ 
        File wrFileToClnt = null;
        FileOutputStream wrFileStream = null;
        try {
            System.out.println("--> processReadCommand START");

            // send the READ command to server
            SendServerString(commands[0]);

            // Send the name of file to be read to the server
            SendServerString(commands[1]);

            // receive the total Number Of Bytes In the File from the server
            byte[] byteBuffer = new byte[BUFSIZE]; 
            clntIn.read(byteBuffer);
            String s = new String(byteBuffer);
            s=s.trim();
            
            // when the file exists in the the server
            if (!s.equalsIgnoreCase("NO SUCH FILE")){
                
                this.fileTransferInProgress = true;
                
                int totalNumberOfBytesInFile = Integer.parseInt(s);
                System.out.println("totalNumberOfBytesInFile: " + totalNumberOfBytesInFile);

                System.out.println("Reading File: " + commands[1]);
                System.out.println("Please Wait ...");

                // Write the file to the Client directory
                String newFilePath = CLNT_DIR+commands[1];                
                wrFileToClnt = new File(newFilePath);

                int bytesReceived = BUFSIZE;
                int totalBytesWritten = 0;
                
                // if file does not exist 
                // then create the file
                if(!wrFileToClnt.exists()){
                    System.out.println("new file created");
                    wrFileToClnt.createNewFile();
                }
                
                // create a FileOutputStream in the client 
                // where the read file will be stored
                wrFileStream = new FileOutputStream(wrFileToClnt); 
                
                // keep receiving the data over socket 
                // and write them to the file in clients
                while ( bytesReceived >= BUFSIZE){
                    byteBuffer = new byte[BUFSIZE]; 
                    bytesReceived = clntIn.read(byteBuffer);
                    wrFileStream.write(byteBuffer);
                    totalBytesWritten += bytesReceived;
                }

                // show a warning if there is a mismatch
                // between totalBytesWritten 
                // and totalNumberOfBytesInFile
                if (totalBytesWritten != totalNumberOfBytesInFile){
                    System.out.println("Warning: totalBytesWritten != "
                            + "totalNumberOfBytesInFile");
                }

                // close the file in client
                System.out.println("Closing File");
                wrFileStream.close();
                this.fileTransferInProgress = false;
            }
            
            // when the file does not exist in the the server
            else {
                System.out.println("In the server, there is no such file named: " 
                        + commands[1]);
            }
            System.out.println("<-- processReadCommand DONE");
        }
        catch (SocketException e){
            
            System.out.println("\n" + e.toString() + "\n");
                
            if (this.fileTransferInProgress == true){
                System.out.println("Connection terminated before reading the total file");
                System.out.println("Removing broken file from Client pub...");
            }
            socket.close();     // close the socket
            System.exit(0);     // close the application
        }
    }
    

    /** 
     * Method:  processWriteCommand
     * @param:  String[] commands
     * @throws: java.io.IOException
     * return:  void
     * Description: This method handles the WRITE command from client
     */
    private void processWriteCommand (String[] commands) throws IOException{    
        System.out.println("--> processWriteCommand START");
        
        // check if the file exists in the client directory or not
        if(fileExists(commands[1], CLNT_DIR)){
        
            this.fileTransferInProgress = true;
            
            // Send the WRITE command to server
            SendServerString(commands[0]);

            // Send the wrFileName to server
            SendServerString(commands[1]);
        
            // Send file size to server
            String clntFilePath = CLNT_DIR + commands[1];
            int wrFileSize = getFileSize(clntFilePath);
            SendServerString(Integer.toString(wrFileSize)); 

            // print the file size in client terminal
            System.out.println("wrFileSize = " + wrFileSize);

            System.out.println("Writting...");
            
            // Send requested to client 
            FileInputStream rdFileStream = null;        
            rdFileStream = new FileInputStream(clntFilePath);        
            int rdByteCount = BUFSIZE;
            int totalRdByteCount = 0;
            byte[] byteBuffer = new byte[BUFSIZE]; 

            // keep reading the file 
            // till the end of the file
            while((rdByteCount = rdFileStream.read(byteBuffer)) != -1){

                // keep sending chunks of 
                // 1024 bytes (= BUFSIZE)
                // till the 2nd last chunk
                // as the last chunk is most
                // likely not to be of 1 KB
                if (rdByteCount >= BUFSIZE){
                    clntOut.write(byteBuffer);
                    byteBuffer = new byte[BUFSIZE]; 
                    totalRdByteCount += rdByteCount;
                }
                // sending the last chunk <= 1KB
                else {
                    byte[] fileByteBuffer = new byte[rdByteCount]; 
                    for (int i = 0; i < rdByteCount; i++){
                        fileByteBuffer[i] = byteBuffer[i];
                    }
                    clntOut.write(fileByteBuffer);
                    totalRdByteCount += rdByteCount;
                }
            }

            System.out.println("totalRdByteCount = " + totalRdByteCount);

            // close the file as long as 
            // it was open successfully
            if (rdFileStream != null) {
                    rdFileStream.close();
            }

            this.fileTransferInProgress = false;
        }
        else{
            System.out.println("In client directory, there is no such file named : " + commands[1]);
        }
        
        System.out.println("<-- processWriteCommand DONE");
    }
    
  
    /** 
     * Method:  processByeCommand
     * @param:  String commands
     * @throws: java.io.IOException
     * return:  void
     * Description: This method handles the BYE command from client
     */
    private void processByeCommand (String command) throws IOException{  
        
        // send the BYE command to the server 
        // so that server can know that this 
        // client has terminated itself
        Boolean done = SendServerString(command);
        
        // after sending the bye command 
        // close  the client application
        if (done == true){
            System.out.println("Client closed connection sucessfully!");
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
    private void printUsage(){
        System.out.println("\nHere are the supported commands from FTP Client terminal: \n"
                + "------------------------------------------------------------------ \n"
                + "<COMMAND>          <DESCRIPTION> \n"
                + "HELP               Lists all the supported commands and show usage\n"
                + "LIST-ALL           List the files and their sizes from \n"
                + "                   the server’s current music directory.\n"
                + "LIST-DETAILS       List the files and their sizes from \n"
                + "                   the server’s current music directory.\n"
                + "LIST-NAMES         List the file names from \n"
                + "                   the server’s current music directory.\n"
                + "READ <filename>    The client requests the file \n" 
                + "                   specified by 'filename' and stores \n" 
                + "                   it in it’s current working directory.\n"
                + "WRITE <filename>   The client writes the file \n" 
                + "                   specified by 'filename' into the \n" 
                + "                   server's appropriate directory.\n"
                + "BYE                Exit from the client after closing \n" 
                + "                   the connection server, if one exists.\n" 
                + "------------------------------------------------------------------ \n");
    }    
    

    /** 
     * Method:  main
     * @param:  String[] args
     * @throws: java.io.IOException
     * return:  void
     * Description: This is the main method of the FTP client
     */
    public static void main(String[] args) throws IOException {

        // create new FTPclient object
        FTPclient client = new FTPclient();
        
        // check for arguments in the client
        client.checkArguments(args);

        // connect client to the server
        client.connectToServer();
            
        // get commands from client and 
        // process the command accordingly
        try {
            // to get input from client 
            // create a BufferedReader object
            BufferedReader commandInput = new BufferedReader(new InputStreamReader(System.in));

            // initializing the commands array 
            // to store client's command
            String[] commands = client.getCommand("init init");

            // run the clinet object as long as 
            // the client did not give BYE command
            while (!commands[0].equalsIgnoreCase("BYE")) {
                
                // Get commands from user
                System.out.print("\nPlease Enter Command : ");
                commands = client.getCommand(commandInput.readLine());

                // if the socket is already closed 
                // from server using QUIT command
                if (client.socket.isClosed()){
                    client.socket.close();  // close the socket
                    System.exit(0);         // close the client application

                    System.out.println("Server is no longer running. "
                            + "\nYour connection has been terminated.");
                    break;  // break out of the while loop
                }
                
                // handle the client commands
                // by calling appropriate method
                // for corresponding command
                switch (commands[0].toUpperCase()) {

                    case "HELP":
                        client.printUsage();
                        break;

                    case "LIST-ALL":
                        client.processListCommand (commands);
                        break;

                    case "LIST-DETAILS":
                        client.processListCommand (commands);
                        break;
                        
                    case "LIST-NAMES":
                        client.processListCommand (commands);
                        break;
                        
                    case "READ":
                        client.processReadCommand (commands);   
                        break;

                    case "WRITE":
                        client.processWriteCommand (commands); 
                        break;

                    case "BYE":
                        
                        break;

                    default:
                        System.out.println("Invalid Command! Please try again!\n");
                        client.printUsage();
                }
            }
            
            // when client gave BYE command 
            // then handle that accordingly
            client.processByeCommand ("BYE"); 
        }
        catch (SocketException Se){
            System.out.println("\n" + Se.toString() + "\n");
            System.out.println("SocketException: Connection got terminated or Server is no longer running.");
            client.socket.close();  // close socket
            System.exit(0);         // close client application
        } 
        catch (IOException ex) {
            System.out.println("\n" + ex.toString() + "\n");
            System.out.println("IOException: Client was FORCED closed");
            
            // call the bye command to
            // manage from server as well
            client.processByeCommand ("BYE");   
        }
    }
}
        