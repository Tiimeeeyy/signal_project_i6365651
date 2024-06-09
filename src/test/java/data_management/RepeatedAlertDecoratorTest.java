package data_management;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.alerts.RepeatedAlertDecorator;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

class RepeatedAlertDecoratorTest {
    private Alert decoratedAlert;
    private AlertGenerator alertGenerator;
    private Patient patient;
    private RepeatedAlertDecorator repeatedAlertDecorator;

    @BeforeEach
    public void setup() {
        decoratedAlert = mock(Alert.class);
        alertGenerator = mock(AlertGenerator.class);
        patient = mock(Patient.class);
        when(decoratedAlert.getCondition()).thenReturn("SYSTOLIC TOO HIGH");
        repeatedAlertDecorator = new RepeatedAlertDecorator(decoratedAlert, 5, alertGenerator, patient);
    }

    @Test
    public void triggerAlert_shouldCallDecoratedAlertTrigger() {
        repeatedAlertDecorator.triggerAlert();
        verify(decoratedAlert, times(1)).triggerAlert();
    }

    @Test
    public void getCondition_shouldReturnDecoratedAlertCondition() {
        String condition = repeatedAlertDecorator.getCondition();
        assertEquals("SYSTOLIC TOO HIGH", condition);
    }
}