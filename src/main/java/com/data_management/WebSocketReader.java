package com.data_management;

import java.net.URI;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;


// We assume the data will come in a JSON format.

/**
 * WebsocketReader class extends WebsocketClient and implements DataReader.
 * This class handles reading data from a WebSocket server.
 */
public class WebSocketReader extends WebSocketClient implements DataReader{
    private DataStorage dataStorage;

    /**
     * Class constructor.
     * @param serverURI The URI of the Websocket server.
     * @param dataStorage The dataStorage object where the data will be stored.
     */
    public WebSocketReader(URI serverURI, DataStorage dataStorage) {
        super(serverURI);
        this.dataStorage = dataStorage;
    }

    /**
     * This method is called when a connection is established with the server.
     *
     * @param serverHandshake The handshake from the server.
     */
    @Override
    public void onOpen (ServerHandshake serverHandshake) {
        System.out.println("Connected to server");
    }

    /**
     * This method is called when a message is received from the server.
     * @param message The message received from the server.
     */
    @Override
    public void onMessage(String message) {
        recieveData(message);
    }

    /**
     * This method is called when the connection with the server closes.
     * @param code The status code.
     * @param reason The reason for disconnection.
     * @param remote Whether the connection was initiated by the remote host.
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected from server");
    }

    /**
     * This method is called when an error occurs.
     * @param ex The exception that was thrown.
     */
    @Override
    public void onError(Exception ex) {
        System.out.println("Exception! " + ex.getMessage());
    }

    /**
     * This method is used to connect to the server.
     * @param dataStorage The DataStorage object, where the data will be stored.
     */
    @Override
    public void connect (DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        super.connect();
    }

    /**
     * This method is used to disconnect from the server.
     */
    @Override
    public void disconnect () {
        super.close();
    }

    /**
     * This method is used to receive data from the server.
     * The data is parsed and added to the DatStorage Object.
     * @param data The data received from the server.
     */
    @Override
    public void recieveData(String data) {
        // Split by comma, so we can read CSV format files
       String [] values = data.split(",");

       int id = Integer.parseInt(values[0]);
       long timestamp = Long.parseLong(values[1]);
       String recordType = values[2];
       double value = Double.parseDouble(values[3]);

       dataStorage.addPatientData(id, value, recordType, timestamp);
    }


}
