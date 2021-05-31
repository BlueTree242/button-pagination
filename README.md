# button-utils

## Adds a collection of utils for the Buttons API

see src/main/examples for examples

docs coming soon

gradle:

```gradle
repositories {
  maven { url 'https://jitpack.io' }
}

dependencies {
  implementation 'com.github.arynxd:button-utils:VERSION'
}
```

maven:

```xml
<repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
</repositories>

<dependency>
    <groupId>com.github.arynxd</groupId>
    <artifactId>button-utils</artifactId>
    <version>VERSION</version>
</dependency>
```

## TODO

- Add timeout checks for greater than 15 minutes (interaction limit)
- Slash command paginator
- Refactor & organise
- Menus (& Paginated)
