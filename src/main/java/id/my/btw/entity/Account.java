package id.my.btw.entity;

import com.vdurmont.emoji.EmojiManager;

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
        return EmojiManager.getForAlias(emoji).getUnicode() + " " + name();
    }

}
