package com.leblanc.knowledge;

interface Subject {
    void registerObserver(BookObserver observer);
    void removeObserver(BookObserver observer);
    void notifyObservers();
}