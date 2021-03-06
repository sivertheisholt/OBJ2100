package com.eksamen.utils;

/**
 * Inneholder forskjellige logge handlinger (Handling som skjedde)
 */
public enum LogOperations {
    NY_BRUKER_ROM("Bruker ble med i rom"),
    NY_MELDING_ROM("Ny melding ble sendt: "),
    NY_ROM("Nytt rom ble opprettet"),
    NY_BRUKER_KOBLING("Ny bruker koblet seg på"),
    FJERNA_ROM_BRUKER("Bruker forlot rom"),
    FJERNA_ROM("Rom ble sletta");

    private final String handling;

    LogOperations(String s) {
        this.handling = s;
    }

    /**
     * Skaffer handling
     * @return String
     */
    public String getHandling() {
        return handling;
    }
}
