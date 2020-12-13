package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.identityaccess.domain.model.IdentityAccessTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FullNameTest extends IdentityAccessTest {

    private final static String FIRST_NAME = "Zoe";
    private final static String LAST_NAME = "Doe";
    private final static String MARRIED_LAST_NAME = "Jones-Doe";
    private final static String WRONG_FIRST_NAME = "Zeo";

    @Test
    public void TestChangedFirstName() {
        FullName name = new FullName(WRONG_FIRST_NAME, LAST_NAME);
        name = name.withChangedFirstName(FIRST_NAME);
        assertEquals(FIRST_NAME + " " + LAST_NAME, name.asFormattedName());
    }

    @Test
    public void TestChangedLastName() {
        FullName name = new FullName(FIRST_NAME, LAST_NAME);
        name = name.withChangedLastName(MARRIED_LAST_NAME);
        assertEquals(FIRST_NAME + " " + MARRIED_LAST_NAME, name.asFormattedName());
    }

    @Test
    public void TestFormattedName() {
        FullName name = new FullName(FIRST_NAME, LAST_NAME);
        assertEquals(FIRST_NAME + " " + LAST_NAME, name.asFormattedName());
    }
}
