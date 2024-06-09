package com.alerts;

public class PriorityAlertDecorator extends AlertDecorator {
    private String priorityLevel;

    public PriorityAlertDecorator(Alert decoratedAlert, String prioritylevel){
        super(decoratedAlert);
        this.priorityLevel = priorityLevel;
    }

    @Override
    public String getCondition() {
        return super.getCondition() + "(Priority Level: " + priorityLevel + ")";

    }

    @Override
    public void triggerAlert() {
        super.triggerAlert();

    }
}
