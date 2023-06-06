/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server;

/**
 * Project 2
 * @author Habibe Gurel 1921221034
 */

import Message.Message;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SClient {

    int id;
    public String name = "NoName";
    Socket soket;
    ObjectOutputStream sOutput;
    ObjectInputStream sInput;
    //clientten gelenleri dinleme threadi
    Listen listenThread;
    //cilent eşleştirme thredi
    PairingThread pairThread;
    //rakip client
    SClient receiver;
    String availableRoom="";
    //eşleşme durumu
    public boolean paired = false;
    public int control = 1;
    public int control2=1;

    public SClient(Socket gelenSoket, int id) {
        this.soket = gelenSoket;
        this.id = id;
        try {
            this.sOutput = new ObjectOutputStream(this.soket.getOutputStream());
            this.sInput = new ObjectInputStream(this.soket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        //thread nesneleri
        this.listenThread = new Listen(this);
        this.pairThread = new PairingThread(this);

    }

    //client mesaj gonderme
    public void Send(Message message) {
        try {
            this.sOutput.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void FindClient(String clientName) {
        for (int i = 0; i < Server.Clients.size(); i++) {
            if (Server.Clients.get(i).name.equals(clientName)) {
                receiver = Server.Clients.get(i);
                break;
            }
        }
    }

    //client dinleme threadi
    //her clientin ayri bir dinleme threadi vardir
    class Listen extends Thread {

        SClient TheClient;

        //thread nesne almasi için yapici metod
        Listen(SClient TheClient) {
            this.TheClient = TheClient;
        }

        public void run() {
            //client bağlı olduğu sürece dönsün
            while (TheClient.soket.isConnected()) {
                try {
                    //mesajı bekleyen kod satırı
                    Message received = (Message) (TheClient.sInput.readObject());
                    //mesaj gelirse bu satıra geçer
                    //mesaj tipine göre işlemlere ayır
                    switch (received.type) {
                        case Name:
                            TheClient.name = received.content.toString();
                            // isim verisini gönderdikten sonra eşleştirme işlemine başla
                            for (int i = 0; i < Server.Clients.size(); i++) {
                                if (Server.Clients.get(i) != TheClient) {
                                    Server.Send(Server.Clients.get(i), received);//listeye ekler
                                }
                            }
                            break;
                        case Connect://kendinden onceki isimleri people listesinde gosterecek
                            for (int i = 0; i < Server.Clients.size(); i++) {
                                if (Server.Clients.get(i) != TheClient) {
                                    Message msg = new Message(Message.Message_Type.Connect);
                                    msg.content = Server.Clients.get(i).name;
                                    Server.Send(TheClient, msg);
                                }
                            }
                            for (int i = 0; i < Server.RoomList.size(); i++) {//kensinden onceki roomlari room listesinde gosterecek
                                Message msg = new Message(Message.Message_Type.GetRooms);
                                msg.content = Server.RoomList.get(i);
                                Server.Send(TheClient, msg);
                            }
                            break;
                        case CreateRoom://oda olusturacak
                            for (int i = 0; i < Server.Clients.size(); i++) {
                                if (Server.Clients.get(i) != TheClient) {
                                    Server.Send(Server.Clients.get(i), received);//kendisi haric tum clientlere olusturulan odanin adi yollanir
                                }
                            }
                            Server.RoomList.add(received.content.toString());
                            Server.RoomList.add("");//ilk bos mesaj atamasi
                            break;
                        case SendMessage://diger clienta mesaj gonderme islemi
                            if (control == 1) {//ismi yolla
                                String currentName = received.content.toString();
                                FindClient(currentName); //receiver
                                received.content = TheClient.name; //sender adi
                                Server.Send(TheClient.receiver, received); //receivera sender adini yollar 
                                control = 2;
                            } else if (control == 2) {//mesaji yolla
                                Server.Send(TheClient.receiver, received); //receivera mesaji yollar
                                control = 1;
                            }
                            break;
                        case SendRoomMessage://odada konusulan mesajları tum clientlara gonderme
                            if (control2 == 1) {//room adi yolla
                                String currentRoomName = received.content.toString();
                                control2 = 2;
                            } else if (control2 == 2) {//mesaji yolla
                                for (int i = 0; i < Server.RoomList.size(); i++) {
                                    if (i % 2 == 0 && Server.RoomList.get(i).equals(availableRoom)) {
                                        String msgRoom = received.content.toString();//yeni mesaj
                                        String oldChat = Server.RoomList.get(i+1);//onceki room chatte bulunan mesaj
                                        oldChat += msgRoom;//onceki mesajin ustune yeni mesaj eklenir
                                        Server.RoomList.set(i+1, oldChat);//room listte mesaj guncellenir
                                        Message msg = new Message(Message.Message_Type.SendRoomMessage);
                                        msg.content = Server.RoomList.get(i + 1);
                                        //roomda bulunan kisilere room chatinde mesajlari yollar
                                        for (int j = 0; j < Server.Clients.size(); j++) {
                                            if(Server.Clients.get(j).availableRoom.equals(availableRoom)){
                                                Server.Send(Server.Clients.get(j), msg);
                                            }
                                        }
                                    }
                                }
                                control2 = 1;
                            }
                            break;
                        case GetRoomMessage:
                            String currentRoomName = received.content.toString();
                            availableRoom = currentRoomName;//acik olan odanin adini yollar

                            for (int i = 0; i < Server.RoomList.size(); i++) {
                                if (i % 2 == 0 && Server.RoomList.get(i).equals(currentRoomName)) {
                                    Message msg = new Message(Message.Message_Type.GetRoomMessage);
                                    msg.content = Server.RoomList.get(i + 1);
                                    Server.Send(TheClient, msg);
                                }
                            }
                            break;
                        case Bitis:
                            for (int i = 0; i < Server.Clients.size(); i++) {
                                if(Server.Clients.get(i) != TheClient){
                                    Server.Send(Server.Clients.get(i), received);//clientlere kapatan kisinin adi yollanir
                                }
                            }
                            for (int i = 0; i < Server.Clients.size(); i++) {
                                if(Server.Clients.get(i) == TheClient){//eger kapatan kisiyse Clients arrayinden sil
                                   Server.Clients.remove(i);
                                }
                            }
                            break;
                    }
                } 
                catch (EOFException ex) {
                    // Handle end of stream gracefully (e.g., close the connection)
                    break;
                }
                catch (IOException ex) {
                    
                    Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
                    //client bağlantısı koparsa listeden sil
                    Server.Clients.remove(TheClient);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
                    //client bağlantısı koparsa listeden sil
                    Server.Clients.remove(TheClient);
                }
            }

        }
    }

    //eşleştirme threadi
    //her clientin ayrı bir eşleştirme thredi var
    class PairingThread extends Thread {

        SClient TheClient;

        PairingThread(SClient TheClient) {
            this.TheClient = TheClient;
        }

        public void run() {
            //client bağlı ve eşleşmemiş olduğu durumda dön
            while (TheClient.soket.isConnected() && TheClient.paired == false) {
                try {
                    //lock mekanizması
                    //sadece bir client içeri grebilir
                    //diğerleri release olana kadar bekler
                    Server.pairTwo.acquire(1);

                    //client eğer eşleşmemişse gir
                    if (!TheClient.paired) {
                        SClient crival = null;
                        //eşleşme sağlanana kadar dön
                        while (crival == null && TheClient.soket.isConnected()) {
                            //sürekli dönmesin 1 saniyede bir dönsün
                            //thredi uyutuyoruz
                            sleep(1000);
                        }
                       
                    }
                    //lock mekanizmasını servest bırak
                    //bırakılmazsa deadlock olur.
                    Server.pairTwo.release(1);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PairingThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
