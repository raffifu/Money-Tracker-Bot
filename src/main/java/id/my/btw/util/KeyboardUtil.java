package id.my.btw.util;

import com.vdurmont.emoji.EmojiManager;
import id.my.btw.CallbackData;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class KeyboardUtil {

    public static InlineKeyboardMarkup deletePad() {
        InlineKeyboardButton deleteButton = InlineKeyboardButton.builder()
                .text(EmojiManager.getForAlias("basket").getUnicode() + " Delete")
                .callbackData(CallbackData.DELETE)
                .build();

        MutableList<InlineKeyboardButton> keyboardButtons = Lists.mutable.of(deleteButton);

        return InlineKeyboardMarkup.builder()
                .keyboardRow(keyboardButtons)
                .build();
    }

    public static InlineKeyboardMarkup confirmDeletePad() {
        InlineKeyboardButton yesButton = InlineKeyboardButton.builder()
                .text(EmojiManager.getForAlias("white_check_mark").getUnicode() + " Yes")
                .callbackData(CallbackData.CONFIRM_DELETE)
                .build();

        InlineKeyboardButton cancelButton = InlineKeyboardButton.builder()
                .text(EmojiManager.getForAlias("x").getUnicode() + " Cancel")
                .callbackData(CallbackData.CANCEL_DELETE)
                .build();

        MutableList<InlineKeyboardButton> keyboardButtons = Lists.mutable.of(yesButton, cancelButton);

        return InlineKeyboardMarkup.builder()
                .keyboardRow(keyboardButtons)
                .build();
    }

}
