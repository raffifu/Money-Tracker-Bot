package id.my.btw.util;

import com.vdurmont.emoji.EmojiManager;
import id.my.btw.entity.Category;
import id.my.btw.service.CallbackService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Slf4j
public class KeyboardUtil {

    public static InlineKeyboardMarkup defaultPad() {
        InlineKeyboardButton deleteButton = InlineKeyboardButton.builder()
                .text(EmojiManager.getForAlias("basket").getUnicode() + " Delete")
                .callbackData(CallbackService.DELETE)
                .build();

        InlineKeyboardButton editCategoryButton = InlineKeyboardButton.builder()
                .text(EmojiManager.getForAlias("pencil").getUnicode() + " Edit Category")
                .callbackData(CallbackService.EDIT_CATEGORY)
                .build();

        MutableList<InlineKeyboardButton> deleteRow = Lists.mutable.of(deleteButton);
        MutableList<InlineKeyboardButton> editCategoryRow = Lists.mutable.of(editCategoryButton);

        return InlineKeyboardMarkup.builder()
                .keyboard(Lists.mutable.of(deleteRow, editCategoryRow))
                .build();
    }

    public static InlineKeyboardMarkup confirmDeletePad() {
        InlineKeyboardButton yesButton = InlineKeyboardButton.builder()
                .text(EmojiManager.getForAlias("white_check_mark").getUnicode() + " Yes")
                .callbackData(CallbackService.CONFIRM_DELETE)
                .build();

        InlineKeyboardButton cancelButton = InlineKeyboardButton.builder()
                .text(EmojiManager.getForAlias("x").getUnicode() + " Cancel")
                .callbackData(CallbackService.CANCEL_DELETE)
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

}
