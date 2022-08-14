package id.my.btw.util;

import com.vdurmont.emoji.EmojiManager;
import id.my.btw.entity.Account;
import id.my.btw.entity.Category;
import id.my.btw.service.CallbackService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
public class KeyboardUtil {

    public static InlineKeyboardMarkup defaultPad() {
        InlineKeyboardButton deleteButton = InlineKeyboardButton.builder()
                .text(EmojiManager.getForAlias("basket").getUnicode() + " Delete")
                .callbackData(CallbackService.DELETE)
                .build();

        InlineKeyboardButton editButton = InlineKeyboardButton.builder()
                .text(EmojiManager.getForAlias("pencil").getUnicode() + " Edit")
                .callbackData(CallbackService.EDIT)
                .build();

        MutableList<InlineKeyboardButton> deleteRow = Lists.mutable.of(deleteButton);
        MutableList<InlineKeyboardButton> editRow = Lists.mutable.of(editButton);

        return InlineKeyboardMarkup.builder()
                .keyboard(Lists.mutable.of(deleteRow, editRow))
                .build();
    }

    public static InlineKeyboardMarkup editPad() {

        MutableList<String> textButtons = Lists.mutable.of(
                EmojiManager.getForAlias("pencil").getUnicode() + " Category",
                EmojiManager.getForAlias("atm").getUnicode() + " Account",
                EmojiManager.getForAlias("date").getUnicode() + " Date"
        );

        MutableList<String> callbackButtons = Lists.mutable.of(
                CallbackService.EDIT_CATEGORY,
                CallbackService.EDIT_ACCOUNT,
                CallbackService.EDIT_DATE
        );

        MutableList<Pair<String, String>> buttonData = textButtons.zip(callbackButtons);

        MutableList<MutableList<InlineKeyboardButton>> keyboard = Lists.mutable.empty();
        MutableList<InlineKeyboardButton> row = Lists.mutable.empty();
        for (var data : buttonData) {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(data.getOne())
                    .callbackData(data.getTwo())
                    .build();

            row.add(button);

            if (row.size() == 2) {
                keyboard.add(row.clone());
                row.clear();
            }
        }

        if (row.size() != 0)
            keyboard.add(row);

        InlineKeyboardButton cancelButton = InlineKeyboardButton.builder()
                .text(EmojiManager.getForAlias("x").getUnicode() + " Cancel")
                .callbackData(CallbackService.CANCEL)
                .build();

        keyboard.add(Lists.mutable.of(cancelButton));

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }

    public static InlineKeyboardMarkup confirmDeletePad() {
        InlineKeyboardButton yesButton = InlineKeyboardButton.builder()
                .text(EmojiManager.getForAlias("white_check_mark").getUnicode() + " Yes")
                .callbackData(CallbackService.CONFIRM_DELETE)
                .build();

        InlineKeyboardButton cancelButton = InlineKeyboardButton.builder()
                .text(EmojiManager.getForAlias("x").getUnicode() + " Cancel")
                .callbackData(CallbackService.CANCEL)
                .build();

        MutableList<InlineKeyboardButton> keyboardButtons = Lists.mutable.of(yesButton, cancelButton);

        return InlineKeyboardMarkup.builder()
                .keyboardRow(keyboardButtons)
                .build();
    }

    public static InlineKeyboardMarkup categoryPad() {
        MutableList<InlineKeyboardButton> keyboardRow = Lists.mutable.empty();
        MutableList<MutableList<InlineKeyboardButton>> keyboard = Lists.mutable.empty();

        for (Category category : Category.values()) {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(category.toString())
                    .callbackData(category.name())
                    .build();

            keyboardRow.add(button);

            if (keyboardRow.size() == 2) {
                keyboard.add(keyboardRow.clone());
                keyboardRow.clear();
            }
        }

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }

    public static InlineKeyboardMarkup accountPad() {
        MutableList<InlineKeyboardButton> keyboardRow = Lists.mutable.empty();
        MutableList<MutableList<InlineKeyboardButton>> keyboard = Lists.mutable.empty();

        for (Account account : Account.values()) {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(account.toString())
                    .callbackData(account.name())
                    .build();

            keyboardRow.add(button);
            keyboard.add(keyboardRow.clone());
            keyboardRow.clear();
        }

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }

    public static InlineKeyboardMarkup datePad(LocalDate date) {
        MutableList<InlineKeyboardButton> keyboardRow = Lists.mutable.empty();

        for (int i = -1; i < 2; i += 2) {
            LocalDate option = date.plusDays(i);

            if (option.isAfter(LocalDate.now()))
                continue;

            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(option.format(DateTimeFormatter.ofPattern("d LLL yyyy")))
                    .callbackData(option.toString())
                    .build();

            keyboardRow.add(button);
        }

        InlineKeyboardButton cancelButton = InlineKeyboardButton.builder()
                .text(EmojiManager.getForAlias("x").getUnicode() + " Cancel")
                .callbackData(CallbackService.CANCEL)
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboard(Lists.mutable.of(keyboardRow, Lists.mutable.of(cancelButton)))
                .build();
    }
}
