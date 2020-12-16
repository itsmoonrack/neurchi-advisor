package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.identityaccess.domain.model.IdentityAccessTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ContactInformationTest extends IdentityAccessTest {

    @Test
    public void TestContactInformation() {
        ContactInformation contactInformation = this.contactInformation();

        assertEquals(FIXTURE_USER_EMAIL_ADDRESS, contactInformation.emailAddress().address());
    }

    @Test
    public void TestChangeEmailAddress() {
        ContactInformation contactInformation = this.contactInformation();
        ContactInformation contactInformationCopy = new ContactInformation(contactInformation.emailAddress());

        ContactInformation contactInformation2 =
                contactInformation
                        .changeEmailAddress(
                                new EmailAddress(FIXTURE_USER_EMAIL_ADDRESS2));

        assertEquals(contactInformationCopy, contactInformation);
        assertNotEquals(contactInformation2, contactInformation);
        assertNotEquals(contactInformation2, contactInformationCopy);

        assertEquals(FIXTURE_USER_EMAIL_ADDRESS, contactInformation.emailAddress().address());
        assertEquals(FIXTURE_USER_EMAIL_ADDRESS2, contactInformation2.emailAddress().address());
    }
}
