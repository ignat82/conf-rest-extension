package it.ru.homecredit.rest;

import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.homecredit.confrestextension.response.AttachmentResponse;

import static org.junit.Assert.assertEquals;

public class AttachmentControllerFuncTest {

    @Before
    public void setup() {

    }

    @After
    public void tearDown() {

    }

    @Test
    public void messageIsValid() {

        String baseUrl = System.getProperty("baseurl");
        String resourceUrl = baseUrl + "/web/deleteatt/1.0/message";

        RestClient client = new RestClient();
        Resource resource = client.resource(resourceUrl);

        AttachmentResponse message = resource.get(AttachmentResponse.class);

        assertEquals("wrong message","Hello  World","Hello  World");
    }
}
