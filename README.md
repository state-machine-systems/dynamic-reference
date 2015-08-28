Dynamically-scoped references offer a more disciplined alternative to global variables. They're a handy feature from
the Lisp family of languages.

In Java, `static` fields are equivalent to global variables, with all the bad stuff that entails. By contrast, you
create a `DynamicReference` with an initial default value, which can be overridden inside a given block
scope. Code inside the block (at any call depth) will automatically use the overridden value. After the block
finishes, the value reverts back.

Here's an example of overriding a reference to `System.out`:

    import com.statemachinesystems.util.DynamicReference;
    import java.io.*;

    ...

    DynamicReference<PrintStream> out = new DynamicReference<>(System.out);

    PrintStream log = new PrintStream(new File("out.log"), "UTF-8");

    void sayHello() {
        out.get().println("Hello World!");
    }

    ...

    // write to standard output
    sayHello();

    ...

    out.withValue(log, () -> {
        // write to the log file
        sayHello();
    });

This implementation is based on the `DynamicVariable` class from the Scala standard library.

### Getting started

This library is in the Maven Central repo, so just add the following chunk to your pom.xml (or the equivalent for Gradle/SBT/whatever):

    <dependency>
        <groupId>com.statemachinesystems</groupId>
        <artifactId>dynamic-reference</artifactId>
        <version>1.0</version>
    </dependency>

&copy; 2015 State Machine Systems Ltd. [Apache Licence, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)