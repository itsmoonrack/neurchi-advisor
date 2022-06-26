package com.neurchi.advisor.common.media;

import org.springframework.http.MediaType;

public class NeurchiAdvisorMediaType {

    public static final MediaType ID_ADVISOR_TYPE_JSON =
            new MediaType("application", "vnd.neurchiadvisor.idadvisor+json");

    public static final String ID_ADVISOR_TYPE_JSON_VALUE =
            "application/vnd.neurchiadvisor.idadvisor+json";

    public static final String ID_ADVISOR_TYPE_XML_VALUE =
            "application/vnd.neurchiadvisor.idadvisor+xml";
}
