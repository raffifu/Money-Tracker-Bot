package id.my.btw.util;

import com.vdurmont.emoji.EmojiParser;
import id.my.btw.entity.Expense;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ResponseFormatter {
    public static String onSucceedExpensePayload(Expense expense) {
        String amountFormatted = NumberFormat
                .getCurrencyInstance(new Locale("id", "ID"))
                .format(expense.getAmount());

        StringBuilder sb = new StringBuilder();
        sb.append("*EXPENSE DETAIL*\n")
                .append(":moneybag: : ").append(amountFormatted).append("\n")
                .append(":performing_arts: : ").append(expense.getName()).append("\n")
                .append(":round_pushpin: : ").append(expense.getCategory()).append("\n")
                .append(":date: : ").append(expense.getDate().format(DateTimeFormatter.ofPattern("d LLL yyyy"))).append("\n")
                .append("\n_Reply to edit (").append(expense.getId()).append(")_");

        return EmojiParser.parseToUnicode(sb.toString());
    }
}
