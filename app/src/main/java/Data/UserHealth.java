package Data;

public class UserHealth {

    private String userEmail;
    private String userHeight;
    private String userWeight;

    public UserHealth() {
    }

    public UserHealth(String userEmail, String userHeight, String userWeight) {
        this.userEmail = userEmail;
        this.userHeight = userHeight;
        this.userWeight = userWeight;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserHeight() {
        return userHeight;
    }

    public void setUserHeight(String userHeight) {
        this.userHeight = userHeight;
    }

    public String getUserWeight() {
        return userWeight;
    }

    public void setUserWeight(String userWeight) {
        this.userWeight = userWeight;
    }
}
