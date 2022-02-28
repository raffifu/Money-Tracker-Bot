package id.my.btw.util;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MessageUtilTest {
    EasyRandom easyRandom = new EasyRandom();

    @Test
    void getId() {
        Integer id = easyRandom.nextInt(100);
        String testMessage = "_Reply to edit (" + id + ")_";

        Integer result = MessageUtil.getId(testMessage);
        Assertions.assertEquals(id, result);
    }
}