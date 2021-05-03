package com.eksamen.networking;

import com.eksamen.components.Rom;
import com.eksamen.scenes.ClientScene;

import java.io.BufferedReader;
import java.io.BufferedWriter;

public class SyncClient {
    private BufferedWriter bufferedWriter;
    private ClientScene clientScene;

    public SyncClient(BufferedWriter bufferedWriter, ClientScene clientScene) {
        this.bufferedWriter = bufferedWriter;
        this.clientScene = clientScene;
    }

    /**
     * Synker klienten med info fra server
     * @param message
     * @param rom
     */
    public void syncClient(String message)  {
        switch(message.split(":")[0]) {
            case "newRoom":
                newRoomServer(message);
                break;
        }
    }
    /**
     * Får kommando fra server at nytt rom har blitt lagd
     * @param message
     * @param rom
     */
    public void newRoomServer(String message) {
        String[] messageArray = message.split(":");
        clientScene.getRomSystem().opprettRom(messageArray[0],messageArray[1]);
        clientScene.getRooms().add(new Rom(messageArray[0], messageArray[1]));
    }

    /**
     * Synker server med info fra klient
     * @param message
     * @param rom
     */
    public void syncServer(String message, Rom rom) {
        switch(message) {
            case "newRoom":
                newRoomServer(message);
        }
    }

    /**
     * Sender kommando til server at klient lagde nytt rom
     * @param message
     * @param rom
     */
    public void newRoomClient(String message) {
        String[] messageSplit = message.split(":");

    }



}