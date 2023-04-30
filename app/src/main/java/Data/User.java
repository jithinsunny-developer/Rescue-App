package Data;

public class User {
    private String userName;
    private String dob;
    private String gender;
    private String bloodGrp;
    private String phone;
    private String emergencyNum;
    private String medicalHistory;
    private String pwd;
    private String emai;

    public User() {
    }


    public User(String userName, String dob, String gender, String bloodGrp, String phone, String emergencyNum, String medicalHistory, String pwd, String emai) {
        this.userName = userName;
        this.dob = dob;
        this.gender = gender;
        this.bloodGrp = bloodGrp;
        this.phone = phone;
        this.emergencyNum = emergencyNum;
        this.medicalHistory = medicalHistory;
        this.pwd = pwd;
        this.emai = emai;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBloodGrp() {
        return bloodGrp;
    }

    public void setBloodGrp(String bloodGrp) {
        this.bloodGrp = bloodGrp;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmergencyNum() {
        return emergencyNum;
    }

    public void setEmergencyNum(String emergencyNum) {
        this.emergencyNum = emergencyNum;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getEmai() {
        return emai;
    }

    public void setEmai(String emai) {
        this.emai = emai;
    }
}
