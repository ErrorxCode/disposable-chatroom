package apis.clorabase.chatroom;

public interface ChatRoomEventListener {
    void onMessage(String from,String message);
    void onImage(String from,String image);
    void onLeave(String user);
    void onJoin(String user);
}
