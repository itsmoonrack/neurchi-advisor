package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.identityaccess.domain.model.IdentityAccessTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnablementTest extends IdentityAccessTest {

    @Test
    public void TestEnablementEnabled() {

        Enablement enablement = new Enablement(true, null, null);

        assertTrue(enablement.isEnablementEnabled());
    }

    @Test
    public void TestEnablementDisabled() {

        Enablement enablement = new Enablement(false, null, null);

        assertFalse(enablement.isEnablementEnabled());
    }

    @Test
    public void TestEnablementOutsideStartEndDates() {

        Enablement enablement =
                new Enablement(
                        true,
                        this.dayBeforeYesterday(),
                        this.yesterday());

        assertFalse(enablement.isEnablementEnabled());
    }

    @Test
    public void TestEnablementUnsequencedDates() {

        boolean failure = false;

        try {
            new Enablement(
                    true,
                    this.tomorrow(),
                    this.today());
        } catch (Throwable t) {
            failure = true;
        }

        assertTrue(failure);
    }

    @Test
    public void TestEnablementEndsTimeExpired() {

        Enablement enablement =
                new Enablement(
                        true,
                        this.dayBeforeYesterday(),
                        this.yesterday());

        assertTrue(enablement.isTimeExpired());
    }

    @Test
    public void TestEnablementHasNotBegunTimeExpired() {

        Enablement enablement =
                new Enablement(
                        true,
                        this.tomorrow(),
                        this.dayAfterTomorrow());

        assertTrue(enablement.isTimeExpired());
    }
}
