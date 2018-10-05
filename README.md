# Kotlin ❤ 2018

> Showcasing the coolest language features

It's really hard to pick up the best features of Kotlin, since one can easily end up writing about everything. But it would just mean rewriting their [awesome documentation](https://kotlinlang.org/docs/reference/idioms.html) :). The most important is what can actually be used in solving *real world issues™*.

I was working on a internal time-tracking application. Backend is written in Java and it has a fairly complex logic when it comes to operations with time. For instance, the working day is defined as 8 hours, and anything that's above it is considered an overtime. 

*Note: This is actually a simplification of this problem, but let's go with it for the sake of this example*

### Data classes

In Java, it is implemented using various libraries and utility functions on top of complex structures. What I really like about Kotlin, is that it's very expressive, while it stays readable. For instance, if we would like to define the structure for `Time`, we could write a simple **data class**

```kotlin
data class Time(val hours: Int, val minutes: Int)
```

This will give you an immutable data structure, with `equals`/`hashCode`/`toString`/`getters` and `setters` (and other) functions implemented out of the box. It's something Java people would call a POJO, but defined in one line.

### Operators overloading

The next thing, useful in our scenario would be comparing one Time to other. For instance `1:30` is more than `0:45` and so on. In Java, you can implement `Comparable` and override `compareTo` function. The same works in Kotlin, but with one big difference - you are able to compare instances by using equality operators.

```kotlin
data class Time(val hours: Int, val minutes: Int) : Comparable<Time> {
    override fun compareTo(other: Time): Int {
        return when {
            this.hours == other.hours -> this.minutes - other.minutes
            else -> this.hours - other.hours
        }
    }
}

println(Time(1,30) > Time(0,45)) // true
```

You can also **override other operators**. For instance we would need the `minus` operator. You can just add this to `Time` class:

```kotlin
operator fun minus(other: Time): Time {
    return convertToTime(convertToDouble(this) - convertToDouble(other))
}
```
and then
```kotlin
println(Time(1,30) - Time(0,45)) // Time(hours=0, minutes=45)
```

### Companion objects

You can see that I've used some utility functions `convertToTime` and `convertToDouble`. If you are working with Java, you are used to create your own `XYZUtil` classes, so instead of creating `TimeUtil` class, we can use the **companion object**.

```kotlin
data class Time(val hours: Int, val minutes: Int) : Comparable<Time> {

    companion object {
        @JvmStatic
        fun convertToTime(time: Number): Time {
            // implementation
        }
        @JvmStatic
        fun convertToDouble(time: Time): Double {
            // implementation
        }
    }
}
```

### Java interoperability

As you can see, I've annotated the methods with `@JvmStatic` annotation. Kotlin does not have a `static` methods, but to provide **perfect interoperability with Java**, it can make them visible by using this annotation. Thus, you can call this in Java:

```java
public class JavaClass {
    public static void main(String[] args) {
        System.out.println(Time.convertToTime(2.75));
    }
}
```

You can expose also constants and other fields, that would be visible in Java.

### Null safety

Kotlin does not allow you to use null values by default. However, if you for some reason (e.g. Java inter-op) want to allow them, you can define the parameter types with `?`. Let's define another constructor in `Time` that accepts only hours:

```kotlin
data class Time(val hours: Int, val minutes: Int) {
    constructor(hours: Int?) : this(hours ?: 0, 0)
}
```

We define it using `constructor` keyword and specifying parameter as `Int?`. But if this value is `null`, we would like to provide zero as a default. You can use so called **elvis operator** `?:` which will evaluate the expression and if it is null, it will use the right hand value as a fallback. 

### Smart casting

In Java, it is very annoying to do the "check-than-cast" routine, where you first check whether object instance is `instanceof` some class and then manually cast it. Let's look at the Kotlin implementation of `convertToTime` function defined above:

```kotlin
fun convertToTime(time: Number): Time {
    return when (time) {
        is Double -> {
            val fullHours = Math.floor(time).toInt()
            Time(fullHours, ((time - fullHours) * 60).toInt())
        }
        is Float -> convertToTime(time.toDouble())
        is Int -> Time(time)
        else -> Time(time.toInt())
    }
}
```

