package ftpserver;

import java.net.*;   // for Socket
import java.io.*;    // for IOException and Input/OutputStream
import java.util.*;
import java.util.logging.Level;

/**
 *
 * @author Syed Anjoom Iqbal
 * COMP ENG 4DN4 - 2014
 * Lab 2 - McNapster
 * McMaster University, Hamilton, ON, Canada
 * 
 * Class Name:  FTPserverProtocol extends Thread
 * Description: This is the thread class to handle commands from FTP client
 */
public class FTPserverProtocol extends Thread {
    
    /**************************************************************
     *          FIELDS OF FTPserverProtocol class
     *************************************************************/
    private static final int    BUFSIZE  = 1024;   // I/O buffer size in bytes
    private static final String SERV_DIR = 
         "C:\\Users\\Anjoom\\Desktop\\server\\pub\\MUSIC\\".replace("\\", "/");
    
    private Socket       clntSock;  // Connection socket
    private InputStream  servIn;    // server InputStream
    private OutputStream servOut;   // server OutputStream
    
    boolean fileTransferInProgress; // flag for traching busy status
    private static FileSystem servFileSystem;   // FileSystem object of server
    
    
    /**************************************************************
     *          METHODS OF FTPserverProtocol class
     *************************************************************/    
    
    /** 
     * Method:  FTPserverProtocol
     * @param:  ServerSocket servSock
     * @throws: java.io.IOException
     * Description: This is the constructor of this class
     */
    public FTPserverProtocol(ServerSocket servSock) throws IOException {
        this.clntSock = servSock.accept();      // blocking accept method in server socket
        this.fileTransferInProgress = false;    
    }
  
        
    /** 
     * Method:  closeSocket
     * @param:  <>
     * @throws: java.io.IOException
    * return:  void
     * Description: This method closes the server socket properly
     */
    void closeSocket () throws IOException{
        if (this.clntSock.isConnected()){
            this.servIn.close();    // close server InputStream object
            this.servOut.close();   // close server OutStream object
            this.clntSock.close();  // close client socket
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
     * @param:  String s
     * @throws: java.io.IOException
     * return:  void
     * Description: This method sends the string s to the connected client 
     */
    private void SendClientString(String s) throws IOException{
        servOut.write(s.getBytes());
    }
    
              
    /** 
     * Method:  handleListDetailsCommand
     * @param:  FileSystem servFileSystem
     * @throws: java.io.IOException
     * return:  void
     * Description: This method handles the LIST-DETAILS 
     *              & LIST-ALL commands from server side
     */
    private void handleListDetailsCommand (FileSystem servFileSystem) throws IOException{
        
        System.out.println("-->");
        System.out.println("handleListDetailsCommand START");
        
        // call the readDirectoryDetails of 
        // the FileSystem object and 
        // get a String with the detailed 
        // information of the files 
        // in the server directory
        String detailedFileList = servFileSystem.readDirectoryDetails();
        
        // send the list of files
        SendClientString(detailedFileList);

        System.out.println("handleListDetailsCommand END");
        System.out.println("<--");
    }
    
                  
    /** 
     * Method:  handleListAllCommand
     * @param:  FileSystem servFileSystem
     * @throws: java.io.IOException
     * return:  void
     * Description: This method handles the LIST-NAMES command from server side
     */
    private void handleListNamesCommand (FileSystem servFileSystem) throws IOException{
        
        System.out.println("-->");
        System.out.println("handleListNamesCommand START");
        
        // call the readDirectoryList of 
        // the FileSystem object and 
        // convert the returned ArrayList
        // to string by calling the 
        // toString method of ArrayList class
        // and get a String with only the 
        // names of the files 
        // in the server directory
        String FileNameList = servFileSystem.readDirectoryList().toString();
        
        // process the string return by the 
        // toString method of ArrayList class
        // to get the file names per line
        FileNameList = FileNameList + ", ";
        FileNameList = FileNameList.replace(", ", "\n");
        FileNameList = FileNameList.replace("[", "");
        FileNameList = FileNameList.replace("]", "");
        FileNameList = FileNameList.trim();
        
        
        // send the list of files to client
        SendClientString(FileNameList);

        System.out.println("handleListNamesCommand END");
        System.out.println("<--");
    }
    
     
    /** 
     * Method:  handleReadCommand
     * @param:  FileSystem servFileSystem
     * @throws: java.io.IOException
     * return:  void
     * Description: This method handles the 
     *              READ command from server side
     */
    private void handleReadCommand (FileSystem servFileSystem) throws IOException{
        
        System.out.println("-->");
        System.out.println("handleReadCommand START");
        
        // call the readDirectoryList of 
        // the FileSystem object 
        // and get the file names in
        // the server directory 
        // in an ArrayList        
        ArrayList fileList = servFileSystem.readDirectoryList();
                
        // get the name of the file to be read from client
        byte[] byteBuffer = new byte[BUFSIZE]; 
        servIn.read(byteBuffer);
        String rdFile = new String(byteBuffer);
        rdFile=rdFile.trim();
        
        // print the file name of that has 
        // been requested to be read by the client
        System.out.println("rdFile = '" + rdFile + "'");
        
        // if the requested file exists 
        // in the server directory
        if (fileList.contains(rdFile)){        
            // get the file size by calling
            // the getFileSize method 
            // of the FileSystem object
            int rdFileSize = servFileSystem.getFileSize(rdFile);
            
            // Send file size to client
            SendClientString(Integer.toString(rdFileSize));  

            // open the file reqested to be sent over socket
            // initialize book keeping fields
            FileInputStream rdFileStream = null;    
            String servFilePath = SERV_DIR + rdFile;
            rdFileStream = new FileInputStream(servFilePath);        
            int rdByteCount = 0;
            int totalRdByteCount = 0;
            byteBuffer = new byte[BUFSIZE]; 
            
            // Keep sending chunks of the 
            // requested file to the client
            // till the end of the file
            while((rdByteCount = rdFileStream.read(byteBuffer)) != -1){
                // Keep sending chunks 
                // till the last chunk
                if (rdByteCount >= BUFSIZE){
                    servOut.write(byteBuffer);      // send the data to the client
                    byteBuffer = new byte[BUFSIZE]; // re initialize the byteBuffer
                    totalRdByteCount += rdByteCount; // increment the total bytes read
                }
                
                // sending the last chunk
                else {
                    // create a byte array with
                    // size equal to last chunk 
                    // which is not equal 
                    // to the total BUFFSIZE
                    byte[] fileByteBuffer = new byte[rdByteCount]; 
                    
                    // copy the last chuck 
                    // (which is less than BUFFSIZE)
                    // to another buffer array
                    for (int i = 0; i < rdByteCount; i++){
                        fileByteBuffer[i] = byteBuffer[i];
                    }
                    servOut.write(fileByteBuffer);  // send the data to the client
                    totalRdByteCount += rdByteCount;
                }
            }

            // print the total 
            // number of bytes read
            System.out.println("totalRdByteCount = " + totalRdByteCount);

            // close the file if 
            // it was successfully opened
            if (rdFileStream != null) {
                rdFileStream.close(); 
            }
        }
        
        // if the requested file exists 
        // in the server directory
        else {
            SendClientString("NO SUCH FILE"); 
        }
        
        System.out.println("handleReadCommand END");
        System.out.println("<--");
    }
    

    /** 
     * Method:  handleWriteCommand
     * @param:  <>
     * @throws: java.io.IOException
     * return:  void
     * Description: This method handles the WRITE command from server side
     */
    private void handleWriteCommand () throws IOException{
        
        // initialize File and FileOutputStream fields
        File wrFileToServ = null;
        FileOutputStream wrFileStream = null;
        
        try {
           
            this.fileTransferInProgress = true;
           
            System.out.println("-->");
            System.out.println("handleWriteCommand START");

            // get the name of the file 
            // to be written from the client
            // and print it in the server terminal
            byte[] byteBuffer = new byte[BUFSIZE]; 
            servIn.read(byteBuffer);
            String wrFileName = new String(byteBuffer);
            wrFileName = wrFileName.trim();
            System.out.println("received wrFileName: '" + wrFileName + "'");

            // get the file size in 
            // total Number Of Bytes from client
            // and print it in the server terminal
            byteBuffer = new byte[BUFSIZE]; 
            servIn.read(byteBuffer);
            String s = new String(byteBuffer);
            s=s.trim();
            int totalNumberOfBytesToWrite = Integer.parseInt(s);
            System.out.println("totalNumberOfBytesInFile: " + totalNumberOfBytesToWrite);

            // compile the file name with the path
            System.out.println("Writing....");
            String newFilePath = SERV_DIR+wrFileName;
            System.out.println("newFilePath = '" + newFilePath + "'");
            
            // open a new file with the 
            // file name received from the client
            wrFileToServ = new File(newFilePath);
            wrFileStream = null;
            int bytesReceived = BUFSIZE;
            int totalBytesWritten = 0;
            
            // if the file does not exist 
            // then create a new file
            if(!wrFileToServ.exists()){
                wrFileToServ.createNewFile();
            }

            // create a FileOutputStream with the file
            wrFileStream = new FileOutputStream(newFilePath);
            
            // write the received data in socket 
            // to the file in the server directory
            while ( bytesReceived >= BUFSIZE){
                byteBuffer = new byte[BUFSIZE]; 
                bytesReceived = servIn.read(byteBuffer);
                wrFileStream.write(byteBuffer);
                totalBytesWritten += bytesReceived;
            }


            System.out.println("totalBytesWritten: " + totalBytesWritten);

            // close the open FileOutputStream object
            wrFileStream.close();
            System.out.println("Closed new file");

            System.out.println("handleWriteCommand END");
            System.out.println("<--");
            
            this.fileTransferInProgress = false;
            
       }
       catch (SocketException e){
            System.out.println("\n" + e.toString() + "\n");
            
            // catch exception when socket 
            // gets closed before completing 
            // file transfer
            if (this.fileTransferInProgress == true){
                System.out.println("Connection terminated before writing the total file to server");
                System.out.println("Removing broken file from Server pub...");
            }
        }
    }


    /** 
     * Method:  handleByeCommand
     * @param:  <>
     * @throws: java.io.IOException
     * return:  void
     * Description: This method handles the BYE command from server side
     */
    private void handleByeCommand () throws IOException{
        System.out.println("-->");
        System.out.println("handleByeCommand START");
        
        System.out.println(Thread.currentThread().getName() + " is closing ...");
        System.out.println("BEFORE: Connected Client Count = " + FTPserver.connectedClientCount);
        
        // decrement connect client count 
        FTPserver.connectedClientCount--;
        
        // close the client socket
        this.closeSocket();
        
        System.out.println("AFTER:  Connected Client Count = " + FTPserver.connectedClientCount);
        System.out.println("THREAD CLOSE: SUCCESSFUL");
        
        System.out.println("handleByeCommand END");
        System.out.println("<--");        
    }
    

    /** 
     * Method:  run
     * @param:  <>
     * @throws: <>
     * return:  void
     * Description: This is the run method of the thread. 
     *              When this thread object is created this method is executed.
     */
    public void run() {
        
        // when a client gets connected to the server
        System.out.println("Client address          = " + clntSock.getInetAddress().getHostAddress());
        System.out.println("Client port             = " + clntSock.getPort());
        System.out.println("Thread ID               = " + Thread.currentThread().getName());
        System.out.println("Connected Client Count  = " + Integer.toString(FTPserver.connectedClientCount));
        
        try{
            // Get the input and output I/O streams from socket
            servIn  = clntSock.getInputStream();
            servOut = clntSock.getOutputStream();
            
            // create an object of FileSystem class 
            // with the server directory
            servFileSystem = new FileSystem(SERV_DIR);
            String[] commands = getCommand("init init");

            System.out.println("File System Directory   = " + servFileSystem.getDirectory());
            System.out.println("\n-----------------------------------------\n");
            
            // Run the thread in the server 
            // untill the client sends a BYE command
            while (!commands[0].equalsIgnoreCase("BYE")){
                
                // if server is running
                if (HandleQuit.isServerRunning() == true){
                    
                    // getting the command from 
                    // the received data from client
                    commands[0] = "";
                    byte[] byteBuffer = new byte[BUFSIZE];
                    servIn.read(byteBuffer);
                    commands = getCommand(new String(byteBuffer));       

                    System.out.println(Thread.currentThread().getName() + " : "
                            + "command = " + commands[0].toUpperCase() 
                            + "\t Connected Client Count  = " +  FTPserver.connectedClientCount);
                    
                    // according to the command from the cleint
                    // call apprropriate method to handle that action
                    switch (commands[0].toUpperCase()) {

                        case "LIST-ALL":
                            handleListDetailsCommand (servFileSystem);
                            break;

                        case "LIST-DETAILS":
                            handleListDetailsCommand (servFileSystem);
                            break;

                        case "LIST-NAMES":
                            handleListNamesCommand (servFileSystem);
                            break;

                        case "READ":
                            handleReadCommand (servFileSystem);
                            break;

                        case "WRITE":
                            handleWriteCommand ();
                            break;

                        case "BYE":
                            handleByeCommand ();
                            break;

                        default:
                            System.out.println("Invalid Command from client : "
                                    + Thread.currentThread().getName());
                    }

                    // clearing buffer
                    byteBuffer = new byte[BUFSIZE];
                }
                // if server is not running 
                // or the server has been closed
                // by the QUIT command from 
                // server terminal
                else {
                    // close the socket safely
                    clntSock.close();   
                    
                    // decrement the connectedClientCount
                    FTPserver.connectedClientCount--;
                }
            } // end of while loop
        }
        catch (SocketException e){
            // print the exact error
            System.out.println("\n" + e.toString() + "\n");
                
            try {
                FTPserver.connectedClientCount--;
                System.out.println("SocketException: \n"
                        + "Connected Client Count  = " +  FTPserver.connectedClientCount
                        + "\t" + Thread.currentThread().getName() + " : "
                        + "Socket has been closed due to termination of client");
                clntSock.close();
            } 
            catch (IOException ex) {
                System.out.println("\n" + ex.toString() + "\n");
                java.util.logging.Logger.getLogger(FTPserverProtocol.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
        catch (IOException e) {
            System.out.println("\n" + e.toString() + "\n");
            java.util.logging.Logger.getLogger(FTPserverProtocol.class.getName()).log(Level.SEVERE, null, e);
            System.out.println("IOException: \n"
                    + "Connected Client Count  = " +  FTPserver.connectedClientCount
                    + "\t" + Thread.currentThread().getName() + " : " + "IOException" );
        }
    }
}
