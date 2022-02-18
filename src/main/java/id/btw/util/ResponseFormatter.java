package id.btw.util;

import id.btw.entity.Expense;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ResponseFormatter {
    public static String onSucceedExpensePayload(Expense expense) {
        String amountFormatted = NumberFormat
                .getCurrencyInstance(new Locale("id", "ID"))
                .format(expense.getAmount());
        return "EXPENSE DETAIL\n" +
                "\uD83D\uDCB0 : " + amountFormatted + "\n" +
                "\uD83C\uDFAD : " + expense.getName() + "\n" +
                "\uD83D\uDCCD : " + expense.getDescription() + "\n" +
                "\uD83D\uDCC5 : " + expense.getDate().format(DateTimeFormatter.ofPattern("d LLL yyyy"));
    }
}
