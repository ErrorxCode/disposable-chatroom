# Disposable chatroom ~ Use-n-throw chating library

The disposable chatroom is a library from which you can build a fully anonymous virtual room where your friends or random person can join to talk. Talking about **privacy** doesn't make sense since the room is disposable However, this more **depends on the app** made using this library.


## **NOTE
**The project has been discontinued due to the [changes](https://help.heroku.com/RSBRUH58/removal-of-heroku-free-product-plans-faq#:~:text=For%20non%2DEnterprise%20users%2C%20free,will%20be%20converted%20to%20mini%20.) in heroku free tear**



## Implementation

#### Gradle

Add it to your root *build.gradle* at the end of repositories:

```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

Add the dependency

```groovy
dependencies {
	 implementation 'com.github.ErrorxCode:disposible-chatroom:v1.0'
}
```

#### Maven

Add the repository:

```markup
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```

Add the dependency:

```markup
	<dependency>
	    <groupId>com.github.ErrorxCode</groupId>
	    <artifactId>disposible-chatroom</artifactId>
	    <version>v1.0</version>
	</dependency>
```



## Usage

The usage is so simple. Just create a room and the rest of the members will join it. 

#### Creating room

```java
try {
    Chatroom room = Chatroom.create("test-room","rahil");
} catch (Exception e) {
    e.printStackTrace();  // room name or user name already exists.
}
```

This will create a room with the name *test-room* and yourself in it with the name *rahil*. Now other members can join it using `Chatroom.join()` method. **The room will be automatically closed when there is no member in it**.



#### Joining room

```java
try {
    Chatroom room = Chatroom.join("test-room", "user1");
} catch (Exception e) {
    e.printStackTrace();  // No such room, or user already exists
}
```

This way, others can join the room created with **test-room** name. The room creator does not need to join the room since it will automatically be joined when the room is opened.



**Note : Sometime, creating or joining room may take long (upto 10 seconds)**



#### Listening events

```java
room.addMessageListener(new ChatRoomEventListener() {
    @Override
    public void onMessage(String from, String message) {
        // someone has sent a new message
    }
    
    @Override
    public void onImage(String from, String image) {
       // someone has shared a image
    }
    
    @Override
    public void onLeave(String user) {
      // someone left the room
    }
    
    @Override
    public void onJoin(String user) {
      // someone join the room
    }
});
```



#### Sending messages

```java
room.broadcastMessage("Hello, world!");
room.sendImage("base64-image-data");
room.sendPM("user2", "Hello, user2! Here is a Top secret for you");
room.reply("How are you?", "I'm fine, thanks!");
```

`sendPM` sends the message privately to the user. This means that it will not be visibled to others.

The `reply()` method's has a message body convection. It send's a message in the following formate :-

```
username: [REPLY:message_to_reply] : your_reply
```

The developer should impliment a custom reply GUI mechanism using this convection.



#### Admin functions

```java
room.kick("user2");
room.leave();
room.ping();
room.close();
```

***Note :*** *To perform any of the operation* (except *ping*), **you must be the creator of the room** (also know as admin). 





## That's sit

That's all for the library, now its time to impliment thin in your app and build awesome apps. If you liked my hard work, you can support me by giving this repo a **star**. 

Wan't to build your own library for another langunge ? Check out  [Clorabase chatroom API](https://github.com/ErrorxCode/Clorabase-APIs/wiki/Chatroom-API-Reference).
