package data_management;

import com.data_management.DataStorage;
import com.data_management.WebSocketReader;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.Mockito.*;

class WebSocketReaderTest {
    private DataStorage dataStorage;
    private WebSocketReader webSocketReader;

    @BeforeEach
    public void setup() throws URISyntaxException {
        dataStorage = mock(DataStorage.class);
        webSocketReader = new WebSocketReader(new URI("ws://localhost:8080"), dataStorage);
    }

    @Test
    void onOpen_shouldPrintConnectedToServer() {
        ServerHandshake serverHandshake = mock(ServerHandshake.class);
        webSocketReader.onOpen(serverHandshake);
        // Verify that "Connected to server" is printed to the console
    }

    @Test
    void onMessage_shouldCallReceiveData() {
        webSocketReader.onMessage("1,1622544000,temperature,36.6");
        verify(dataStorage, times(1)).addPatientData(1, 36.6, "temperature", 1622544000);
    }

    @Test
    void onClose_shouldPrintDisconnectedFromServer() {
        webSocketReader.onClose(1000, "Normal closure", true);
        // Verify that "Disconnected from server" is printed to the console
    }

    @Test
    void onError_shouldPrintExceptionMessage() {
        Exception ex = new Exception("Test exception");
        webSocketReader.onError(ex);
        // Verify that "Exception! Test exception" is printed to the console
    }

    @Test
    void receiveData_shouldAddPatientData() {
        webSocketReader.recieveData("1,1622544000,temperature,36.6");
        verify(dataStorage, times(1)).addPatientData(1, 36.6, "temperature", 1622544000);
    }

    @Test
    void receiveData_shouldHandleInvalidData() {
        webSocketReader.recieveData("invalid data");
        verify(dataStorage, never()).addPatientData(anyInt(), anyDouble(), anyString(), anyLong());
    }
    @Test
    void receiveData_shouldNotAddIncompletePatientData() throws URISyntaxException {
        DataStorage dataStorage1 = mock(DataStorage.class);
        WebSocketReader webSocketReader1 = new WebSocketReader(new URI("ws://localhost:8080"), dataStorage);

        webSocketReader1.recieveData("1,122332535, temperature");
        verify(dataStorage1, never()).addPatientData(anyInt(), anyDouble(), anyString(), anyLong());
    }
}
