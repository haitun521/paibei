package com.haitun.pb.bean;

// Generated 2015-6-6 16:16:14 by Hibernate Tools 3.4.0.CR1

/**
 * PbComment generated by hbm2java
 */
public class PbComment implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer CId;
	private String Aname ;
	private String Bname;
	private String CDate;
	private String CContent;

	public PbComment() {
	}

    public PbComment(String CContent, String aname, String bname, String CDate, Integer CId) {
        this.CContent = CContent;
        Aname = aname;
        Bname = bname;
        this.CDate = CDate;
        this.CId = CId;
    }

    public String getCContent() {
        return CContent;
    }

    public void setCContent(String CContent) {
        this.CContent = CContent;
    }

    public String getCDate() {
        return CDate;
    }

    public void setCDate(String CDate) {
        this.CDate = CDate;
    }

    public String getBname() {
        return Bname;
    }

    public void setBname(String bname) {
        Bname = bname;
    }

    public String getAname() {
        return Aname;
    }

    public void setAname(String aname) {
        Aname = aname;
    }

    public Integer getCId() {
        return CId;
    }

    public void setCId(Integer CId) {
        this.CId = CId;
    }
}
