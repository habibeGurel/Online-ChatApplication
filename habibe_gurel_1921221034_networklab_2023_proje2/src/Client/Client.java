/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Client;

/**
 * Project 2
 * @author Habibe Gurel 1921221034
 */

import Message.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import static Client.Client.sInput;
import habibe_gurel_1921221034_networklab_2023_proje2.Login;
import habibe_gurel_1921221034_networklab_2023_proje2.chat;
import static habibe_gurel_1921221034_networklab_2023_proje2.chat.messageArr;

/**
 *
 * @author INSECT
 */
// serverdan gelecek mesajları dinleyen thread
class Listen extends Thread {
    
    public void run() {
        //soket bağlı olduğu sürece dön
        while (Client.socket.isConnected()) {
            try {
                //mesaj gelmesini bloking olarak dinyelen komut
                Message received = (Message) (sInput.readObject());
                //mesaj gelirse bu satıra geçer
                //mesaj tipine göre yapılacak işlemi ayır.
                switch (received.type) {
                    case Name:
                        String newUser = received.content.toString();//gelen clientin ismi
                        chat.mychat.peopleListModel.addElement(newUser);
                        break;
                    case RivalConnected:
                        String name = received.content.toString();
                        break;
                    case Connect:
                        String prev = received.content.toString();
                        chat.mychat.peopleListModel.addElement(prev);
                        break;
                    case CreateRoom:
                        String room = received.content.toString();
                        chat.mychat.roomListModel.addElement(room);
                        break;
                    case GetRooms:
                        String newRoom = received.content.toString();
                        chat.mychat.roomListModel.addElement(newRoom);
                        break;
                    case SendMessage:
                        //Game.ThisGame.txt_receive.setText(received.content.toString());
                        if (chat.mychat.control == 1) {
                            String senderName = received.content.toString();
                            int cnt = 0;//control
                            for (int i = 0; i < messageArr.size(); i++) {
                                if (i % 2 == 0 && messageArr.get(i).equals(senderName)) {
                                    cnt = 1;//kisi var
                                }
                            }
                            if (cnt == 0) {//kisi yoksa
                                messageArr.add(senderName);
                                messageArr.add("");
                            }
                            chat.mychat.control = 2;
                            chat.mychat.senderPerson = senderName;
                        } else if (chat.mychat.control == 2) {//mesaj ekleme
                            String message = received.content.toString();
                            for (int i = 0; i < messageArr.size(); i++) {
                                if (i % 2 == 0 && messageArr.get(i).equals(chat.mychat.senderPerson)) {
                                    String oldmsg = messageArr.get(i + 1);
                                    oldmsg = oldmsg + message;
                                    System.out.println(message);
                                    messageArr.set(i + 1, oldmsg);
                                }
                            }
                            if (chat.mychat.lbl_person.getText().equals(chat.mychat.senderPerson)) {
                                for (int i = 0; i < messageArr.size(); i++) {
                                    if (i % 2 == 0 && messageArr.get(i).equals(chat.mychat.senderPerson)) {
                                        chat.mychat.txtArea_chat.setText(messageArr.get(i + 1));//text area guncellenir
                                    }
                                }
                            }
                            chat.mychat.control=1;
                        }
                        break;
                    case SendRoomMessage:
                        String msg3 = received.content.toString();
                        chat.mychat.txtArea_roomMessages.setText(msg3);//gelen mesaji txt areaya atar
                        break;
                    case GetRoomMessage:
                        String message = received.content.toString();
                        chat.mychat.txtArea_roomMessages.setText(message);
                        break;
                    case Bitis:
                        for (int i = 0; i < chat.mychat.peopleListModel.size(); i++) {
                            if(chat.mychat.peopleListModel.get(i).equals(received.content.toString())){
                                chat.mychat.peopleListModel.remove(i);
                            }
                        }
                        break;
                }
                
            } catch (IOException ex) {
                
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                //Client.Stop();
                break;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                //Client.Stop();
                break;
            }
        }
    }
}

public class Client {

    public static Socket socket;//her clientin bir socketi olacak
    public static ObjectInputStream sInput;//veri alacak nesne
    public static ObjectOutputStream sOutput;//veri gonderecek nesne
    public static Listen listenMe;//serveri dinleyen listen thread
    
    public static void Start(String ip, int port) {
        try {
            // Client Soket nesnesi
            Client.socket = new Socket(ip, port);
            Client.Display("Servera bağlandı");
            // input stream
            Client.sInput = new ObjectInputStream(Client.socket.getInputStream());
            // output stream
            Client.sOutput = new ObjectOutputStream(Client.socket.getOutputStream());
            Client.listenMe = new Listen();
            Client.listenMe.start();

            Message msg = new Message(Message.Message_Type.Name);//ilk mesaj isim
            msg.content = Login.user.txt_username.getText();//Sclient name e geldi
            Client.Send(msg);//servera yollar
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //client durdurma fonksiyonu
    public static void Stop() {
        try {
            if (Client.socket != null) {
                Client.listenMe.stop();
                Client.socket.close();
                Client.sOutput.flush();
                Client.sOutput.close();
                
                Client.sInput.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static void Display(String msg) {
        System.out.println(msg);
    }

    //mesaj gönderme fonksiyonu
    public static void Send(Message msg) {
        try {
            Client.sOutput.writeObject(msg);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
