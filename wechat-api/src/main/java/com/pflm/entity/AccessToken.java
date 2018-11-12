package com.pflm.entity;

/**
 * @author qinxuewu
 * @version 1.00
 * @time 6/11/2018下午 7:34
 */
public class AccessToken implements Comparable<AccessToken> {

    private String token;
    private int expiresIn;
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public int getExpiresIn() {
        return expiresIn;
    }
    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    @Override
    public String toString() {
        return "AccessToken [token=" + token + ", expiresIn=" + expiresIn + "]";
    }
    //此方法是提供给Collections.sort方法使用的。

    public int compareTo(AccessToken a) {
        // 只能对一个字段做比较，如果做整个对象的比较就实现不了按指定字段排序了。
        return this.getToken().compareTo(a.getToken());
    }
}
