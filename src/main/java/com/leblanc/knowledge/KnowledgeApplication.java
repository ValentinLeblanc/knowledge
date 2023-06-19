package com.leblanc.knowledge;

public class KnowledgeApplication {

	public static void main(String[] args) {
        BookStore bookStore = new BookStore();
        Customer customer1 = new Customer("John");
        Customer customer2 = new Customer("Emily");

        bookStore.registerObserver(customer1);
        bookStore.registerObserver(customer2);

        Book book1 = new Book("Java Programming");
        Book book2 = new Book("Design Patterns");

        bookStore.addBook(book1); // Notifies both customers
        bookStore.addBook(book2); // Notifies both customers

        bookStore.removeBook(book1); // Notifies both customers
	}
}
