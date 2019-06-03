ManyWho Box Service
===================

> This service is currently in development, and not yet recommended for use in production environments

[![Build Status](https://travis-ci.org/manywho/service-box.svg?branch=develop)](https://travis-ci.org/manywho/service-box)

This service allows you to integrate your Flows with [Box](https://www.box.com).

## Running

The service is compatible with Heroku, and can be deployed by clicking the button below:

[![Deploy to Heroku](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy?template=https://github.com/manywho/service-box/tree/develop)

To build the service, you will need to have Apache Ant, Maven 3 and a Java 8 implementation installed, OpenJDK or Oracle JDK with Java Cryptography Extension (JCE) Unlimited

You will need to generate a configuration file for the service by running the provided `build.xml` script with Ant, and 
passing in a valid URL to a Redis instance:

```bash
$ ant -Dsecure.privateKey=xxx \
-Dsecure.privateKeyPassword=xxx \
-Doauth2.contentApi.clientId=xxx \
-Doauth2.contentApi.clientSecret=xxx \
-Doauth2.developerEdition.clientId=xxx \
-Doauth2.developerEdition.clientSecret=xxx \
-Dwebhook.signature.primary.key=xxx \
-Dwebhook.signature.secondary.key=xxx \
-Dassignment.flowId=xxx \
-Dassignment.flowVersionId=xxx \
-Dassignment.tenantId=xxx \
-Dveryfication.key=xxx \
-Dredis.url=xxx \

```

**Note:** `secure.privateKey` and `verification.key` are Base64 encoded

For information of how to create the verification.key please check the test encryptAndDecryptTest

Now you can build the runnable shaded JAR


##### Defaults

Running the following command will start the service listening on `0.0.0.0:8080/api/box/3`:

```bash
$ java -jar target/box-3.0-SNAPSHOT.jar
```

##### Custom Port

You can specify a custom port to run the service on by passing the `server.port` property when running the JAR. The
following command will start the service listening on port 9090 (`0.0.0.0:9090/api/box/3`):

```bash
$ java -Dserver.port=9090 -jar target/box-3.0-SNAPSHOT.jar
```

## Contributing

Contributions are welcome to the project - whether they are feature requests, improvements or bug fixes! Refer to 
[CONTRIBUTING.md](CONTRIBUTING.md) for our contribution requirements.

## License

This service is released under the [MIT License](http://opensource.org/licenses/mit-license.php).
