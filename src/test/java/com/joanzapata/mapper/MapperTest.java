/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.joanzapata.mapper;

import com.joanzapata.mapper.model.Book;
import com.joanzapata.mapper.model.BookDTO;
import com.joanzapata.mapper.model.BookEntry;
import com.joanzapata.mapper.model.BookEntryDTO;
import com.joanzapata.mapper.model.ModelWithCollection;
import com.joanzapata.mapper.model.ModelWithEnum;
import com.joanzapata.mapper.model.ModelWithEnumDTO;
import com.joanzapata.mapper.model.ModelWithSet;
import com.joanzapata.mapper.model.ModelWithString;
import com.joanzapata.mapper.model.entry.AddressEntry;
import com.joanzapata.mapper.model.entry.AddressEntryDTO;
import com.joanzapata.mapper.model.entry.PhoneEntry;
import com.joanzapata.mapper.model.entry.PhoneEntryDTO;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class MapperTest {

    @Test(expected = StackOverflowError.class)
    public void cyclicDependenciesErrorWithCustomMapper() {
        final Book testBook = createTestBook();

        final Mapper bookMapper = new Mapper();
        final Mapper phoneEntryMapper = new Mapper()
                .customMapper(new CustomMapper<Book, BookDTO>() {
                    @Override
                    public BookDTO map(Book source, MappingContext context) {
                        return bookMapper.map(source, BookDTO.class);
                    }
                });

        bookMapper.customMapper(new CustomMapper<PhoneEntry, PhoneEntryDTO>() {
            @Override
            public PhoneEntryDTO map(PhoneEntry source, MappingContext context) {
                return phoneEntryMapper.map(source, PhoneEntryDTO.class);
            }
        });

        bookMapper.map(testBook, BookDTO.class);
    }

    @Test
    public void cyclicDependenciesSuccessWithCustomMapper() {
        final Book testBook = createTestBook();

        final Mapper bookMapper = new Mapper();
        final Mapper phoneEntryMapper = new Mapper()
                .customMapper(new CustomMapper<Book, BookDTO>() {
                    @Override
                    public BookDTO map(Book source, MappingContext context) {
                        return bookMapper.map(source, BookDTO.class, context);
                    }
                });

        bookMapper.customMapper(new CustomMapper<PhoneEntry, PhoneEntryDTO>() {
            @Override
            public PhoneEntryDTO map(PhoneEntry source, MappingContext context) {
                return phoneEntryMapper.map(source, PhoneEntryDTO.class, context);
            }
        });

        bookMapper.map(testBook, BookDTO.class);
    }

    @Test
    public void singleObjectWithCustomMapperToNull() {
        Mapper mapper = new Mapper().customMapper(new CustomMapper<Book, BookDTO>() {
            @Override
            public BookDTO map(Book source, MappingContext mappingContext) {
                return null;
            }
        });
        Book book = new Book(5L, "Book");
        assertNull(mapper.map(book, BookDTO.class));
    }

    @Test
    public void objectListWithCustomMapperToNull() {
        Mapper mapper = new Mapper().customMapper(new CustomMapper<Book, BookDTO>() {
            @Override
            public BookDTO map(Book source, MappingContext mappingContext) {
                return null;
            }
        });
        Book b1 = new Book(1L, "Book1");
        Book b2 = new Book(2L, "Book2");
        Book b3 = new Book(3L, "Book3");
        List<Book> bookList = Arrays.asList(b1, b2, b3);
        List<BookDTO> bookListDTO = mapper.map(bookList, BookDTO.class);
        assertTrue(bookListDTO.isEmpty());
    }

    @Test
    public void singleObjectWithCustomMapperToFixed() {
        Mapper mapper = new Mapper().customMapper(new CustomMapper<Book, BookDTO>() {
            @Override
            public BookDTO map(Book source, MappingContext mappingContext) {
                final BookDTO bookDTO = new BookDTO();
                bookDTO.setName("Fixed");
                return bookDTO;
            }
        });
        Book book = new Book(5L, "Book");
        final BookDTO bookDto = mapper.map(book, BookDTO.class);
        assertEquals("Fixed", bookDto.getName());
    }

    @Test
    public void objectListWithCustomMapperToFixed() {
        Mapper mapper = new Mapper().customMapper(new CustomMapper<Book, BookDTO>() {
            @Override
            public BookDTO map(Book source, MappingContext mappingContext) {
                final BookDTO bookDTO = new BookDTO();
                bookDTO.setName("Fixed");
                return bookDTO;
            }
        });
        Book b1 = new Book(1L, "Book1");
        Book b2 = new Book(2L, "Book2");
        Book b3 = new Book(3L, "Book3");
        List<Book> bookList = Arrays.asList(b1, b2, b3);
        List<BookDTO> bookListDTO = mapper.map(bookList, BookDTO.class);
        for (BookDTO bookDTO : bookListDTO) {
            assertEquals("Fixed", bookDTO.getName());
        }
    }

    @Test
    public void inheritanceWithCustomMapperToNull() {
        Book book = createTestBook();
        Mapper mapper = new Mapper()
                .mapping(AddressEntry.class, AddressEntryDTO.class)
                .mapping(PhoneEntry.class, PhoneEntryDTO.class)
                .customMapper(new CustomMapper<PhoneEntry, PhoneEntryDTO>() {
                    @Override
                    public PhoneEntryDTO map(PhoneEntry source, MappingContext mappingContext) {
                        return null;
                    }
                });

        BookDTO bookDTO = mapper.map(book, BookDTO.class);
        assertEquals(1, bookDTO.getEntries().size());
    }

    @Test
    public void inheritanceWithCustomMapperToFixed() {
        Book book = createTestBook();
        Mapper mapper = new Mapper()
                .mapping(AddressEntry.class, AddressEntryDTO.class)
                .mapping(PhoneEntry.class, PhoneEntryDTO.class)
                .customMapper(new CustomMapper<PhoneEntry, PhoneEntryDTO>() {
                    @Override
                    public PhoneEntryDTO map(PhoneEntry source, MappingContext mappingContext) {
                        final PhoneEntryDTO phoneEntryDTO = new PhoneEntryDTO();
                        phoneEntryDTO.setPhoneNumber("Fixed");
                        return phoneEntryDTO;
                    }
                });

        BookDTO bookDTO = mapper.map(book, BookDTO.class);
        assertEquals(2, bookDTO.getEntries().size());
        assertTrue(bookDTO.getEntries().get(0) instanceof PhoneEntryDTO);
        assertEquals("Fixed", ((PhoneEntryDTO) bookDTO.getEntries().get(0)).getPhoneNumber());
    }

    @Test
    public void inheritanceWithUncalledCustomMapper() {
        Book book = createTestBook();
        Mapper mapper = new Mapper()
                .mapping(AddressEntry.class, AddressEntryDTO.class)
                .mapping(PhoneEntry.class, PhoneEntryDTO.class)
                .customMapper(new CustomMapper<PhoneEntry, Object>() {
                    @Override
                    public Object map(PhoneEntry source, MappingContext mappingContext) {
                        fail("Shouldn't call this mapper");
                        return null;
                    }
                });
        mapper.map(book, BookDTO.class);
    }

    @Test
    public void singleObject() {
        Mapper mapper = new Mapper();
        Book book = new Book(5L, "Book");
        BookDTO bookDTO = mapper.map(book, BookDTO.class);
        assertEquals(book.getId(), bookDTO.getId());
        assertEquals(book.getName(), bookDTO.getName());
    }

    @Test
    public void singleObjectList() {
        Mapper mapper = new Mapper();
        Book b1 = new Book(1L, "Book1");
        Book b2 = new Book(2L, "Book2");
        Book b3 = new Book(3L, "Book3");
        List<Book> bookList = Arrays.asList(b1, b2, b3);
        List<BookDTO> bookListDTO = mapper.map(bookList, BookDTO.class);
        assertEquals(new Long(1), bookListDTO.get(0).getId());
        assertEquals(new Long(2), bookListDTO.get(1).getId());
        assertEquals(new Long(3), bookListDTO.get(2).getId());
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
        Book book = createTestBook();

        Mapper mapper = new Mapper()
                .mapping(AddressEntry.class, AddressEntryDTO.class)
                .mapping(PhoneEntry.class, PhoneEntryDTO.class);

        BookDTO bookDTO = mapper.map(book, BookDTO.class);

        assertEquals(2, bookDTO.getEntries().size());

        BookEntryDTO phoneEntryDTO = bookDTO.getEntries().get(0);
        BookEntryDTO addressEntryDTO = bookDTO.getEntries().get(1);

        Assert.assertTrue(phoneEntryDTO instanceof PhoneEntryDTO);
        Assert.assertTrue(addressEntryDTO instanceof AddressEntryDTO);

        assertEquals(
                ((PhoneEntry) book.getEntries().get(0)).getPhoneNumber(),
                ((PhoneEntryDTO) phoneEntryDTO).getPhoneNumber());
        assertEquals(
                ((AddressEntry) book.getEntries().get(1)).getCity(),
                ((AddressEntryDTO) addressEntryDTO).getCity());

        assertEquals(bookDTO, phoneEntryDTO.getBookDTO());
        assertEquals(bookDTO, addressEntryDTO.getBookDTO());
    }

    @Test(expected = StrictModeException.class)
    public void throwExceptionIfPropertyNotFoundInSource() {
        new Mapper()
                .strictMode(true)
                .map(new A(), B.class);
    }

    @Test
    public void bidirectionalMappings() {
        Book book = createTestBook();

        Mapper mapper = new Mapper()
                .biMapping(AddressEntry.class, AddressEntryDTO.class)
                .biMapping(PhoneEntry.class, PhoneEntryDTO.class)
                .strictMode(true);

        BookDTO bookDTO = mapper.map(book, BookDTO.class);
        Book newBook = mapper.map(bookDTO, Book.class);

        assertEquals(book.getId(), newBook.getId());
        assertEquals(book.getName(), newBook.getName());

        assertEquals(
                book.getEntries().get(0).getId(),
                newBook.getEntries().get(0).getId());
        assertEquals(
                book.getEntries().get(1).getId(),
                newBook.getEntries().get(1).getId());
    }

    @Test
    public void nameVariations() {
        NameVariationTest in = new NameVariationTest();
        NameVariationTestDTO out = new Mapper().map(in, NameVariationTestDTO.class);
        Assert.assertEquals(in.getTest(), out.getTestDTO());
        Assert.assertEquals(in.getTestOther(), out.getTestOtherDTO());
    }

    @Test
    public void testHook() {
        Mapper mapper = new Mapper()
                .hook(new Hook<Book, BookDTO>() {
                    @Override
                    public void extraMapping(Book from, BookDTO to) {
                        to.setName("ItWorks.");
                    }
                });
        Book testBook = createTestBook();
        BookDTO out = mapper.map(testBook, BookDTO.class);
        assertEquals("ItWorks.", out.getName());
    }

    @Test
    public void testHookWithInheritance() {
        Mapper mapper = new Mapper()
                .hook(new Hook<BookEntry, BookEntryDTO>() {
                    @Override
                    public void extraMapping(BookEntry from, BookEntryDTO to) {
                        to.setId(1337);
                    }
                });
        Book testBook = createTestBook();
        BookDTO out = mapper.map(testBook, BookDTO.class);
        assertEquals(1337, out.getEntries().get(0).getId());
        assertEquals(1337, out.getEntries().get(1).getId());
    }

    @Test
    public void testMapMapping() {
        Mapper mapper = new Mapper();
        Book testBook = createTestBook();
        BookDTO out = mapper.map(testBook, BookDTO.class);
        assertEquals(1L, out.getEntriesById().get(1L).getId());
        assertEquals(2L, out.getEntriesById().get(2L).getId());
    }

    @Test
    public void testNull() {
        Mapper mapper = new Mapper();
        Object nullObject = null;
        assertNull(mapper.map(nullObject, BookDTO.class));
    }

    @Test
    public void testDirectMapMapping() {
        Mapper mapper = new Mapper()
                .biMapping(AddressEntry.class, AddressEntryDTO.class)
                .biMapping(PhoneEntry.class, PhoneEntryDTO.class)
                .strictMode(true);

        Map<Long, Book> in = new HashMap<Long, Book>();
        in.put(1L, createTestBook(1L));
        in.put(2L, createTestBook(2L));

        Map<Long, BookDTO> out = mapper.map(in, Long.class, BookDTO.class);
        assertEquals(1l, (long) out.get(1L).getId());
        assertEquals(2l, (long) out.get(2L).getId());
    }

    @Test(expected = StrictModeException.class)
    public void testIncompatibleTypes() {
        final Mapper mapper = new Mapper().strictMode(true);
        mapper.map(1L, Byte.class);
    }

    @Test(expected = StrictModeException.class)
    public void testDirectIncompatibleTypes() {
        Map<Long, String> input = new HashMap<Long, String>();
        input.put(1L, "1");
        Map<Long, BookDTO> incompatibleOutput = new Mapper().strictMode(true).map(input, Long.class, BookDTO.class);
    }

    @Test
    public void testEnumDirect() {
        Mapper mapper = new Mapper();
        assertEquals(EnumSourceDTO.A, mapper.map(EnumSource.A, EnumSourceDTO.class));
        assertEquals(EnumSourceDTO.B, mapper.map(EnumSource.B, EnumSourceDTO.class));
        assertEquals(EnumSourceDTO.C, mapper.map(EnumSource.C, EnumSourceDTO.class));
    }

    @Test
    public void testEnumIndirect() {
        ModelWithEnum input = new ModelWithEnum();
        Mapper mapper = new Mapper();
        final ModelWithEnumDTO output = mapper.map(input, ModelWithEnumDTO.class);
        assertEquals(ModelWithEnumDTO.MyEnumDTO.A, output.getMyEnumsDTO().get(0));
    }

    @Test(expected = StrictModeException.class)
    public void testIncompatibleTypesStringToList_strictMode() {
        Mapper mapper = new Mapper().strictMode();
        ModelWithString input = new ModelWithString();
        input.setData("Test");
        ModelWithCollection output = mapper.map(input, ModelWithCollection.class);
    }

    @Test
    public void testIncompatibleTypesStringToList() {
        Mapper mapper = new Mapper();
        ModelWithString input = new ModelWithString();
        input.setData("Test");
        ModelWithCollection output = mapper.map(input, ModelWithCollection.class);
        assertNull(output.getData());
    }

    @Test(expected = StrictModeException.class)
    public void testIncompatibleTypesListToString_strictMode() {
        Mapper mapper = new Mapper().strictMode();
        ModelWithCollection input = new ModelWithCollection();
        final ArrayList<String> data = new ArrayList<String>();
        data.add("Test");
        input.setData(data);
        ModelWithString output = mapper.map(input, ModelWithString.class);
    }

    @Test
    public void testIncompatibleTypesListToString() {
        Mapper mapper = new Mapper();
        ModelWithCollection input = new ModelWithCollection();
        final ArrayList<String> data = new ArrayList<String>();
        data.add("Test");
        input.setData(data);
        ModelWithString output = mapper.map(input, ModelWithString.class);
        assertNull(output.getData());
    }

    @Test
    public void testSet() {
        Mapper mapper = new Mapper().strictMode();
        ModelWithSet input = new ModelWithSet();
        final Set<String> data = new HashSet<String>();
        data.add("Test");
        input.setData(data);
        ModelWithSet output = mapper.map(input, ModelWithSet.class);
        assertTrue(output.getData().contains("Test"));
        assertEquals(1, output.getData().size());
    }

    @Test
    public void testSet_direct() {
        Mapper mapper = new Mapper().strictMode();
        Set<String> input = new HashSet<String>();
        input.add("Test");
        Set<String> output = mapper.map(input, String.class);
        assertTrue(output.contains("Test"));
        assertEquals(1, output.size());
    }
    
    @Test
    public void testCustomBiMappers() {
        Book book = createTestBook();

        Mapper mapper = new Mapper()
            .customBiMapper(new CustomBiMapper<Book, BookDTO>() {
                @Override
                public Book mapBackward(BookDTO d, MappingContext c) {
                    return new Book(d.getId(), d.getName());
                }
    
                @Override
                public BookDTO mapForward(Book s, MappingContext c) {
                    BookDTO b = new BookDTO();
                    b.setId(s.getId());
                    b.setName(s.getName());
                    return b;
                    
                }
            });

        BookDTO bookDTO = mapper.map(book, BookDTO.class);
        Book newBook = mapper.map(bookDTO, Book.class);

        assertEquals(book.getId(), newBook.getId());
        assertEquals(book.getName(), newBook.getName());
    }

    private Book createTestBook() {
        return createTestBook(0L);
    }

    private Book createTestBook(long id) {
        Book book = new Book(id, "Book");

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

        Map<Long, BookEntry> map = new HashMap<Long, BookEntry>();
        map.put(entry1.getId(), entry1);
        map.put(entry2.getId(), entry2);
        book.setEntriesById(map);

        return book;
    }

    public static enum EnumSource {
        A, B, C
    }

    public static enum EnumSourceDTO {
        A, B, C
    }

    public static class A {
    }

    public static class B {
        public void setName(String name) {
        }
    }

    public static class NameVariationTest {
        String test = "Test", testOther = "TestOther";

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }

        public String getTestOther() {
            return testOther;
        }

        public void setTestOther(String testOther) {
            this.testOther = testOther;
        }
    }

    public static class NameVariationTestDTO {
        String testDTO, testOtherDTO;

        public String getTestDTO() {
            return testDTO;
        }

        public void setTestDTO(String testDTO) {
            this.testDTO = testDTO;
        }

        public String getTestOtherDTO() {
            return testOtherDTO;
        }

        public void setTestOtherDTO(String testOtherDTO) {
            this.testOtherDTO = testOtherDTO;
        }
    }
}
