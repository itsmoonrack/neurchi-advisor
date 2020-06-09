package com.neurchi.advisor.common.port.adapter.messaging;

public class AllPhoneNumbersCounted extends PhoneNumberProcessEvent {
    
    private int totalPhoneNumbers;

    public AllPhoneNumbersCounted(final String processId, final int totalPhoneNumbers) {
        super(processId);
        
        this.totalPhoneNumbers = totalPhoneNumbers;
    }

    public int totalPhoneNumbers() {
        return this.totalPhoneNumbers;
    }
}
