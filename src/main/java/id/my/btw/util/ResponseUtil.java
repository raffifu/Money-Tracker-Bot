package id.my.btw.util;

import com.vdurmont.emoji.EmojiParser;
import id.my.btw.entity.Expense;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

public class ResponseUtil {
    public static String genResultMessage(Expense expense) {
        String amountFormatted = NumberFormat
                .getCurrencyInstance(new Locale("id", "ID"))
                .format(expense.getAmount());

        String category = Optional.ofNullable(expense.getCategory()).orElse("-");

        StringBuilder sb = new StringBuilder();
        sb.append("*EXPENSE DETAIL*\n")
                .append(":moneybag: : ").append(amountFormatted).append("\n")
                .append(":performing_arts: : ").append(expense.getNote()).append("\n")
                .append(":card_index_dividers: : ").append(category).append("\n")
                .append(":date: : ").append(expense.getDate().format(DateTimeFormatter.ofPattern("E, d LLL yyyy"))).append("\n")
                .append("\n_Reply to edit (").append(expense.getId()).append(")_");

        return EmojiParser.parseToUnicode(sb.toString());
    }

    public static SendMessage genSendMessage(Message message, String text) {
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .replyToMessageId(message.getMessageId())
                .text(text)
                .parseMode(ParseMode.MARKDOWN)
                .build();
    }

    public static EditMessageText genEditMessage(Message message) {
        return genEditMessage(message, message.getText());
    }

    public static EditMessageText genEditMessage(Message message, String text) {
        return EditMessageText.builder()
                .chatId(message.getChatId().toString())
                .messageId(message.getMessageId())
                .text(text)
                .parseMode(ParseMode.MARKDOWN)
                .build();
    }
}
