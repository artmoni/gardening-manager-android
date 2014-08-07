Gardening Manager development plateform is processed maven
This short documentation explain how to configure the developer environment with Eclipse, Android ADT and Maven

# Quick step installation guide
1. git clone https://github.com/artmoni/gardening-manager-android
2. Get the Android SDK http://developer.android.com/sdk/index.html
3. Configure your Android environment
4. git clone https://github.com/mosabua/maven-android-sdk-deployer
5. git clone https://github.com/artmoni/nuxeo-android.git
	* mvn clean install
6. Compile gardening manager with maven

# ANDROID
## Install Android
    1. export ANDROID_HOME=/path/to/android-sdk/
    2. export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
    3. android update sdk --no-ui
    
With android SDK Manager, install those tools:

	- Android 4.0.3 API (or later)
	- SDK Plateform
	- ARM EABI V7
	- Google API
	- Extras (All)
	- Google Repository (for maven local repository)

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

# ECLIPSE
## Install Android Developer Tool (ADT)
Install ADT: https://dl-ssl.google.com/android/eclipse/

## Maven in eclipse with m2e 
Use android-m2e to mavenize the project and manage dependencies inside your eclipse environment
http://blog.xebia.fr/2010/03/23/maven-et-android-comment-utiliser-le-plugin/

### Android AppCompat Compatibility version for device < 4.0
Import AppCompat Android project from SDK extras directory
    File->Import (android-sdk\extras\android\support\v7). Choose "appcompat"
    Project-> properties->Android. In the section library "Add" and choose "appCompat"
    That is all!
    
# MAVEN 
Project https://github.com/artmoni/gardening-manager-maven is load automatically in pom.xml

## INCLUDE ANDROID SDK IN MAVEN REPOSITORY
Use maven-android-sdk-deployer to feed the local repository:

    $ git clone https://github.com/mosabua/maven-android-sdk-deployer
    $ cd maven-android-sdk-deployer
    $ export ANDROID_HOME=/path/to/android-sdk/
    $ mvn install -P 4.2

## Compile

    $ mvn clean install [--settings settings.xml] [-P env-dev]

## Deploy 

    $ mvn android:deploy android:run [-Dandroid.sdk.path=/path/to/android-sdk/]
 
## Release 
	$ mvn --settings settings.xml -Prelease clean install
	$ mvn --settings settings.xml -Prelease release:prepare android:manifest-update

### Verify package certificate

    $ jarsigner -verbose -sigalg MD5withRSA -digestalg SHA1 -certs -verify target/gardening-manager-*.apk

### Delete meta-inf from apk released package

Sign with release certificate

    $ jarsigner -sigalg MD5withRSA -digestalg SHA1 -keystore '$KEYSTORE_DIR/keystore' -storepass 'STORE_PASS' -keypass 'KEY_PASS' $PROJECT_HOME/target/gardening-manager-*.apk artmonimobile
    $ zipalign  4 target/gardening-manager-0.14.apk target/gardening-manager-*-signed.apk


#########################


# SQLITE

    $ cd /data/data/org.gots/databases/
    $ sqlite3 gots.db
    .tables
    .header on
    .mode column

# PROCESS IMAGE

    $ mogrify -resize 100x100 veget_*.jpg

# NUXEO INTEGRATION

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
	