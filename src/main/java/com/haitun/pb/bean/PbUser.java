package com.haitun.pb.bean;

// Generated 2015-6-6 16:16:14 by Hibernate Tools 3.4.0.CR1

/**
 * PbUser generated by hbm2java
 */
public class PbUser implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String UPhone;
	private String UPassword;
	private String USex;
	private String URegion;
	private String UCoordinate;
	private Integer UIntegral;
	private String UName;

	public PbUser() {
	}

    public PbUser(String UPhone, String UPassword, String USex, String URegion, String UCoordinate, Integer UIntegral, String UName) {
        this.UPhone = UPhone;
        this.UPassword = UPassword;
        this.USex = USex;
        this.URegion = URegion;
        this.UCoordinate = UCoordinate;
        this.UIntegral = UIntegral;
        this.UName = UName;
    }


    public String getUPhone() {
        return UPhone;
    }

    public void setUPhone(String UPhone) {
        this.UPhone = UPhone;
    }

    public String getUPassword() {
        return UPassword;
    }

    public void setUPassword(String UPassword) {
        this.UPassword = UPassword;
    }

    public String getUSex() {
        return USex;
    }

    public void setUSex(String USex) {
        this.USex = USex;
    }

    public String getURegion() {
        return URegion;
    }

    public void setURegion(String URegion) {
        this.URegion = URegion;
    }

    public String getUCoordinate() {
        return UCoordinate;
    }

    public void setUCoordinate(String UCoordinate) {
        this.UCoordinate = UCoordinate;
    }

    public Integer getUIntegral() {
        return UIntegral;
    }

    public void setUIntegral(Integer UIntegral) {
        this.UIntegral = UIntegral;
    }

    public String getUName() {
        return UName;
    }

    public void setUName(String UName) {
        this.UName = UName;
    }
}