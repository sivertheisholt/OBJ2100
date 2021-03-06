package com.eksamen.networking;

import com.eksamen.components.Bruker;
import com.eksamen.components.Rom;
import com.eksamen.scenes.ClientScene;
import com.eksamen.systems.chatsystem.DeltakerTabell;
import com.eksamen.systems.chatsystem.InndataTabell;
import com.eksamen.utils.Feilmelding;
import com.eksamen.utils.StopNettverk;
import javafx.application.Platform;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * ClientNetworking håndterer nettverksdelen på klientsiden
 *
 */
public class ClientNetworking extends Thread implements StopNettverk {
    private Socket socket;
    private InputStreamReader input;
    private OutputStreamWriter output;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private ArrayList<Rom> rooms;
    private SyncClient syncClient;
    private CloseConnection closeConnection;

    /**
     * Konstruerer en ny ClientNetworking
     *
     * @param clientScene klient scenen den skal tilhøre
     * @param bruker bruker som skal brukes
     */
    public ClientNetworking(ClientScene clientScene, Bruker bruker) {
        try {
            socket = new Socket();
            SocketAddress socketadress = new InetSocketAddress("localhost", 1234);
            int timeout = 10000;
            socket.connect(socketadress, timeout);

            input = new InputStreamReader(socket.getInputStream());
            output = new OutputStreamWriter(socket.getOutputStream());

            bufferedReader = new BufferedReader(input);
            bufferedWriter = new BufferedWriter(output);

            closeConnection = new CloseConnection();

            rooms = new ArrayList<>();

            syncClient = new SyncClient(bufferedWriter, clientScene, this, bruker);

            getInformation();
        }catch (IOException e) {
            Feilmelding.visFeilmelding("Serveren ser ut til å være nede! Start programmet på nytt for å koble til igjen.");
            Platform.exit();
            return;
        }
    }

    /**
     * Run kjøres av Thread klassen
     * Er ansvarlig for å skaffe informasjon fra server
     */
    public void run() {
        try {
            if(socket == null)
                return;
            while(true) {
                String message = bufferedReader.readLine();
                syncClient.syncClient(message);
            }
        }catch (IOException e) {
            System.out.println("Kobling avsluttet");
        } finally {
            //avslutter kobling
            new CloseConnection().closeConnectionClient(socket, input, output, bufferedReader, bufferedWriter);
        }
    }

    /**
     * Skaffer start informasjonen fra server
     */
    public void getInformation() {
        try {
            Rom rom = null;
            looper: do {
                String message = bufferedReader.readLine();
                String[] messageArray = message.split(":");
                switch(messageArray[0]) {
                    case "romInfo":
                        rom = new Rom(messageArray[1], messageArray[2]);
                        rooms.add(rom);
                        break;
                    case "meldingInfo":
                        rom.leggTilMld(new InndataTabell(messageArray[1], messageArray[2], messageArray[3].replace("/", ":")));
                        break;
                    case "brukerInfo":
                        rom.leggTilDeltaker(new DeltakerTabell(messageArray[1]));
                        break;
                    case "Ferdig":
                        break looper;
                }
            } while(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sender melding til server at ny kobling har blitt lagd
     * @param brukernavn brukernavnet til brukeren
     */
    public void newKobling(String brukernavn) {
        syncClient.syncServer("newKobling",new Rom("",""),"","");
    }

    /**
     * Sender melding til server at nytt rom har blitt lagd
     * @param message hendelses type
     * @param rom rom som er laget
     */
    public void newRoom(String message, Rom rom) {
        syncClient.syncServer(message, rom, "", "");
    }

    /**
     * Sender melding til server at ny melding har blitt sendt i rom
     * @param message hendelsestype
     * @param rom rom som melding tilhører
     * @param senderUsername brukernavn til sender
     * @param messageUser melding fra bruker
     */
    public void newMessage(String message, Rom rom, String senderUsername, String messageUser) {
        syncClient.syncServer(message, rom, senderUsername, messageUser);
    }

    /**
     * Sender melding til server at ny bruker har logget på rom
     * @param message hendelsestype
     * @param rom rom som bruker tilhører
     * @param brukernavn brukernavn til bruker
     */
    public void newBruker(String message, Rom rom, String brukernavn) {
        syncClient.syncServer(message, rom, brukernavn, "");
    }

    /**
     * Sender melding til server at bruker har forlatt rom
     * @param message hendelsestype
     * @param rom rom som bruker tilhørte
     * @param brukernavn brukernavn til bruker
     */
    public void removeBruker(String message, Rom rom, String brukernavn) {
        syncClient.syncServer(message, rom, brukernavn, "");
    }

    /**
     * Skaffer rom fra nettverk (Kjøres en gang)
     * @return ArrayList med rom
     */
    public ArrayList<Rom> getRooms() {
        return rooms;
    }

    /**
     * Sender melding til server at rom har blitt fjernet
     * @param message hendelsestype
     * @param rom rom som blir fjernet
     * @param brukernavn navnet på brukeren som fjernet rommet
     */
    public void removeRoom(String message, Rom rom, String brukernavn) {
        syncClient.syncServer(message, rom, brukernavn, "");
    }

    /**
     * Stopper nettverkskoblingen
     */
    public void stopNetwork() {
        closeConnection.closeConnectionClient(socket,input,output,bufferedReader,bufferedWriter);
    }
}
