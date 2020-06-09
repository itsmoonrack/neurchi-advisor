package com.neurchi.advisor.common.port.adapter.messaging;

public class PhoneNumbersMatched extends PhoneNumberProcessEvent {

    private String matchedPhoneNumbers;

    public PhoneNumbersMatched(final String processId, final String matchedPhoneNumbers) {
        super(processId);
        
        this.matchedPhoneNumbers = matchedPhoneNumbers;
    }

    public String matchedPhoneNumbers() {
        return this.matchedPhoneNumbers;
    }
}
