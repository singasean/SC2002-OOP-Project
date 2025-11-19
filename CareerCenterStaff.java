public class CareerCenterStaff extends User {

    public CareerCenterStaff(String userID, String name) {
        super(userID, name);
    }

    @Override
    public String getRole() {
        return "Career Center Staff";
    }
}