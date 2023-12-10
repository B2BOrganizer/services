# Installation

1. Run mongodb database (eg. use https://github.com/PiotrRaszkowski/dockereo) and set up a dedicated database and user/pass.
1. Build image, Java 21 is required:
```shell
$ ./gradlew bootBuildImage
```
1. Create docker/.env file and provide the following configuration:
- mongo db properties
- receiver mail configuration
- sender mail configuration
```env
SPRING_APPLICATION_JSON='{
    "spring.data.mongodb.host": "mongo",
    "spring.data.mongodb.port": 27017,
    "spring.data.mongodb.database": "b2bOrganizer",
    "spring.data.mongodb.username": "b2bOrganizer",
    "spring.data.mongodb.password": "<YOUR NONGO DB PASSWORD>",
    "pro.b2organizer.mail.receiver.protocol": "imaps",
    "pro.b2organizer.mail.receiver.host": "<RECEIVER MAIL HOST>",
    "pro.b2organizer.mail.receiver.port": <RECEIVER MAIL PORT>,
    "pro.b2organizer.mail.receiver.folder": "INBOX",
    "pro.b2organizer.mail.receiver.username": "<RECEIVER MAIL USERNAME>",
    "pro.b2organizer.mail.receiver.password": "<RECEIVER MAIL PASSWORD>",
    "spring.mail.host": "<SENDER MAIL HOST>",
    "spring.mail.port": <SENDER MAIL PORT>,
    "spring.mail.username": "<SENDER MAIL USERNAME>",
    "spring.mail.password": "<SENDER MAIL PASSWORD>",
    "spring.mail.protocol": "smtp",
    "spring.mail.properties.mail.smtp.starttls.enable": true,
    "spring.mail.default-encoding": "UTF-8",
    "jwt.secret": "<JWT TOKEN SECRET>",
    "jwt.expirationInMillis": 900000
}'
```
1. Run:
```shell
$ docker compose up -d
```


