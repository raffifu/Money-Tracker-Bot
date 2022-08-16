package id.my.btw.entity;

import com.vdurmont.emoji.EmojiManager;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public enum Button {
    DELETE("basket"),
    YES("white_check_mark"),
    CANCEL("x"),
    CATEGORY("pencil"),
    DATE("date"),
    ACCOUNT("atm"),
    EDIT("pencil");

    private String emoji;

    Button(String emoji) {
        this.emoji = emoji;
    }

    @Override
    public String toString() {
        return EmojiManager.getForAlias(emoji).getUnicode() + " " + prettyFormat(name());
    }

    private String prettyFormat(String data) {
        return data.charAt(0) +
                data.substring(1).toLowerCase();
    }

    public InlineKeyboardButton toInlineKeyboardButton() {
        return InlineKeyboardButton.builder()
                .text(toString())
                .callbackData(name())
                .build();
    }

}
