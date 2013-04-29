package com.jzapata.mapper;

import com.joanzapata.mapper.Mapper;
import com.joanzapata.mapper.PropertyNotFoundException;
import com.jzapata.mapper.model.Book;
import com.jzapata.mapper.model.BookDTO;
import com.jzapata.mapper.model.BookEntry;
import com.jzapata.mapper.model.BookEntryDTO;
import com.jzapata.mapper.model.entry.AddressEntry;
import com.jzapata.mapper.model.entry.AddressEntryDTO;
import com.jzapata.mapper.model.entry.PhoneEntry;
import com.jzapata.mapper.model.entry.PhoneEntryDTO;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class MapperTest {

    @Test
    public void singleObject() {
        Mapper mapper = new Mapper();
        Book a = new Book(5L, "Book");
        BookDTO animalDTO = mapper.map(a, BookDTO.class);
        Assert.assertEquals(a.getId(), animalDTO.getId());
        Assert.assertEquals(a.getName(), animalDTO.getName());
    }

    @Test
    public void objectWithCyclicDependencies() {

        Book book = new Book(0L, "Book");
        BookEntry entry1 = new BookEntry(1, book);
        BookEntry entry2 = new BookEntry(2, book);
        book.setEntries(Arrays.asList(entry1, entry2));

        Mapper mapper = new Mapper();
        BookDTO bookDTO = mapper.map(book, BookDTO.class);

        BookEntryDTO entry1DTO = bookDTO.getEntries().get(0);
        BookEntryDTO entry2DTO = bookDTO.getEntries().get(1);

        assertEquals(entry1DTO.getId(), entry1.getId());
        assertEquals(entry2DTO.getId(), entry2.getId());
        assertEquals(bookDTO, entry1DTO.getBookDTO());
        assertEquals(bookDTO, entry2DTO.getBookDTO());
    }

    @Test
    public void inheritance() {
        Book book = new Book(0L, "Book");

        PhoneEntry entry1 = new PhoneEntry();
        entry1.setBook(book);
        entry1.setId(1);
        entry1.setPhoneNumber("123456789");

        AddressEntry entry2 = new AddressEntry();
        entry2.setBook(book);
        entry2.setId(2);
        entry2.setCity("Paris");
        entry2.setCountry("France");

        book.setEntries(Arrays.asList(entry1, entry2));

        Mapper mapper = new Mapper()
                .addMapping(AddressEntry.class, AddressEntryDTO.class)
                .addMapping(PhoneEntry.class, PhoneEntryDTO.class);

        BookDTO bookDTO = mapper.map(book, BookDTO.class);

        assertEquals(2, bookDTO.getEntries().size());

        BookEntryDTO phoneEntryDTO = bookDTO.getEntries().get(0);
        BookEntryDTO addressEntryDTO = bookDTO.getEntries().get(1);

        Assert.assertTrue(phoneEntryDTO instanceof PhoneEntryDTO);
        Assert.assertTrue(addressEntryDTO instanceof AddressEntryDTO);

        assertEquals(entry1.getPhoneNumber(), ((PhoneEntryDTO) phoneEntryDTO).getPhoneNumber());
        assertEquals(entry2.getCity(), ((AddressEntryDTO) addressEntryDTO).getCity());
        assertEquals(bookDTO, phoneEntryDTO.getBookDTO());
        assertEquals(bookDTO, addressEntryDTO.getBookDTO());
    }

    @Test(expected = PropertyNotFoundException.class)
    public void throwExceptionIfPropertyNotFoundInSource() {
        new Mapper()
                .setThrowExceptionIfPropertyNotFoundInSource(true)
                .map(new A(), B.class);
    }

    public static class A {
    }

    public static class B {
        public void setName(String name) { }
    }
}
