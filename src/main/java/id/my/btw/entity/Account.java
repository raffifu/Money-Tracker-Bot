package id.my.btw.entity;

import com.vdurmont.emoji.EmojiManager;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public enum Account {
    BANK("bank"),
    CASH("moneybag"),
    EMONEY("iphone");

    private String emoji;

    Account(String emoji) {
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
