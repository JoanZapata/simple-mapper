# Simple Mapper 
> I created this library because I'm tired of big xml-based or annotation-based mapping frameworks when all I need is a **very basic mapping** between business objects and lightweight DTOs with a similar structure.

**Simple Mapper** is a java objects mapper meant to be very easy-to-use and intuitive. It looks for setters in the destination object and try to find the corresponding getter in the source object. It manages ```cyclic dependencies```, ```inheritance```, and ```hooks```.

# Get it

Simple Mapper is **available in Maven Central**:

```xml
<dependency>
    <groupId>com.joanzapata.mapper</groupId>
    <artifactId>simple-mapper</artifactId>
    <version>1.0.0</version>
</dependency>
```

# Basics

You do all the mapping with the ```Mapper``` object and its ```map()``` method.

```java
// First, create a ```Mapper``` object.
Mapper mapper = new Mapper();

// Map a plain old java object with map()
BookDTO bookDTO = mapper.map(book, BookDTO.class);

// Mapper will detect lists and maps so you can do things like:
List<BookDTO> bookListDTO = mapper.map(bookList, BookDTO.class);
Map<Long, BookDTO> bookListDTO = mapper.map(bookMap, Long.class, BookDTO.class);
```

# Inheritance

If you need support for inheritance, you must provide the mappings of the subclasses:

```java
Mapper mapper = new Mapper()
    .addMapping(AddressEntry.class, AddressEntryDTO.class)
    .addMapping(PhoneEntry.class, PhoneEntryDTO.class);
```

* You can register mapping in both directions by using the ```addBidirectionalMapping``` method.

# Name binding

The mapper supports name variations, that means for example that ```public Book getBook()``` in the source object is considered as a valid candidate for ```public void setBookDTO(BookDTO bookDTO)``` in the destination object. The library currently manage ```DTO``` and ```BO``` name variations.

# Hooks

If you need custom mapping or additional operations after some mappings, you can provide hooks:

```java
Mapper mapper = new Mapper()
    .addHook(new Hook<BookEntry, BookEntryDTO>() {
        @Override
        public void extraMapping(BookEntry source, BookEntryDTO destination) {
            // Do additional operations in the destination object
        }
    };
});
```

* Hooks are called **after** the object has been fully mapped.
* Hooks are guaranteed to be called in the **order** you added them to the mapper. 
