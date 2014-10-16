package smarthomeserver;

/**
 *
 * @author Syed Anjoom Iqbal
 * 
 * COMP ENG 4DN4 - 2014
 * McMaster University
 * Hamilton, ON, Canada
 * 
 * Class Name:  Device
 * Description: This is the class to make a simple model of 
 *              all the connected devices in Internet-of-Everything (IoE). 
 */
class Device {

    /**************************************************************
     *          PRIVATE FIELDS of Device class
     *************************************************************/
    private String DeviceName;
    private String RdValue;
    private String WrValue;


    
    /**************************************************************
     *          CONSTRUCTORs of Device class
     *************************************************************/
    
    /** 
     * Constructor:  Device
     * @param:  String DeviceName, String RdValue, String WrValue
     * @throws: <>
     * return:  <>
     * Description: Creates a Device object
     */
    Device(String DeviceName, String RdValue, String WrValue) {
        this.DeviceName = DeviceName;
        this.RdValue = RdValue;
        this.WrValue = WrValue;
    }

    /** 
     * Constructor:  Device
     * @param:  String DeviceName
     * @throws: <>
     * return:  <>
     * Description: Creates a Device object
     */
    Device(String DeviceName) {
        this.DeviceName = DeviceName;
        this.RdValue = "Not Initialized";
        this.WrValue = "Not Initialized";
    }
    
    
    /**************************************************************
     *          Get & Set Methods of Device class
     *************************************************************/
    
   /** 
     * Method:  getDeviceName
     * @param:  <>
     * @throws: <>
     * return:  Device.DeviceName
     * Description: returns the DeviceName of the Device object
     */
    String getDeviceName() {
        return DeviceName;
    }
      
    
   /** 
     * Method:  getRdValue
     * @param:  <>
     * @throws: <>
     * return:  Device.RdValue
     * Description: returns the RdValue of the Device object
     */
    String getRdValue() {
        return RdValue;
    }

            
   /** 
     * Method:  setRdValue
     * @param:  String RdValue
     * @throws: <>
     * return:  void
     * Description: Sets the RdValue of the Device object to RdValue in parameter
     */
    void setRdValue(String RdValue) {
        this.RdValue = RdValue;
    }
    
            
   /** 
     * Method:  setWrValue
     * @param:  String WrValue
     * @throws: <>
     * return:  void
     * Description: Sets the WrValue of the Device object to WrValue in parameter
     */
    void setWrValue(String WrValue) {
        this.WrValue = WrValue;
    }
    
    
    
    public String getWrValue() {
        return WrValue;
    }

}
