package models;

import java.io.Serializable;


public class Host implements Serializable{
	
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String masterAlias;
	private String alias;
	private String address;
	
	public Host() {}

	public Host(String masterAlias, String alias, String address) {
		super();
		this.masterAlias = masterAlias;
		this.alias = alias;
		this.address = address;
	}

	public String getMasterAlias() {
		return masterAlias;
	}

	public void setMasterAlias(String masterAlias) {
		this.masterAlias = masterAlias;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	};
	
	
	
}
