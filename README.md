# Anagram comparison API

## Introduction

This is a web service with REST API to compare texts if they are an anagram of each other.

### Swagger UI

You can find a Swagger UI endpoint at `http://{{host}}/swagger-ui` 

### Compare texts

```
POST http://{{host}}/compare
Content-Type: application/x-www-form-urlencoded

text=a given text&
candidate=a given candidate to compare
```

The result of the service is a JSON object:

```
{
    "text": "a given text",
    "candidate": "a given candidate to compare",
    "result": "EQUAL"
}
```

`result` is one of:

* `ANAGRAM` - the given text and candidate are an anagram of each other
* `EQUAL` - the given text and candidate are equal
* `NO_MATCH` - the given text and candidate are different from each other

## Motivation

* Using Kotlin, because I learned to love it the past years
* Using Maven, because I haven't used it a long time and want to see how it behave with Kotlin
* Using Ktor that I never used before, just out of curiosity (wouldn't do that in a productive project though ;-)) 

