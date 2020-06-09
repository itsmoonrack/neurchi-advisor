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

        assertEquals("Author", reader.textValue("role"));
        assertEquals("zoe", reader.textValue("username"));
        assertEquals("A94A8298-43B8-4DA0-9917-13FFF9E116ED", reader.textValue("tenantId"));
        assertEquals("Zoe", reader.textValue("firstName"));
        assertEquals("Doe", reader.textValue("lastName"));
        assertEquals("zoe@saasovation.com", reader.textValue("emailAddress"));
    }
}