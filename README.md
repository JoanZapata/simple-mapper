# Simple Mapper 
> I created this library because I'm tired of big xml-based or annotation-based mapping frameworks when all I need is a **very basic mapping** between business objects and lightweight DTOs with a similar structure.

**Simple Mapper** is a java objects mapper meant to be very easy-to-use and intuitive. It looks for setters in the destination object and try to find the corresponding getter in the source object. It manages ```cyclic dependencies```, ```inheritance```, and ```hooks```.

# Get it

Simple Mapper is **available in Maven Central**:

```xml
<dependency>
    <groupId>com.joanzapata.mapper</groupId>
    <artifactId>simple-mapper</artifactId>
    <version>1.0.3</version>
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

![Alt](https://raw.github.com/JoanZapata/simple-mapper/master/inheritance.png)

```
Mapper mapper = new Mapper()
    .mapping(B.class, B'.class)
    .mapping(C.class, C'.class);
```

* You can register mapping in both directions by using the ```biMapping``` method.

# Name binding

The mapper supports name variations, that means for example that ```public Book getBook()``` in the source object is considered as a valid candidate for ```public void setBookDTO(BookDTO bookDTO)``` in the destination object. The library currently manage ```DTO``` and ```BO``` name variations.

# Hooks

If you need custom mapping or additional operations after some mappings, you can provide hooks:

```java
Mapper mapper = new Mapper()
    .hook(new Hook<BookEntry, BookEntryDTO>() {
        @Override
        public void extraMapping(BookEntry source, BookEntryDTO destination) {
            // Do additional operations in the destination object
        }
    };
});
```

* Hooks are called **after** the object has been fully mapped.
* Hooks are guaranteed to be called in the **order** you added them to the mapper. 

# Strict Mode

The simple-mapper is very permissive by default. If something wrong happens mapping a property, it gives up and goes to the next property to map. You can override this behavior by setting the ```StrictMode```. In this mode, the ```map()``` function will raise a ```StrictModeException``` if something goes wrong:
* No getter found that matches a setter in the destination object.
* Types mismatch between getter and setter.
* Destination object doesn't have an empty constructor.
* Any other bad thing happens during the mapping.

```java
Mapper mapper = new Mapper().strictMode();
```

# License

```
Copyright 2013 Joan Zapata

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
