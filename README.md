## Description

RESTful money transfer service

## Technologies

**Netty**  - web server with custom router <br>
**Hazelcast** - in memory data store <br>
**Typesafe** - configuration <br>
**Lombok** - pills from checked exceptions headache <br>
**org.JSON** - JSON implementation <br>
**Apache Http Client** - client for integration testing <br>
**JUnit** - for junit staff

## Configuration

Application configuration is stored in `application.json` file

### Available params

- `rest-port`: Int - port for web server to start on
- `executor-threads`: Int - number of threads for concurrent request handling

## Run

**Prerequisites**
- Java 12 (Only Oracle JDK has been tested)

To start application *gradle* task `run` should be executed. Gradle wrapper `gradlew run` could be used if no supported gradle version is present

## API

- **POST** `/account/create` => *Response JSON:* {"accountId": "*created-account-id*"} - creates new account and returns its id <br>
- **POST** `/lotteryWinner` params: `account`: *String*, `amount`: *Long* => *Response JSON:* {"message", "Wow, we have a winner here!"} - increases balance of account with `account` id by `amount`, `amount` should be > 0 <br>
- **GET** `/account/balance` params: `account`: *String* => *Response JSON:* {"balance": *balance*} - returns amount of account with `account` id<br>
- **POST** `/transfer` params: `debit`: *String*, `credit`: *String*, `amount`: *Long* => *Response JSON:* {"message": "Transfer successful"} - transfers `amount` of money from account `debit` to account `credit`, `amount` should be > 0, `debit` should not be equal `credit`<br>

