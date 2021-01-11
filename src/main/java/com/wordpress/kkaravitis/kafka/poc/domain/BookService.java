package com.wordpress.kkaravitis.kafka.poc.domain;

import com.wordpress.kkaravitis.kafka.poc.messaging.MessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final MessagePublisher messagePublisher;

    @Transactional("chainedTransactionManager")
    public void addBook(Book book) {
        messagePublisher.send(book.getTitle());
        bookRepository.save(book);
    }
}
