public class User{
    private String userID;
    private String name;
    private String password;
    private boolean isLoggedIn;

    public User(String userID, String name, String password) {
        this.userID = userID;
        this.name = name;
        this.password = password;
        this.isLoggedIn = false;
    }
    
    public boolean login(String inputUserID, String inputPassword) {
        if (userID.equals(inputUserID) && password.equals(inputPassword)) {
            isLoggedIn = true;
            System.out.println("Login successful! Welcome, " + name);
        return true;
        } else {
            System.out.println("Login failed! Invalid credentials.");
            return false;
        }
    }

    public void logout() {
        if (isLoggedIn) {
            isLoggedIn = false;
            System.out.println("Logout successful! Goodbye, " + name);
        } else {
            System.out.println("No user is currently logged in.");
        }
    }

    public boolean changePassword(String oldPassword, String newPassword) {
        if (!isLoggedIn) {
            System.out.println("Please login first to change password.");
            return false;
        }

        if (password.equals(oldPassword)) {
            password = newPassword;
            System.out.println("Password changed successfully!");
            return true;
        } else {
            System.out.println("Password change failed! Old password is incorrect.");
            return false;
        }
    }

    public String getUserID(){
        return userID;
    }

    public String getName(){
        return name;
    }

    public String getPassword(){
        return password;
    }
    public void setUserID(String ID){
        userID = ID;
    }

    public void setName(String Name){
        name = Name;
    }

    public void setPassword(String Password){
        password = Password;
    }
}
