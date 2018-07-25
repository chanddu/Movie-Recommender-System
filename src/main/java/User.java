import java.io.Serializable;

public class User implements Serializable {
    private Integer userId;
    private String gender;
    private Integer age;
    private Integer occupation;
    private String zip;

    public User(Integer userId, String gender, Integer age, Integer occupation, String zip) {
        super();
        this.userId = userId;
        this.gender = gender;
        this.age = age;
        this.occupation = occupation;
        this.zip = zip;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getGender() {
        return gender;
    }

    public Integer getAge() {
        return age;
    }

    public Integer getOccupation() {
        return occupation;
    }

    public String getZip() {
        return zip;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", gender=" + gender + ", age=" + age + ", occupation=" + occupation
                + ", zip=" + zip + "]";
    }

}