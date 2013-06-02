--------------------------------------------------------
---- environnement ----------------------------------------
--------------------------------------------------------
*** ANDROID Environnement
export ANDROID_HOME=~/Projets/Android/android-sdk-linux/
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
workspace/android-sdk-linux/tools/android update sdk --no-ui

*** Linux Android initialization
http://source.android.com/source/initializing.html


*** On ubuntu 12.04 x86_64
$ sudo apt-get install git-core gnupg flex bison gperf build-essential \
  zip curl libc6-dev libncurses5-dev:i386 x11proto-core-dev \
  libx11-dev:i386 libreadline6-dev:i386 libgl1-mesa-glx:i386 \
  libgl1-mesa-dev g++-multilib mingw32 openjdk-6-jdk tofrodos \
  python-markdown libxml2-utils xsltproc zlib1g-dev:i386
$ sudo ln -s /usr/lib/i386-linux-gnu/mesa/libGL.so.1 /usr/lib/i386-linux-gnu/libGL.so
$ apt-get install   lib32z1-dev bison flex lib32ncurses5-dev libx11-dev gperf g++-multilib

*** Eclipse Addons
https://dl-ssl.google.com/android/eclipse/
http://subclipse.tigris.org/update_1.8.x

--------------------------------------------------------
--- ECLIPSE MAVEN INTEGRATION
--------------------------------------------------------
Use android-m2e
http://blog.xebia.fr/2010/03/23/maven-et-android-comment-utiliser-le-plugin/

1/ INCLUDE EXTERNALS IN MAVEN REPOSITORY
mvn install:install-file -Dfile=GoogleAnalytics/libGoogleAnalytics.jar -Dversion=1.4.2 -DartifactId=analytics -DgroupId=com.google.android.analytics -Dpackaging=jar
mvn install:install-file -Dfile=GoogleAdMobAdsSdkAndroid-4.3.1/GoogleAdMobAdsSdk-4.3.1.jar -Dversion=4.3.1 -DartifactId=admob -DgroupId=com.google.android.admob -Dpackaging=jar
mvn install:install-file -Dfile=JakeWharton-ActionBarSherlock-e5c2d1c/library/bin/actionbarsherlock.jar -Dversion=4.2.0 -DartifactId=actionbarsherlock -DgroupId=com.actionbarsherlock -Dpackaging=jar
mvn install:install-file -Dfile=simple-xml-2.6.4/jar/simple-xml-2.6.4.jar -Dversion=2.6.4 -DartifactId=org.simpleframework -DgroupId=simple-xml -Dpackaging=jar
mvn install:install-file -Dfile=opensocial-1.0.jar -Dversion=1.0 -DartifactId=opensocial -DgroupId=org.opensocial -Dpackaging=jar

2/ INCLUDE ANDROID SDK IN MAVEN REPOSITORY
With android SDK Manager, install those tools:
- Android 4.0.3 API (or more recent) 
--SDK Plateforme
-- ARM EABI V7
-- Google API
-Extras (All)

git clone https://github.com/mosabua/maven-android-sdk-deployer
-export ANDROID_HOME=/home/sfleury/Projets/Android/android-sdk-linux
-mvn install -P 4.0.3

git clone https://github.com/JakeWharton/ActionBarSherlock
link the library project with your project

3/ COMPILATION lifecycle
mvn install --settings settings.xml -P env-dev

--------------------------------------------------------
------------- COMPILATION PROCESS 
--------------------------------------------------------
$ mvn clean install android:deploy android:run
 
--------------------------------------------------------
------------- RELEASE PROCESS 
--------------------------------------------------------
verify package certificate
--jarsigner -verbose -sigalg MD5withRSA -digestalg SHA1 -certs -verify target/gardening-manager-0.14.apk
delete meta-inf from apk released package 
sign with release certificat
--jarsigner -sigalg MD5withRSA -digestalg SHA1 -keystore '$KEYSTORE_DIR/keystore' -storepass 'STORE_PASS' -keypass 'KEY_PASS' $PROJECT_HOME/target/gardening-manager-0.14.apk artmonimobile
--zipalign  4 target/gardening-manager-0.14.apk target/gardening-manager-0.14-signed.apk

