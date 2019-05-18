package dhbw.familymanager.familymanager.model;

public class ChatRoom {
    private String chatId;
    private String chatName;
    private String userId;
    private String familyId;

    public String getUserId() {
        return userId;
    }
    public String getFamilyId() {
        return familyId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ChatRoom() {

    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }
}
