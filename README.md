# Simple Mapper 
> Note that this is a **0.0.1-SNAPSHOT** versions, it hasn't been deeply tested at all. I'm using it because I'm tired of big mapping frameworks when all I need is a very basic mapping between business objects and lightweight DTOs with the same structure.

**Simple Mapper** is a java objects mapper meant to be very easy-to-use and intuitive. It looks for setters in the destination object and try to find the corresponding getter in the source object. It manages ```cyclic dependencies``` and ```inheritance```.

# Get it

It's not in maven central yet, so you need to build it.

```shell
git clone https://github.com/JoanZapata/simple-mapper.git
cd simple-mapper
mvn install
```

Then include it in your ```pom.xml```

```xml
<dependency>
    <groupId>com.joanzapata.mapper</groupId>
    <artifactId>simple-mapper</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

# How to use


For the basics, just provide the source object and the destination:

```java
Mapper mapper = new Mapper();
BookDTO bookDTO = mapper.map(book, BookDTO.class);
```

To convert a list, exactly the same:

```java
 List<BookDTO> bookListDTO = mapper.map(bookList, BookDTO.class);
```

If you need support for inheritance, you must provide the mappings of the subclasses:

```java
Mapper mapper = new Mapper()
    .addMapping(AddressEntry.class, AddressEntryDTO.class)
    .addMapping(PhoneEntry.class, PhoneEntryDTO.class);
```

You can get a bidirectionnal mapper by using the ```addBidirectionalMapping``` method:

```java
Mapper mapper = new Mapper()
    .addBidirectionalMapping(AddressEntry.class, AddressEntryDTO.class)
    .addBidirectionalMapping(PhoneEntry.class, PhoneEntryDTO.class);
```

The mapper supports name variations, that means for example that ```public Book getBook()``` can be used to fill ```public void setBookDTO(BookDTO bookDTO)```. For the moment it's just a ```startsWith``` comparison but later on it should be able to check the type names and validate the variations in the accessor names.
