Gardening Manager development plateform is processed maven
This short documentation explain how to configure the developer environment with Eclipse, Android ADT and Maven

# Quick step installation guide
1. git clone https://github.com/artmoni/gardening-manager-android
2. Get the Android SDK http://developer.android.com/sdk/index.html
3. Configure your Android environment
4. git clone https://github.com/artmoni/nuxeo-android.git
	* mvn clean install
5. Compile gardening manager with Maven

# Configure your Android environment
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

## Configure your GNU/Linux environment
http://source.android.com/source/initializing.html

## On ubuntu 12.04 x86_64
    $ sudo apt-get install git-core gnupg flex bison gperf build-essential \
      zip curl libc6-dev libncurses5-dev:i386 x11proto-core-dev \
      libx11-dev:i386 libreadline6-dev:i386 libgl1-mesa-glx:i386 \
      libgl1-mesa-dev g++-multilib mingw32 openjdk-6-jdk tofrodos \
      python-markdown libxml2-utils xsltproc zlib1g-dev:i386
    $ sudo ln -s /usr/lib/i386-linux-gnu/mesa/libGL.so.1 /usr/lib/i386-linux-gnu/libGL.so
    $ apt-get install lib32z1-dev bison flex lib32ncurses5-dev libx11-dev gperf g++-multilib
    
# Compile gardening manager with Maven 
Project https://github.com/artmoni/gardening-manager-maven is loaded automatically in pom.xml

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
# Eclipse: Configure Developer plateform
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

# WEB 
	https://www.versioneye.com/user/projects/51cb703566ec030002008baf
	https://www.ohloh.net/p/gardening-manager
	http://translate.gardening-manager.com/
	