You might not know this, but function `Math.floor` is expecting `Double` as a parameter. We are not casting it explicitly, but since we're doing it in on of the branches of `when` (i.e. `switch` in Java), it is automatically **smart casted** to `Double`. And IDE will highlight this for you, so that you are aware of it! The same goes to call `is Int -> Time(time)` which is smart casted to `Int` and uses the constructor defined in the previous section.

### Extension functions

We've talked about writing utility functions on top of our newly created classes by using companion objects. But what if we would like to add functionality to existing classes?

For instance, we would like to provide a `toString` implementation for our Time class, that would print `00:45` instead of the data class default `Time(hours=0, minutes=45)`:

```kotlin
override fun toString() = "${hours.padZeros(2)}:${minutes.padZeros(2)}"
```

`hours` and `minutes` are `Int` properties of Time class, but function `padZeros` does not exist in `Int` class. If you really want to use this across the whole project, you can define an application-wide **extension function**:

```kotlin
fun Int.padZeros(length: Int) = "${if (this.sign < 0) "-" else ""}${this.absoluteValue.toString().padStart(length, '0')}"
```

The `this` in the function above refers to instance on which the function is invoked, so you can use it however you like.

*Note: As you see, Kotlin allows powerful String templating using syntax known from other languages: `"${someVar}"` :)*

### Ranges

Let's put it all together in a next example. We would like to have a class for `Workday`, which can tell us when the person started and concluded his day at work. One of the specialities of this class, would be determining, whether person was at work at specific time.

We can achieve this by using a `ClosedRange` interface, which allows us to use this syntax:

```kotlin
println(Time(10, 30) in Workday(Time(8), Time(16)))
```

The implementation is pretty straight-forward thanks to conventions used for `ClosedRange`. It expects that the implementation provides the `start` and `endInclusive` properties of specified type (which in our case is `Time`)

```kotlin
class Workday(override val start: Time, override val endInclusive: Time) : ClosedRange<Time>
```

One important note is that this works because `ClosedRange` expects that the type is implementing `Comparable` interface, which our `Time` class does.

### Higher order functions

Kotlin supports some idioms of functional programming by treating functions as a first-class citizens in language. This means, that you can use them as either parameters, return types or declare them as variables.

For instance, we would like to add functionality which will check the constructor parameters once the class is initialized:

```kotlin
class Workday(override val start: Time, override val endInclusive: Time) : ClosedRange<Time> {

    init {
        // higher order function
        require(isValid()) { "Range `${this.start} to ${this.endInclusive}` does not define a valid workday." }
    }

    fun isValid() = start >= Time(0, 0) && endInclusive <= Time(23, 59)
}
```

There are several things going on here at once. First, we define a function `isValid` which validates that start and end times are within a day range. Then, in `init` block (which is called after constructing instance) a `require` function is called, which is defined in Kotlin *stdlib* as this:

```kotlin
public inline fun require(
    value: Boolean,
    lazyMessage: () -> Any
): Unit
```

In other words, it will evaluate a first parameter, and if it is `false` it will invoke a second parameter which is a function (declared using [lambda expression syntax](https://kotlinlang.org/docs/reference/lambdas.html#instantiating-a-function-type)) that defines the message used in `IllegalArgumentException`. So this call

```kotlin
Workday(Time(-1), Time(12))
```

will end up like this:

```text
Exception in thread "main" java.lang.IllegalArgumentException: Range `-01:00 to 12:00` does not define a valid workday.
	at Workday.<init>(WorkingTime.kt:62)
	at WorkingTimeKt.main(WorkingTime.kt:83)
```

*Note: In Kotlin, if the last parameter is a lambda expression, you can write it after the function call (as in our example). But it is the same as `require(isValid(), { "Range ${this.start} to ${this.endInclusive} does not define a valid workday." })`*

## Wrapping it up

There are many hidden gems, that you will discover when working with Kotlin. It enables you to write concise and readable code while working with known technologies (e.g. Java, JVM and it's frameworks). If you would like to discover them, you can start by going through [Kotlin koans](https://try.kotlinlang.org), a set of exercises that go through most of the language features - and you can do it in browser!