--------------------------------------------------------
------------- SQLITE
--------------------------------------------------------
cd /data/data/org.gots/databases/
sqlite3 gots.db

.tables
.header on
.mode column

------------- PROCESS IMAGE
mogrify -resize 100x100 veget_*.jpg

-################ JARSIGNER
http://afleurentdidier.wordpress.com/2010/02/21/comment-publier-son-application-sur-landroid-market/

keytool -genkeypair -alias artmonimobile -validity 10000

Tapez le mot de passe du Keystore :  
Mot de passe de Keystore trop court, il doit compter au moins 6 caractères
Tapez le mot de passe du Keystore :  
Ressaisissez le nouveau mot de passe : 
Quels sont vos prénom et nom ?
  [Unknown] :  Sébastien FLEURY
Quel est le nom de votre unité organisationnelle ?
  [Unknown] :  Artmoni mobile
Quelle est le nom de votre organisation ?
  [Unknown] :  Artmoni
Quel est le nom de votre ville de résidence ?
  [Unknown] :  Boissy l'Aillerie
Quel est le nom de votre état ou province ?
  [Unknown] :  France
Quel est le code de pays à deux lettres pour cette unité ?
  [Unknown] :  FR
Est-ce CN=Sébastien FLEURY, OU=Artmoni mobile, O=Artmoni, L=Boissy l'Aillerie, ST=France, C=FR ?
  [non] :  oui

Spécifiez le mot de passe de la clé pour <artmonimobile>
	(appuyez sur Entrée s'il s'agit du mot de passe du Keystore) : 
-

###############################
#### NUXEO INTEGRATION
###############################
mvn install:install-file -Dfile=../nuxeo-android/nuxeo-automation-thin-client/target/nuxeo-automation-thin-client-2.0-SNAPSHOT.jar -Dversion=2.0-SNAPSHOT -DartifactId=nuxeo-automation-client -DgroupId=org.nuxeo.ecm.automation -Dpackaging=jar
mvn install:install-file -Dfile=../nuxeo-android/nuxeo-android-connector/target/nuxeo-android-connector-2.0-SNAPSHOT.jar -Dversion=2.0-SNAPSHOT -DartifactId=nuxeo-android-connector -DgroupId=org.nuxeo.android -Dpackaging=jar

# run nuxeo shell
java -cp nuxeo-shell-5.6.jar org.nuxeo.shell.Main
connect http://localhost:8080/nuxeo/site/automation -u Administrator


### Authentication
## 1) USER CREATION
<- ServerSide: User creation by admin or openid
<- org.nuxeo.ecm.platform.oauth2.openid.auth.OpenIDConnectAuthenticator => OAuth2 authentication, user creation
<- ++ Generate password, send email with android specific URI (no manual copy of password)
<- ++ Generate token and send email with android specific URI (step 2 useless)

## 2) TOKEN REQUEST
-> ClientSide connect with user/password to request token 
-> org.gots.ui.LoginActivity.request_token(boolean)
<- org.nuxeo.ecm.platform.ui.web.auth.plugins.BasicAuthenticator => user authenticated with password
<- org.nuxeo.ecm.tokenauth.servlet.TokenAuthenticationServlet => send token for user
-> token stored on device

## 3) TOKEN USE
-> client side, connect with token
-> org.gots.utils.TokenRequestInterceptor => send request with token and deviceid
<- org.nuxeo.ecm.platform.ui.web.auth.token.TokenAuthenticator => check token and log the request

## 1bis) USER CREATION REQUEST
-> ++ Client side send a request for a user creation for a given user (email)
<- ++ Server creates user, generate token and send email with android specific URI (step 1 useless)
org.gots.server.auth.RequestAuthenticationTokenByEmail
-> ++ Client side, click email, open Gardening, token request for device
<- org.gots.server.auth.TemporaryTokenAuthenticator userPrincipal (replace basic auth)
<- org.nuxeo.ecm.tokenauth.servlet.TokenAuthenticationServlet => send token for user
-> store token (and connect to validate user creation)
<- ++ If created user is not validated within a given delay, the user is deleted

