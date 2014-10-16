package ftpserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 *
 * @author Syed Anjoom Iqbal
 * COMP ENG 4DN4 - 2014
 * Lab 2 - McNapster
 * McMaster University, Hamilton, ON, Canada
 * 
 * Class Name:  FileSystem
 * Description: This is the class to handle the file system in FTP server.
 */
public class FileSystem {
	
    /**************************************************************
     *          FIELDS OF FileSystem class
     *************************************************************/
    private String directory="";

    
    /**************************************************************
     *          METHODS OF FileSystem class
     *************************************************************/    
    
    
    /** 
     * Method:  FileSystem
     * @param:  String dir
     * @throws: <>
     * Description: This is the constructor of this class
     */
    public FileSystem(String dir) {
        this.directory = dir;
    }

        
    /** 
     * Method:  getDirectory
     * @param:  <>
     * @throws: <>
     * return:  String
     * Description: This is the getter of field 'directory'
     */
    public String getDirectory() {
        return directory;
    }
    
        
    /** 
     * Method:  setDirectory
     * @param:  String directory
     * @throws: <>
     * return:  void
     * Description: This is the setter of field 'directory'
     */
    public void setDirectory(String directory) {
        this.directory = directory;
    }

        
    /** 
     * Method:  getFileSize
     * @param:  String fileName
     * @throws: java.io.IOException
     * return:  (int) file size in bytes
     * Description: This method gets the size of the file (in bytes) 
     *              with name 'fileName' in the server directory
     */
    public int getFileSize(String fileName) throws IOException {
        FileInputStream fileByteStream = null;
        int fileSizeInBytes = 0;
        try {
            // create the complete file path
            String filePath = directory + fileName;
            
            // create a new FileInputStream object 'fileByteStream' with the file
            fileByteStream = new FileInputStream(new File(filePath));
            
            // read the fileByteStream object byte by byte 
            // and increament the fileSizeInBytes local field 
            // which will be returned
            while ((fileByteStream.read()) != -1) {
                fileSizeInBytes++;
            }
        } 
        // catch any IO exception
        catch (IOException e) {
            System.out.println("\n" + e.toString() + "\n");
        }

        // close the file if it was successfuly opened
        if (fileByteStream != null) {
            fileByteStream.close();
        }

        return fileSizeInBytes;
    }
    
            
    /** 
     * Method:  readDirectoryDetails
     * @param:  <>
     * @throws: java.io.IOException
     * return:  String
     * Description: This method reads the files in the 'directory' and
     *              makes a detailed list with file-names and file-sizes of each file.
     *              It returns a string with info of each file seperated by new line.
     */
    public String readDirectoryDetails() throws IOException {
        ArrayList<String> fileList = new ArrayList<String>();
        
        // create a file array with the files in the server directory
        File[] fileArray = new File(directory).listFiles();
        
        // populate the arraylist with file name and size
        for (int i = 0; i < fileArray.length; i++){
            if (fileArray[i].isFile()) {
                String s = fileArray[i].getName() 
                            + "\t\t"
                            + getFileSize(fileArray[i].getName().toString()) 
                            + "\t Bytes";
                fileList.add(s);
            }
        }
        // convert the arrayList to a nice string 
        // with info of each file seperated by new line
        String s = fileList.toString();
        s = s + ", ";
        s = s.replace(", ", "\n");
        s = s.replace("[", "");
        s = s.replace("]", "");
        s = s.trim();
        
        return s;
    }    
    
                
    /** 
     * Method:  readDirectoryList
     * @param:  <>
     * @throws: java.io.IOException
     * return:  ArrayList fileList
     * Description: This method reads the files in the 'directory' and
     *              makes a quick list with only file-names of each file.
     *              It returns an ArrayList<String> with names of each file.
     */
    public ArrayList readDirectoryList() throws IOException {
        ArrayList<String> fileList = new ArrayList<String>();
        
        // create a file array with the files in the server directory
        File[] fileArray = new File(directory).listFiles();
        
        // populate the arraylist with file names only
        for (int i = 0; i < fileArray.length; i++){
            if (fileArray[i].isFile()) {
                String s = fileArray[i].getName(); // get file name
                fileList.add(s);    // add the file name to the list
            }
        }
        
        return fileList;
    }
    
} // end of class
