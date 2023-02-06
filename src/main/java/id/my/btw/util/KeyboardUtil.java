package id.my.btw.util;

import id.my.btw.entity.Account;
import id.my.btw.entity.Button;
import id.my.btw.entity.Category;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
public class KeyboardUtil {

    public static InlineKeyboardMarkup defaultPad() {
        return editPad();
    }

    public static InlineKeyboardMarkup editPad() {
        MutableList<Button> editButtonData = Lists.mutable.of(
                Button.CATEGORY,
                Button.ACCOUNT,
                Button.DATE
        );

        MutableList<MutableList<InlineKeyboardButton>> keyboard = Lists.mutable.empty();
        MutableList<InlineKeyboardButton> row = Lists.mutable.empty();
        for (Button button : editButtonData) {
            row.add(button.toInlineKeyboardButton());

            if (row.size() == 2) {
                keyboard.add(row.clone());
                row.clear();
            }
        }

        if (row.size() != 0)
            keyboard.add(row);

        keyboard.add(Lists.mutable.of(Button.DELETE.toInlineKeyboardButton()));

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }

    public static InlineKeyboardMarkup confirmDeletePad() {
        MutableList<InlineKeyboardButton> keyboardButtons = Lists.mutable.of(
                Button.YES.toInlineKeyboardButton(),
                Button.CANCEL.toInlineKeyboardButton()
        );

        return InlineKeyboardMarkup.builder()
                .keyboardRow(keyboardButtons)
                .build();
    }

    public static InlineKeyboardMarkup categoryPad() {
        MutableList<InlineKeyboardButton> keyboardRow = Lists.mutable.empty();
        MutableList<MutableList<InlineKeyboardButton>> keyboard = Lists.mutable.empty();

        for (Category category : Category.values()) {
            keyboardRow.add(category.toInlineKeyboardButton());

            if (keyboardRow.size() == 2) {
                keyboard.add(keyboardRow.clone());
                keyboardRow.clear();
            }
        }

        keyboard.add(Lists.mutable.of(Button.CANCEL.toInlineKeyboardButton()));

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }

    public static InlineKeyboardMarkup accountPad() {
        MutableList<InlineKeyboardButton> keyboardRow = Lists.mutable.empty();
        MutableList<MutableList<InlineKeyboardButton>> keyboard = Lists.mutable.empty();

        for (Account account : Account.values()) {
            keyboardRow.add(account.toInlineKeyboardButton());
            keyboard.add(keyboardRow.clone());
            keyboardRow.clear();
        }

        keyboard.add(Lists.mutable.of(Button.CANCEL.toInlineKeyboardButton()));

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }

    public static InlineKeyboardMarkup datePad(LocalDate date, Integer deltaDay) {
        MutableList<InlineKeyboardButton> keyboardRow = Lists.mutable.empty();
        MutableList<MutableList<InlineKeyboardButton>> keyboard = Lists.mutable.empty();

        LocalDate option = date.plusDays(deltaDay);

        keyboardRow.add(createKeyboard("<<", "MINUS_".concat(deltaDay.toString())));

        if (!(option.isAfter(LocalDate.now()) || option.isEqual(LocalDate.now())))
            keyboardRow.add(createKeyboard(">>", "PLUS_".concat(deltaDay.toString())));

        keyboard.add(keyboardRow.clone());

        keyboard.add(Lists.mutable.of(createKeyboard(
                option.format(DateTimeFormatter.ofPattern("d LLL yyyy")),
                option.toString()
        )));

        keyboard.add(Lists.mutable.of(Button.CANCEL.toInlineKeyboardButton()));

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }

    private static InlineKeyboardButton createKeyboard(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build();
    }
}
