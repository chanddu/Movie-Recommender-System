import java.io.Serializable;

public class UserProductTuple implements Serializable {

    private Integer userId;
    private Integer productId;

    public UserProductTuple(Integer userId, Integer productId) {
        super();
        this.userId = userId;
        this.productId = productId;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getProductId() {
        return productId;
    }


    @Override
    public String toString() {
        return "UserProductTuple [userId=" + userId + ", productId=" + productId + "]";
    }



}