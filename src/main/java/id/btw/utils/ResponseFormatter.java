package id.btw.utils;

import id.btw.payload.input.Expense;

import java.text.SimpleDateFormat;

public class ResponseFormatter {
    public static String onSucceedExpensePayload(Expense expense) {
        return "=== EXPENSE DETAIL ===\n" +
                "\uD83D\uDCB0 : " + expense.getAmount() + "\n" +
                "\uD83C\uDFAD : " + expense.getName() + "\n" +
                "\uD83D\uDCCD : " + expense.getDescription() + "\n" +
                "\uD83D\uDCC5 : " + new SimpleDateFormat("dd MMM YYYY").format(expense.getDate());
    }
}
