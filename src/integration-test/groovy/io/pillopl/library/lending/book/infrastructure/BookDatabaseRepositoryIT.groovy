package io.pillopl.library.lending.book.infrastructure


import io.pillopl.library.lending.book.model.AvailableBook
import io.pillopl.library.lending.book.model.Book
import io.pillopl.library.lending.book.model.BookId
import io.pillopl.library.lending.library.model.LibraryBranchId
import io.pillopl.library.lending.patron.model.PatronId
import io.vavr.control.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static io.pillopl.library.lending.book.model.BookFixture.anyBookId
import static io.pillopl.library.lending.book.model.BookFixture.circulatingAvailableBookAt
import static io.pillopl.library.lending.library.model.LibraryBranchFixture.anyBranch
import static io.pillopl.library.lending.patron.model.PatronBooksFixture.anyPatronId

@ContextConfiguration(classes = BookConfiguration.class)
@SpringBootTest
class BookDatabaseRepositoryIT extends Specification {

    BookId bookId = anyBookId()
    LibraryBranchId libraryBranchId = anyBranch()
    PatronId patronId = anyPatronId()

    @Autowired
    BookDatabaseRepository bookEntityRepository

    def 'persistence in real database should work'() {
        given:
            AvailableBook availableBook = circulatingAvailableBookAt(bookId, libraryBranchId)
        when:
            bookEntityRepository.save(availableBook)
        then:
            bookIsPersistedAs(AvailableBook.class)
    }

    void bookIsPersistedAs(Class<?> clz) {
        Book book = loadPersistedBook(bookId)
        assert book.class == clz
    }

    Book loadPersistedBook(BookId bookId) {
        Option<Book> loaded = bookEntityRepository.findBy(bookId)
        Book book = loaded.getOrElseThrow({
            new IllegalStateException("should have been persisted")
        })
        return book
    }
}
