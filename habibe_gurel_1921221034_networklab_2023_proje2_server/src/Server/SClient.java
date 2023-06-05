/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Server;

/**
 *
 * @author Habibe
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import Message.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author INSECT
 */
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
    String availableRoom;
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

    //client mesaj gönderme
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
    //her clientin ayrı bir dinleme thredi var
    class Listen extends Thread {

        SClient TheClient;

        //thread nesne alması için yapıcı metod
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
                                    Server.Send(Server.Clients.get(i), received);
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
                            for (int i = 0; i < Server.RoomList.size(); i++) {
                                Message msg = new Message(Message.Message_Type.GetRooms);
                                msg.content = Server.RoomList.get(i);
                                Server.Send(TheClient, msg);
                            }
                            break;
                        case CreateRoom:
                            for (int i = 0; i < Server.Clients.size(); i++) {
                                if (Server.Clients.get(i) != TheClient) {
                                    Server.Send(Server.Clients.get(i), received);
                                }
                            }
                            Server.RoomList.add(received.content.toString());
                            Server.RoomList.add("");//mesaj
                            break;
                        case SendMessage:
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
                        case SendRoomMessage:
                            if (control2 == 1) {//ismi yolla
                                String currentRoomName = received.content.toString();
                                control2 = 2;
                            } else if (control2 == 2) {//mesaji yolla
                                int CurrentRoomIndex =0;
                                for (int i = 0; i < Server.RoomList.size(); i++) {
                                    if (i % 2 == 0 && Server.RoomList.get(i).equals(availableRoom)) {
                                        CurrentRoomIndex = i;
                                        String msgRoom = received.content.toString();
                                        String oldChat = Server.RoomList.get(i+1);
                                        oldChat += msgRoom;
                                        Server.RoomList.set(i+1, oldChat);
                                        Message msg = new Message(Message.Message_Type.SendRoomMessage);
                                        msg.content = Server.RoomList.get(i + 1);
                                        for (int j = 0; j < Server.Clients.size(); j++) {
                                            if(Server.Clients.get(j).availableRoom.equals(availableRoom)){//?
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
                            break;
                    }

                } catch (IOException ex) {
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
                            //liste içerisinde eş arıyor
//                            for (SClient clnt : Server.Clients) {
//                                if (TheClient != clnt && clnt.rival == null) {
//                                    //eşleşme sağlandı ve gerekli işaretlemeler yapıldı
//                                    crival = clnt;
//                                    crival.paired = true;
//                                   // crival.rival = TheClient;
//                                    TheClient.rival = crival;
//                                    TheClient.paired = true;
//                                    break;
//                                }
//                            }
                            //sürekli dönmesin 1 saniyede bir dönsün
                            //thredi uyutuyoruz
                            sleep(1000);
                        }
                        //eşleşme oldu
                        //her iki tarafada eşleşme mesajı gönder 
                        //oyunu başlat
//                        Message msg1 = new Message(Message.Message_Type.RivalConnected);
//                        msg1.content = TheClient.name;
//                        Server.Send(TheClient.rival, msg1);
//
//                        Message msg2 = new Message(Message.Message_Type.RivalConnected);
//                        msg2.content = TheClient.rival.name;
//                        Server.Send(TheClient, msg2);
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
