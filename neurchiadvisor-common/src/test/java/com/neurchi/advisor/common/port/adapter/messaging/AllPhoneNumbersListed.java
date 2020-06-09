package com.neurchi.advisor.common.port.adapter.messaging;

public class AllPhoneNumbersListed extends PhoneNumberProcessEvent {

    private String allPhoneNumbers;

    public AllPhoneNumbersListed(final String processId, final String allPhoneNumbers) {
        super(processId);

        this.allPhoneNumbers = allPhoneNumbers;
    }

    public String allPhoneNumbers() {
        return this.allPhoneNumbers;
    }
}
