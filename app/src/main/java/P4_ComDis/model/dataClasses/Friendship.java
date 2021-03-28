package P4_ComDis.model.dataClasses;

public class Friendship {
    private String userSender;
    private String userReceived;
    private boolean confirmedFriendship;
    
    public Friendship(String userSender, String userReceived, boolean confirmedFriendship) {
        this.userSender = userSender;
        this.userReceived = userReceived;
        this.confirmedFriendship = confirmedFriendship;
    }
    
    public String getUserSender() {
        return userSender;
    }
    
    public boolean isConfirmedFriendship() {
        return confirmedFriendship;
    }
    
    public void setConfirmedFriendship(boolean confirmedFriendship) {
        this.confirmedFriendship = confirmedFriendship;
    }
    
    public String getUserReceived() {
        return userReceived;
    }
    
    public void setUserReceived(String userReceived) {
        this.userReceived = userReceived;
    }
    
    public void setUserSender(String userSender) {
        this.userSender = userSender;
    }
}
