# quarkus-tls-demo
Quarkus mutual Transport Layer Security (TLS) example - Truststore and Keystore configuration

# TLS

> Transport Layer Security (TLS) is a cryptographic protocol designed to provide communications security over a computer network. The protocol is widely used in applications such as email, instant messaging, and voice over IP, but its use in securing HTTPS remains the most publicly visible.

> Transport Layer Security (TLS) certificates—most commonly known as SSL, or digital certificates—are the foundation of a safe and secure internet. TLS/SSL certificates secure internet connections by encrypting data sent between your browser, the website you’re visiting, and the website server. They ensure that data is transmitted privately and without modifications, loss or theft.

# Certificate and Trustore generation


## Server Certificate 
```
keytool -genkeypair -storepass server-password -keyalg RSA -keysize 2048 -dname "CN=server" -alias server -ext "SAN:c=DNS:localhost,IP:127.0.0.1" -validity 365000 -keystore server/server.keystore
```

## Client "A" Certificate 
```
keytool -genkeypair -storepass client-password -keyalg RSA -keysize 2048 -dname "CN=client" -alias client -ext "SAN:c=DNS:localhost,IP:127.0.0.1" -validity 365000 -keystore client/client.keystore
```

## Client "B" Certificate 
```
keytool -genkeypair -storepass client-b-password -keyalg RSA -keysize 2048 -dname "CN=client" -alias client-b -ext "SAN:c=DNS:localhost,IP:127.0.0.1" -validity 365000 -keystore client/client-b.keystore
```

## Clients that are authorized to access server
```
keytool -genkeypair -storepass authorized-clients-password -keyalg RSA -keysize 2048 -dname "CN=client" -alias authorized-clients -ext "SAN:c=DNS:localhost,IP:127.0.0.1" -validity 365000 -keystore authorized-clients.jks
```
## Command to add a JKS file into an existing JKS file

keytool -importkeystore -srckeystore client/client.keystore -destkeysore authorized-clients.jks

keytool -importkeystore -srckeystore client/client.keystore -srcstorepass client-password -destkeystore authorized-clients.jks -deststorepass authorized-clients-password

keytool -importkeystore -srckeystore client/client-b.keystore -srcstorepass client-b-password -destkeystore authorized-clients.jks -deststorepass authorized-clients-password


## Create Client Trust Store 

```
cp server/server.keystore client/client.truststore
```

## Create Server Trust Store 

```
cp client/client.keystore server/server.truststore
```



# Test cenarios

## Server with TLS Key Store configured and Client without the Trust Store configured

When we enable TLS on the server side:
```properties
# Demand https request
quarkus.http.insecure-requests=redirect

# Configure https port
quarkus.http.ssl-port=8445

# Server Certificate
quarkus.http.ssl.certificate.key-store-file=./server.keystore
quarkus.http.ssl.certificate.key-store-password=server-password
```

### From Quarkus Client

And try to access this endpoint from the client without the Trust Store configuration, we receive this error:
```
jakarta.ws.rs.ProcessingException: javax.net.ssl.SSLHandshakeException: Failed to create SSL connection
...
Caused by: javax.net.ssl.SSLHandshakeException: Failed to create SSL connection
        at io.vertx.core.net.impl.ChannelProvider$1.userEventTriggered(ChannelProvider.java:127)
        ... 25 more
Caused by: javax.net.ssl.SSLHandshakeException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
...
Caused by: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
...
Caused by: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
```

### From curl
```
$ curl -v https://localhost:8445
*   Trying 127.0.0.1:8445...
* Connected to localhost (127.0.0.1) port 8445 (#0)
* ALPN, offering h2
* ALPN, offering http/1.1
*  CAfile: /etc/ssl/certs/ca-certificates.crt
*  CApath: /etc/ssl/certs
* TLSv1.0 (OUT), TLS header, Certificate Status (22):
* TLSv1.3 (OUT), TLS handshake, Client hello (1):
* TLSv1.2 (IN), TLS header, Certificate Status (22):
* TLSv1.3 (IN), TLS handshake, Server hello (2):
* TLSv1.2 (IN), TLS header, Finished (20):
* TLSv1.2 (IN), TLS header, Supplemental data (23):
* TLSv1.3 (IN), TLS handshake, Encrypted Extensions (8):
* TLSv1.3 (IN), TLS handshake, Certificate (11):
* TLSv1.2 (OUT), TLS header, Unknown (21):
* TLSv1.3 (OUT), TLS alert, unknown CA (560):
* SSL certificate problem: self-signed certificate
* Closing connection 0
curl: (60) SSL certificate problem: self-signed certificate
More details here: https://curl.se/docs/sslcerts.html

curl failed to verify the legitimacy of the server and therefore could not
establish a secure connection to it. To learn more about this situation and
how to fix it, please visit the web page mentioned above.
```

## Server with TLS and Client without the Trust Store configured


## Server with TLS Key Store configured and Client with the Trust Store configured


## Server with TLS Key Store configured and demanding identification from the client

```
# Demand https request
quarkus.http.insecure-requests=redirect

# Configure https port
quarkus.http.ssl-port=8445

# Server Certificate
quarkus.http.ssl.certificate.key-store-file=./server.keystore
quarkus.http.ssl.certificate.key-store-password=server-password

# Server Client Authentication
quarkus.http.ssl.client-auth=required
quarkus.http.ssl.certificate.trust-store-file=./server.truststore
quarkus.http.ssl.certificate.trust-store-password=client-password
```


# Credits

https://quarkus.io/guides/security-authentication-mechanisms-concept#mutual-tls

https://quarkus.io/blog/quarkus-mutual-tls/

https://quarkus.io/guides/http-reference

https://en.wikipedia.org/wiki/Transport_Layer_Security

https://www.digicert.com/tls-ssl/tls-ssl-certificates#:~:text=Transport%20Layer%20Security%20(TLS)%20certificates,visiting%2C%20and%20the%20website%20server.

