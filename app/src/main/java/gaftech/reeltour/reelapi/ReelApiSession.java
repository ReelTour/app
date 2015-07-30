package gaftech.reeltour.reelapi;

/**
 * Created by r.suleymanov on 24.06.2015.
 * email: ruslancer@gmail.com
 */

public class ReelApiSession {
    private String token;
    private String membership;
    private Integer id;
    public void setToken(String token) {
        this.token = token;
    }
    public void setMembership(String membership) { this.membership = membership; }
    public void setId(int id) { this.id = id; }

    public String getToken() {
        return this.token;
    }
    public String getMembership() {return this.membership; }
    public Integer getId() { return this.id; }
 }
