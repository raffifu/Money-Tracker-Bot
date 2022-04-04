package id.my.btw.util;

import id.my.btw.entity.CommandMessage;
import id.my.btw.entity.Expense;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class CommonUtil {

    public static Integer getId(String message) {
        Pattern pattern = Pattern.compile("\\(\\d*\\)$");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            log.info("Found id");
            
            String found = matcher.group();
            return Integer.parseInt(found.substring(1, found.length() - 1));
        }

        log.error("Id not found from message: {}", message);

        return null;
    }

    public static Expense getExpense(Message message) {
        MutableList<String> msgListMutable = Lists.mutable.of(message.getText().split("\n"));

        if (msgListMutable.size() != 2) {
            log.error("Invalid input");
            return null;
        }

        return Expense.builder()
                .amount(Integer.parseInt(msgListMutable.get(0)))
                .note(msgListMutable.get(1))
                .date(LocalDate.now(ZoneId.of("Asia/Jakarta")))
                .build();
    }

    public static CommandMessage parseCommandMessage(Message message) {
        Pattern pattern = Pattern.compile("^/[a-zA-Z_]+");
        Matcher matcher = pattern.matcher(message.getText());

        if (matcher.find()) {
            String match = matcher.group();

            String command = match.substring(1);
            String text = message.getText().substring(match.length() + 1);

            return new CommandMessage(command, text);
        }

        return null;
    }

}
