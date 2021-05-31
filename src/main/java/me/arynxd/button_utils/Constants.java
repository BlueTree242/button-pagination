package me.arynxd.button_utils;

import net.dv8tion.jda.api.entities.Emoji;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final String ARROW_RIGHT_UNICODE = "\u27A1\uFE0F";
    public static final String ARROW_LEFT_UNICODE = "\u2B05\uFE0F";
    public static final String WASTEBASKET_UNICODE = "\uD83D\uDDD1\uFE0F";
    public static final Emoji ARROW_RIGHT_EMOJI = Emoji.ofUnicode(ARROW_RIGHT_UNICODE);
    public static final Emoji ARROW_LEFT_EMOJI = Emoji.ofUnicode(ARROW_LEFT_UNICODE);
    public static final Emoji WASTEBASKET_EMOJI = Emoji.ofUnicode(WASTEBASKET_UNICODE);
    /**
     * DO NOT MUTATE
     */
    public static final String[] NUMBER_UNICODES = {
            "\u0030\u20E3",
            "\u0031\u20E3",
            "\u0032\u20E3",
            "\u0033\u20E3",
            "\u0034\u20E3",
            "\u0035\u20E3",
            "\u0036\u20E3",
            "\u0037\u20E3",
            "\u0038\u20E3",
            "\u0039\u20E3"
    };
    /**
     * DO NOT MUTATE
     */
    public static final Emoji[] NUMBER_EMOJIS = {
            Emoji.ofUnicode(NUMBER_UNICODES[0]),
            Emoji.ofUnicode(NUMBER_UNICODES[1]),
            Emoji.ofUnicode(NUMBER_UNICODES[2]),
            Emoji.ofUnicode(NUMBER_UNICODES[3]),
            Emoji.ofUnicode(NUMBER_UNICODES[4]),
            Emoji.ofUnicode(NUMBER_UNICODES[5]),
            Emoji.ofUnicode(NUMBER_UNICODES[6]),
            Emoji.ofUnicode(NUMBER_UNICODES[7]),
            Emoji.ofUnicode(NUMBER_UNICODES[8]),
            Emoji.ofUnicode(NUMBER_UNICODES[9])
    };
    public static final Map<Emoji, Integer> EMOJI_NUMBER_MAP;

    static {
        Map<Emoji, Integer> map = new HashMap<>();
        map.put(NUMBER_EMOJIS[0], 0);
        map.put(NUMBER_EMOJIS[1], 1);
        map.put(NUMBER_EMOJIS[2], 2);
        map.put(NUMBER_EMOJIS[3], 3);
        map.put(NUMBER_EMOJIS[4], 4);
        map.put(NUMBER_EMOJIS[5], 5);
        map.put(NUMBER_EMOJIS[6], 6);
        map.put(NUMBER_EMOJIS[7], 7);
        map.put(NUMBER_EMOJIS[8], 8);
        map.put(NUMBER_EMOJIS[9], 9);
        map.put(WASTEBASKET_EMOJI, -1);
        EMOJI_NUMBER_MAP = Collections.unmodifiableMap(map);
    }

    private Constants() {
    }
}
