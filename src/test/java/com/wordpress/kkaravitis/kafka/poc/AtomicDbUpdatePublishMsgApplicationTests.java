package com.wordpress.kkaravitis.kafka.poc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.wordpress.kkaravitis.kafka.poc.domain.Book;
import com.wordpress.kkaravitis.kafka.poc.domain.BookRepository;
import com.wordpress.kkaravitis.kafka.poc.domain.BookService;
import com.wordpress.kkaravitis.kafka.poc.messaging.MessageSubscriber;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

@DirtiesContext
@EmbeddedKafka(
	brokerProperties = "auto.create.topics.enable=true",
	count = 3,
	ports = {0, 0, 0}
)
@TestPropertySource(properties = {
	"spring.datasource.url=jdbc:h2:mem:testdb",
	"spring.datasource.driverClassName=org.h2.Driver",
	"spring.datasource.username=sa",
	"spring.datasource.password=password",
	"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
	"spring.jpa.hibernate.ddl-auto=create",

	"spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
	"test.topic = test-topic",
// Consumer reads only transaction committed messages
	"spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
	"spring.kafka.consumer.auto-offset-reset=earliest",
	"spring.kafka.consumer.group-id=poc",
	"spring.kafka.consumer.isolation-level=read_committed",
	"spring.kafka.consumer.enable-auto-commit=false",
// Producer uses a transactional id
	"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
	"spring.kafka.producer.properties[transactional.id]=tx-proof-of-concept",
//
	"spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}",
	"logging.level.root=DEBUG"
})
@SpringBootTest
class AtomicDbUpdatePublishMsgApplicationTests {

	@Autowired
	BookService bookService;
	@Autowired
	BookRepository bookRepository;
	@Autowired
	MessageSubscriber messageSubscriber;

	@Test
	void testHappyPath() throws Throwable {
		// given
		String title = "Microservices Patterns";

		// when

		bookService.addBook(new Book(1L, title));
		messageSubscriber.getLatch().await(2, TimeUnit.SECONDS);

		// then
		assertEquals(title, bookRepository.findAll().get(0).getTitle());
		assertEquals(title, messageSubscriber.getPayload());
	}

	@Test
	void givenAdbTransactionFailure_TheMessageShouldNotBeSent() throws Exception {
		// given
		String title = "POJOs In Action";

		// when
		try {
			bookService.addBook(new Book(null, title)); // make the db transaction to fail
		} catch (Exception exception) {
			System.out.println(12);
		}

		messageSubscriber.getLatch().await(3, TimeUnit.SECONDS);
		assertNull(messageSubscriber.getPayload());
		//Actually the message is sent but consumer waits the kafka transaction to commit to receive this message.
		// If we have a non transactional consumer the message would be received although the db transaction rolled back.
	}
}
