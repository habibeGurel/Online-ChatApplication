/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Message;

/**
 *
 * @author Habibe
 */
public class Message implements java.io.Serializable {
    public static enum Message_Type {None, Name, Connect, CreateRoom,RivalConnected, SendMessage,SendRoomMessage,GetRoomMessage, Bitis,Start,GetRooms,}
    
    public Message_Type type;
    public Object content;
    public Message(Message_Type t)
    {
        this.type=t;
    }
}
