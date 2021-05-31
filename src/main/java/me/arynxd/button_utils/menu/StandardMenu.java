package me.arynxd.button_utils.menu;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import me.arynxd.button_utils.Constants;
import me.arynxd.button_utils.builder.menu.StandardMenuBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

import java.security.Key;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;

public class StandardMenu implements Menu {
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final Random random = new Random();

    private final Map<Integer, List<String>> mapper;
    private final EventWaiter waiter;
    private final Predicate<ButtonClickEvent> predicate;
    private final JDA jda;
    private final long channelId;

    private long messageId = -1;

    public StandardMenu(StandardMenuBuilder builder) {
        this.mapper = builder.getPageMapper();
        this.waiter = builder.getWaiter();
        this.predicate = builder.getPredicate();
        this.jda = builder.getJDA();
        this.channelId = builder.getChannelId();
    }

    private MessageChannel getChannel() {
        MessageChannel channel = jda.getTextChannelById(channelId);
        if (channel == null) {
            channel = jda.getPrivateChannelById(channelId);
        }

        return channel;
    }

    private void send() {

    }


    private void delete() {
        MessageChannel channel = getChannel();
        if (channel != null) {
            channel.deleteMessageById(messageId).queue();
        }
    }

    private void doWait() {
        waiter.waitForEvent(ButtonClickEvent.class, predicate, ev -> {
            Emoji emoji = ev.getButton().getEmoji();
            Integer asNum = Constants.EMOJI_NUMBER_MAP.get(emoji);

            if (asNum == null || asNum == -1) {
                //Waste basket or a malicious request, either way we want to delete
                delete();
                return;
            }


            doWait();
        });
    }

    @Override
    public void start() {
        doWait();
    }
}
