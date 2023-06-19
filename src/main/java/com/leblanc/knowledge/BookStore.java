package com.leblanc.knowledge;

import java.util.ArrayList;
import java.util.List;

class BookStore implements Subject {
    private List<Book> books;
    private List<BookObserver> observers;

    public BookStore() {
        books = new ArrayList<>();
        observers = new ArrayList<>();
    }

    public void addBook(Book book) {
        books.add(book);
        notifyObservers();
    }

    public void removeBook(Book book) {
        books.remove(book);
        notifyObservers();
    }

    @Override
    public void registerObserver(BookObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(BookObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (BookObserver observer : observers) {
            observer.update(books);
        }
    }
}
