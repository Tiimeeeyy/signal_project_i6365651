package data_management;

import com.alerts.Alert;
import com.alerts.PriorityAlertDecorator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PriorityAlertDecoratorTest {
    private Alert decoratedAlert;
    private PriorityAlertDecorator priorityAlertDecorator;

    @BeforeEach
    public void setup() {
        decoratedAlert = mock(Alert.class);
        when(decoratedAlert.getCondition()).thenReturn("SYSTOLIC TOO HIGH");
        priorityAlertDecorator = new PriorityAlertDecorator(decoratedAlert, "HIGH");
    }

    @Test
    public void getCondition_shouldAppendPriorityLevel() {
        String condition = priorityAlertDecorator.getCondition();
        assertEquals("SYSTOLIC TOO HIGH(Priority Level: HIGH)", condition);
    }

    @Test
    public void getCondition_shouldHandleEmptyCondition() {
        when(decoratedAlert.getCondition()).thenReturn("");
        String condition = priorityAlertDecorator.getCondition();
        assertEquals("(Priority Level: HIGH)", condition);
    }

}