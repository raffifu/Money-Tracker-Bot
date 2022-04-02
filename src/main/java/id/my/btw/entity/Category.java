package id.my.btw.entity;

import com.vdurmont.emoji.EmojiManager;

public enum Category {
    FOOD("stuffed_flatbread"),
    TRANSPORT("oncoming_automobile"),
    HOUSING("house_with_garden"),
    UTILITIES("bulb"),
    HEALTH("hospital"),
    PERSONAL("dancer"),
    ENTERTAINMENT("movie_camera"),
    MISC("eyes");

    private String emoji;

    Category(String emoji) {
        this.emoji = emoji;
    }

    @Override
    public String toString() {
        return EmojiManager.getForAlias(emoji).getUnicode() + " " + name();
    }
}
