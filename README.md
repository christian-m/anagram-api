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

### Search history

```
POST http://{{host}}/history
Content-Type: application/x-www-form-urlencoded

text=a given text
```

The result of the service is a list of anagrams of the given text that have been entered for comparison in the past.

```
{
    "text": "a given text",
    "anagrams": [
        "given a text"
    ]
}
```

> Important Note: \
> The inputs are stored only locally and were not persisted.

## Deployment

The service is deployed on Google Cloud Run for testing purposes.

You can call the [Swagger UI](https://anagram-api.matzat.dev/swagger-ui/index.html) for testing the service.

> Please Note: \
> Cloud Run containers will be shut down after some idle time. \
> Please allow some delay if the service wasn't called for some time to ramp up the container again. \
> The history data isn't persisted between different container runs.


## Motivation

* Using Kotlin, because I learned to love it the past years
* Using Maven, because I haven't used it a long time and want to see how it behave with Kotlin
* Using Ktor that I never used before, just out of curiosity (wouldn't do that in a productive project though ;-))
* Using KoDeIn as dependency injection framework out of curiosity
