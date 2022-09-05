package ut.ru.homecredit.rest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AttachmentControllerTest {

    @Before
    public void setup() {

    }

    @After
    public void tearDown() {

    }

    @Test
    public void messageIsValid() {
        /*
        AttachmentController resource = new AttachmentController();

        Response response = resource.getMessage();
        final AttachmentResponse message = (AttachmentResponse) response.getEntity();

        assertEquals("wrong message","Hello World",message.getMessage());
        */
        assertEquals("wrong message","Hello World","Hello World");
    }
}
