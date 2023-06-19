package com.leblanc.knowledge;

import java.util.List;

class Customer implements BookObserver {
    private String name;

    public Customer(String name) {
        this.name = name;
    }

    @Override
    public void update(List<Book> books) {
        System.out.println(name + ": Book inventory updated. Total books available: " + books.size());
    }
}
