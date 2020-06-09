package com.neurchi.advisor.common.port.adapter.messaging;

public class MatchedPhoneNumbersCounted extends PhoneNumberProcessEvent {

    private int matchedPhoneNumbers;

    public MatchedPhoneNumbersCounted(final String processId, final int matchedPhoneNumbersCount) {
        super(processId);
        
        this.matchedPhoneNumbers = matchedPhoneNumbersCount;
    }

    public int matchedPhoneNumbers() {
        return this.matchedPhoneNumbers;
    }
}
