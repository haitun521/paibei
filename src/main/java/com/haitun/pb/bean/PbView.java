package com.haitun.pb.bean;

// Generated 2015-6-6 16:16:14 by Hibernate Tools 3.4.0.CR1

/**
 * PbView generated by hbm2java
 */
public class PbView implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String VWord;
	private String VUrl;
	private String VScdate;
	private String VOk;
	private String VUnok;
	private String VArea;
    private String VId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

	public PbView() {
	}

    public PbView(String VWord, String VId, String VArea, String VUnok, String VScdate, String VOk, String VUrl) {
        this.VWord = VWord;
        this.VId = VId;
        this.VArea = VArea;
        this.VUnok = VUnok;
        this.VScdate = VScdate;
        this.VOk = VOk;
        this.VUrl = VUrl;

    }

    public String getVWord() {
        return VWord;
    }

    public void setVWord(String VWord) {
        this.VWord = VWord;
    }

    public String getVUrl() {
        return VUrl;
    }

    public void setVUrl(String VUrl) {
        this.VUrl = VUrl;
    }

    public String getVScdate() {
        return VScdate;
    }

    public void setVScdate(String VScdate) {
        this.VScdate = VScdate;
    }

    public String getVOk() {
        return VOk;
    }

    public void setVOk(String VOk) {
        this.VOk = VOk;
    }

    public String getVUnok() {
        return VUnok;
    }

    public void setVUnok(String VUnok) {
        this.VUnok = VUnok;
    }

    public String getVArea() {
        return VArea;
    }

    public void setVArea(String VArea) {
        this.VArea = VArea;
    }

    public String getVId() {
        return VId;
    }

    public void setVId(String VId) {
        this.VId = VId;
    }
}
