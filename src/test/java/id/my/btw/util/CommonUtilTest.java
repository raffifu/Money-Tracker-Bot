package id.my.btw.util;

import id.my.btw.CommandMessage;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CommonUtilTest {
    EasyRandom easyRandom = new EasyRandom();

    @Test
    void getId() {
        Integer id = easyRandom.nextInt(100);
        String testMessage = "_Reply to edit (" + id + ")_";

        Integer result = CommonUtil.getId(testMessage);
        assertEquals(id, result);
    }

    @Test
    void testParseCommandMessage() {
        // given
        Message message = easyRandom.nextObject(Message.class);

        String command = easyRandom.nextObject(String.class);
        String messageText = easyRandom.nextObject(String.class);
        message.setText("/" + command + " " + messageText);

        CommandMessage expected = new CommandMessage(command, messageText);

        // when
        CommandMessage result = CommonUtil.parseCommandMessage(message);

        // then
        assertNotNull(result);

        assertEquals(expected.getCommand(), result.getCommand());
        assertEquals(expected.getMessage(), result.getMessage());
        assertEquals(expected, result); // Error
    }
}