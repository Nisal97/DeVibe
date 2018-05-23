package devibe.com.devibe.Model;

/**
 * Created by nmmut on 2018-04-03.
 */

public class Customer {

    private String userName;
    private String deviceID;
    private String retypePassword;
    private String Password;
    private String Email;

    public Customer(){

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String username) {
        userName = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getRetypePassword() {
        return retypePassword;
    }

    public void setRetypePassword(String retypePassword) {
        this.retypePassword = retypePassword;
    }

    public Customer(String userName, String deviceID, String retypePassword, String password, String email) {
        this.userName = userName;
        this.deviceID = deviceID;
        this.retypePassword = retypePassword;
        Password = password;
        Email = email;
    }
}
