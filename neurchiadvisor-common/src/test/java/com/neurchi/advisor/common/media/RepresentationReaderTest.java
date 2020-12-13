package com.neurchi.advisor.common.media;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RepresentationReaderTest {

    private static final String USER_IN_ROLE_REPRESENTATION =
            "{"
                    + "\"role\":\"Author\",\"username\":\"zoe\","
                    + "\"tenantId\":\"A94A8298-43B8-4DA0-9917-13FFF9E116ED\","
                    + "\"firstName\":\"Zoe\",\"lastName\":\"Doe\","
                    + "\"emailAddress\":\"zoe@saasovation.com\""
                    + "}";

    @Test
    public void TestUserInRoleRepresentation() throws Exception {
        RepresentationReader reader =
                new RepresentationReader(USER_IN_ROLE_REPRESENTATION);

        assertEquals("Author", reader.stringValue("role"));
        assertEquals("zoe", reader.stringValue("username"));
        assertEquals("A94A8298-43B8-4DA0-9917-13FFF9E116ED", reader.stringValue("tenantId"));
        assertEquals("Zoe", reader.stringValue("firstName"));
        assertEquals("Doe", reader.stringValue("lastName"));
        assertEquals("zoe@saasovation.com", reader.stringValue("emailAddress"));
    }
}