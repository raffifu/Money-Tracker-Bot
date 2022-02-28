package id.my.btw.util;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class MessageUtil {

    public static Integer getId(String message) {
        log.info("Attempting to get id from message");
        Pattern pattern = Pattern.compile("\\(\\d*\\)$");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            String found = matcher.group();
            return Integer.parseInt(found.substring(1, found.length() - 1));
        }

        return null;
    }

}
