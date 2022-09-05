package models;

public class Book {
	private String name;
	private String author;
	private double price;
	private String href;
	private String sprice;
	
	public Book() {
		super();
	}
	
	
	
	public Book(String name, String author, double price) {
		super();
		this.name = name;
		this.author = author;
		this.price = price;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}



	public String getHref() {
		return href;
	}



	public void setHref(String href) {
		this.href = href;
	}



	public String getSprice() {
		return sprice;
	}



	public void setSprice(String sprice) {
		this.sprice = sprice;
	}



	public Book(String name, String author, double price, String href, String sprice) {
		super();
		this.name = name;
		this.author = author;
		this.price = price;
		this.href = href;
		this.sprice = sprice;
	}
	
	
	
	
	
	
	
}
