/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Message;

/**
 * Project 2
 * @author Habibe Gurel 1921221034
 */
public class Message implements java.io.Serializable {

    //mesaj tipleri enum 
    public static enum Message_Type {
        None, Name, Connect, CreateRoom,RivalConnected, SendMessage,SendRoomMessage,GetRoomMessage, Bitis, GetRooms,
    }
    //mesajın tipi
    public Message_Type type;
    //mesajın içeriği obje tipinde ki istenilen tip içerik yüklenebilsin
    public Object content;

    public Message(Message_Type t) {
        this.type = t;
    }

}
