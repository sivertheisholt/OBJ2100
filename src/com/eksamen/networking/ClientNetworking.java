package com.eksamen.networking;

import com.eksamen.components.Bruker;
import com.eksamen.components.Rom;
import com.eksamen.scenes.ClientScene;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Opretter en ny klasse
 *
 */
public class ClientNetworking extends Thread {
    private Socket socket;
    private InputStreamReader input;
    private OutputStreamWriter output;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private ArrayList<Rom> rooms;
    private SyncClient syncClient;

    //Kobler opp klient til serveren og initialiserer streams/buffers
    public ClientNetworking(ClientScene clientScene, Bruker bruker) {
        try {
            socket = new Socket("192.168.10.191", 1234);

            input = new InputStreamReader(socket.getInputStream());
            output = new OutputStreamWriter(socket.getOutputStream());

            bufferedReader = new BufferedReader(input);
            bufferedWriter = new BufferedWriter(output);

            rooms = new ArrayList<>();

            syncClient = new SyncClient(bufferedWriter, clientScene, this, bruker);

            getRoomListe();
        } catch (IOException e) {
            System.out.println("Connection refused");
        }
    }

    //Kjøres av thread
    //Kjører i en loop som leser inn meldinger mottat fra server
    public void run() {
        try {
            if(socket == null)
                return;
            while(true) {
                String message = bufferedReader.readLine();
                System.out.println("Melding fra server: " + message);
                syncClient.syncClient(message);

            }
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            //avslutter kobling
            new CloseConnection().closeConnection(socket, input, output, bufferedReader, bufferedWriter);
        }
    }

    public void getRoomListe() {
        try {
            do {
                String message = bufferedReader.readLine();
                if(message.equals("Ferdig")) break;
                String[] string = message.split(":");
                rooms.add(new Rom(string[0], string[1]));
            } while(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void newRoom(String message, Rom rom) {
        syncClient.syncServer(message, rom, "", "");
    }
    public void newMessage(String message, Rom rom, String senderUsername, String messageUser) {
        syncClient.syncServer(message, rom, senderUsername, messageUser);
    }

    public ArrayList<Rom> getRooms() {
        return rooms;
    }
}
