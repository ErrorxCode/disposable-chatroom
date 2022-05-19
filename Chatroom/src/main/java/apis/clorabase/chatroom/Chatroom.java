package apis.clorabase.chatroom;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * A java library for creating a disposable chatting app. This chatroom is a simple group that allows users to
 * send messages to each other. It's like a invisible anonymous room for chatting which get destroyed when all
 * the members leaves. The chatroom is built using the WebSocket protocol and uses Clorabase chatroom API.
 */
public class Chatroom {
    private final WebSocket socket;
    private static String adminId;
    private static String roomId;
    private static final HttpClient client = HttpClient.newHttpClient();

    private Chatroom(WebSocket socket) throws WebSocketException {
        this.socket = socket.connect();
    }

    /**
     * Creates a new chatroom & joins it with the given name.
     * @param roomName The name of the chatroom.
     * @param userName The name of the user creating the room.
     * @throws Exception If the room already exists or the user is already in the room.
     */
    public static Chatroom create(String roomName, String userName) throws Exception {
        var request = HttpRequest.newBuilder(URI.create("https://clorabase.herokuapp.com/chat/create?room=" + roomName))
                .timeout(Duration.ofSeconds(20))
                .GET()
                .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201){
            adminId = response.body();
            roomId = roomName;
            return join(roomName,userName);
        } else {
            throw new Exception("Room with this name already exists");
        }
    }

    /**
     * Joins the chatroom with the given roomName.
     * @param roomName The name of the chatroom.
     * @param userName The name of the user joining the room.
     * @return The chatroom object.
     * @throws Exception If the room doesn't exist or the user is already in the room.
     */
    public static Chatroom join(String roomName,String userName) throws Exception {
        var query = "?room=" + roomName + "&user=" + userName;
        var socket = new WebSocketFactory().createSocket("wss://clorabase.herokuapp.com/chat/join" + query,10*1000);
        return new Chatroom(socket);
    }

    /**
     * Adds a listener to the chatroom. The listener will be notified when a message is received.
     * @param listener The listener to be added.
     */
    public void addMessageListener(ChatRoomEventListener listener){
        socket.addListener(new WebSocketAdapter(){
            @Override
            public void onTextMessage(WebSocket websocket, String text) throws Exception {
                var sender = text.split(":")[0];
                var message = text.split(":")[1].trim();
                if (message.startsWith("[IMAGE]")) listener.onImage(sender,message.substring(8));
                else if (message.equals("Left the room")) listener.onLeave(sender);
                else if (message.equals("Joined the room")) listener.onJoin(sender);
                else listener.onMessage(sender,message);
            }
        });
    }

    /**
     * Leaves the chatroom
     */
    public void leave(){
        socket.disconnect(1001, "User left");
    }

    /**
     * Kicks out the user with the given userName from the chatroom.
     * @param userName The name of the user to be kicked out.
     * @return true if the success, false if failed. (If the user is not in the chatroom or you are not the admin.)
     */
    public boolean kick(String userName){
        var link = "https://clorabase.herokuapp.com/chat/" + roomId + "/delete?user=" + userName + "&admin=" + adminId;
        var request = HttpRequest.newBuilder(URI.create(link)).GET().build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            return true;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    /**
     * Closes the chatroom. All the members will be kicked out.
     * @return true if the success, false if you are not the admin.
     */
    public boolean close(){
        var link = "https://clorabase.herokuapp.com/chat/" + roomId + "/delete?admin=" + adminId;
        var request = HttpRequest.newBuilder(URI.create(link)).GET().build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            return true;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    /**
     * Broadcasts message to all the members of the chatroom.
     * @param message The message to be sent.
     */
    public void broadcastMessage(String message){
        socket.sendText(message);
    }

    /**
     * Replies to the message quoted with the given message
     * @param message The message to which the reply is to be sent.
     * @param reply The reply to the message.
     */
    public void reply(String message,String reply){
        socket.sendText("[REPLY:" + message + "] : " + reply);
    }

    /**
     * Sends a private message to the user with the given userName.
     * @param userName The name of the user to whom the message is to be sent.
     * @param message The message to be sent.
     */
    public void sendPM(String userName,String message){
        socket.sendText("[PM:" + userName + "] :" + message);
    }

    /**
     * Broadcast a image in the room in base64 format.
     * @param base64 The base64 string of the image.
     */
    public void sendImage(String base64){
        socket.sendText("[IMAGE] " + base64);
    }

    /**
     * Pings the server to keep the connection alive. This method should only be called
     * if the room is closing and the users are taking long time to message.
     */
    public void ping(){
        socket.sendPing();
    }
}