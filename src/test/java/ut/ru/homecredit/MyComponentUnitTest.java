package ut.ru.homecredit;

import org.junit.Test;
import ru.homecredit.confrestextension.api.MyPluginComponent;
import ru.homecredit.confrestextension.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}
