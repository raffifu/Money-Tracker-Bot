package id.my.btw.util;

import id.my.btw.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
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

    public static Expense getExpense(Message message) throws TelegramApiException {
        MutableList<String> msgListMutable = Lists.mutable.of(message.getText().split("\n"));

        if (msgListMutable.size() != 2) {
            log.error("Invalid input");
            return null;
        }

        return Expense.builder()
                .amount(parseAmount(msgListMutable.get(0)))
                .note(msgListMutable.get(1))
                .date(LocalDate.now(ZoneId.of("Asia/Jakarta")))
                .account(Account.CASH.name())
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

    private static Integer parseAmount(String text) throws TelegramApiException {
        int multiplier = 1;

        if (Pattern
                .compile("[kK]$")
                .matcher(text)
                .find()
        )
            multiplier = 1000;

        Pattern pattern = Pattern.compile("\\d*\\.?\\d+");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            String match = matcher.group();
            return (int) (Double.parseDouble(match) * multiplier);
        }

        throw new TelegramApiException("Message broken");
    }

    public static Integer parseDeltaDay(String text) throws TelegramApiException {
        int adder = 1;
        if (Pattern
                .compile("^MINUS_")
                .matcher(text)
                .find()
        )
            adder = -1;

        Pattern pattern = Pattern.compile("-?\\d+");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            String match = matcher.group();
            return Integer.parseInt(match) + adder;
        }

        throw new TelegramApiException("Message broken");
    }

    public static Boolean isDateHandlerButton(String text) {
        return Pattern
                .compile("^(MINUS|PLUS)_")
                .matcher(text)
                .find();
    }

    public static Boolean isDateButton(String date) {
        try {
            LocalDate.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static Boolean isCategoryButton(String data) {
        for (Category category : Category.values()) {
            if (category.name().equals(data)) {
                return true;
            }
        }
        return false;
    }

    public static Boolean isAccountButton(String data) {
        for (Account account : Account.values()) {
            if (account.name().equals(data)) {
                return true;
            }
        }
        return false;
    }

    public static Boolean isCommonButton(String data) {
        for (Button button : Button.values()) {
            if (button.name().equals(data)) {
                return true;
            }
        }
        return false;
    }

}
