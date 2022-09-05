package models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

public class Message implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String sender;
	private String reciver;
	private String content;
	private String date;
	private String subject;
	private String id;
	private String realReciver;
		
	public Message() {}
	
	
	public Message(String sender, String reciver, String content) {
		super();
		this.sender = sender;
		this.reciver = reciver;
		this.content = content;
	}

	
	public Message(String sender, String reciver, String content, String date, String subject) {
		super();
		this.sender = sender;
		this.reciver = reciver;
		this.content = content;
		this.date = date;
		this.subject = subject;
	}
	
	


	public Message(String sender, String reciver, String content, String date, String subject, String realReciver) {
		super();
		this.sender = sender;
		this.reciver = reciver;
		this.content = content;
		this.date = date;
		this.subject = subject;
		this.realReciver = realReciver;
	}


	public String getDate() {
		return date;
	}



	public void setDate(String date) {
		this.date = date;
	}


	public String getRealReciver() {
		return realReciver;
	}


	public void setRealReciver(String realReciver) {
		this.realReciver = realReciver;
	}


	public String getSubject() {
		return subject;
	}



	public void setSubject(String subject) {
		this.subject = subject;
	}



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReciver() {
		return reciver;
	}

	public void setReciver(String reciver) {
		this.reciver = reciver;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	@Override
	public String toString()
	{
		return sender+","+reciver+","+content+","+date+","+subject;
	}
}
