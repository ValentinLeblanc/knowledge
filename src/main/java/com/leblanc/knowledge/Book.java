package com.leblanc.knowledge;

public class Book {

	private String title;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Book(String title) {
		super();
		this.title = title;
	}
	@Override
	public String toString() {
		return "Book [title=" + title + "]";
	}
	
}
