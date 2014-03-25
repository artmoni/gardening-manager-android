# Environment
## ANDROID Environment
    $ export ANDROID_HOME=/path/to/android-sdk/
    $ export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
    $ android update sdk --no-ui

## Linux Android initialization
http://source.android.com/source/initializing.html

## On ubuntu 12.04 x86_64
    $ sudo apt-get install git-core gnupg flex bison gperf build-essential \
      zip curl libc6-dev libncurses5-dev:i386 x11proto-core-dev \
      libx11-dev:i386 libreadline6-dev:i386 libgl1-mesa-glx:i386 \
      libgl1-mesa-dev g++-multilib mingw32 openjdk-6-jdk tofrodos \
      python-markdown libxml2-utils xsltproc zlib1g-dev:i386
    $ sudo ln -s /usr/lib/i386-linux-gnu/mesa/libGL.so.1 /usr/lib/i386-linux-gnu/libGL.so
    $ apt-get install lib32z1-dev bison flex lib32ncurses5-dev libx11-dev gperf g++-multilib

## Eclipse
Install ADT: https://dl-ssl.google.com/android/eclipse/

Use android-m2e 
http://blog.xebia.fr/2010/03/23/maven-et-android-comment-utiliser-le-plugin/

## INCLUDE EXTERNALS IN MAVEN REPOSITORY
Project https://github.com/artmoni/gardening-manager-maven is load automatically in pom.xml


Clone ActionBarSherlock to use its APK lib:

    $ git clone https://github.com/JakeWharton/ActionBarSherlock
    # Then link the library project with your project

## INCLUDE ANDROID SDK IN MAVEN REPOSITORY

With android SDK Manager, install those tools:

- Android 4.0.3 API (or later)
  - SDK Plateforme
  - ARM EABI V7
  - Google API
- Extras (All)

Use maven-android-sdk-deployer to feed the local repository:

    $ git clone https://github.com/mosabua/maven-android-sdk-deployer
    $ cd maven-android-sdk-deployer
    $ export ANDROID_HOME=/path/to/android-sdk/
    $ mvn install -P 4.2

# Build
## Compile

    $ mvn clean install [--settings settings.xml] [-P env-dev]

## Deploy 

    $ mvn android:deploy android:run [-Dandroid.sdk.path=/path/to/android-sdk/]
 
## Release 
	$ mvn --settings settings.xml -Penv-release,release clean install

### Verify package certificate

    $ jarsigner -verbose -sigalg MD5withRSA -digestalg SHA1 -certs -verify target/gardening-manager-*.apk

### Delete meta-inf from apk released package

Sign with release certificate

    $ jarsigner -sigalg MD5withRSA -digestalg SHA1 -keystore '$KEYSTORE_DIR/keystore' -storepass 'STORE_PASS' -keypass 'KEY_PASS' $PROJECT_HOME/target/gardening-manager-*.apk artmonimobile
    $ zipalign  4 target/gardening-manager-0.14.apk target/gardening-manager-*-signed.apk

# SQLITE

    $ cd /data/data/org.gots/databases/
    $ sqlite3 gots.db
    .tables
    .header on
    .mode column

# PROCESS IMAGE

    $ mogrify -resize 100x100 veget_*.jpg

# NUXEO INTEGRATION

TODO: deprecated part

    $ mvn install:install-file -Dfile=../nuxeo-android/nuxeo-automation-thin-client/target/nuxeo-automation-thin-client-2.0-SNAPSHOT.jar -Dversion=2.0-SNAPSHOT -DartifactId=nuxeo-automation-client -DgroupId=org.nuxeo.ecm.automation -Dpackaging=jar
    $ mvn install:install-file -Dfile=../nuxeo-android/nuxeo-android-connector/target/nuxeo-android-connector-2.0-SNAPSHOT.jar -Dversion=2.0-SNAPSHOT -DartifactId=nuxeo-android-connector -DgroupId=org.nuxeo.android -Dpackaging=jar

## run nuxeo shell

TODO: update

    $ java -cp /var/lib/nuxeo/server/lib/log4j-1.2.17.jar:/var/lib/nuxeo/server/client/nuxeo-shell-*.jar org.nuxeo.shell.Main
    $ connect http://localhost:8080/nuxeo/site/automation -u Administrator

# Work in progress

## Authentication
### USER CREATION

    <- ServerSide: User creation by admin or openid
    <- org.nuxeo.ecm.platform.oauth2.openid.auth.OpenIDConnectAuthenticator => OAuth2 authentication, user creation
    <- ++ Generate password, send email with android specific URI (no manual copy of password)
    <- ++ Generate token and send email with android specific URI (step 2 useless)

### TOKEN REQUEST

    -> ClientSide connect with user/password to request token 
    -> org.gots.ui.LoginActivity.request_token(boolean)
    <- org.nuxeo.ecm.platform.ui.web.auth.plugins.BasicAuthenticator => user authenticated with password
    <- org.nuxeo.ecm.tokenauth.servlet.TokenAuthenticationServlet => send token for user
    -> token stored on device

### TOKEN USE

    -> client side, connect with token
    -> org.gots.utils.TokenRequestInterceptor => send request with token and deviceid
    <- org.nuxeo.ecm.platform.ui.web.auth.token.TokenAuthenticator => check token and log the request

### alternate solution) USER CREATION REQUEST

    -> ++ Client side send a request for a user creation for a given user (email)
    <- ++ Server creates user, generate token and send email with android specific URI (step 1 useless)
    org.gots.server.auth.RequestAuthenticationTokenByEmail
    -> ++ Client side, click email, open Gardening, token request for device
    <- org.gots.server.auth.TemporaryTokenAuthenticator userPrincipal (replace basic auth)
    <- org.nuxeo.ecm.tokenauth.servlet.TokenAuthenticationServlet => send token for user
    -> store token (and connect to validate user creation)
    <- ++ If created user is not validated within a given delay, the user is deleted

# WEB 
	https://www.versioneye.com/user/projects/51cb703566ec030002008baf
	https://www.ohloh.net/p/gardening-manager
	http://translate.gardening-manager.com/
	