package com.leblanc.knowledge;

import java.util.List;

public interface BookObserver {
    void update(List<Book> books);
}
