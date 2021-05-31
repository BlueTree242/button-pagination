package me.arynxd.button_utils.pagination;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import me.arynxd.button_utils.Constants;
import me.arynxd.button_utils.builder.pagination.StandardPaginatorBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.ActionRow;
import net.dv8tion.jda.api.interactions.button.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class StandardButtonPaginator implements Paginator {
    public static final Logger LOGGER = LoggerFactory.getLogger(StandardButtonPaginator.class);

    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final Random random = new Random();

    private final TimeUnit timeoutUnit;
    private final int timeout;

    private final Predicate<ButtonClickEvent> predicate;
    private final List<MessageEmbed> embeds;
    private final boolean deleteOnTimeout;
    private final EventWaiter waiter;
    private final long channelId;
    private final JDA jda;

    private final int maxPage;
    private final List<String> jwtTokens;
    private long messageId = -1;
    private int page = 0;
    private Emoji[] emojis;

    public StandardButtonPaginator(StandardPaginatorBuilder builder) {
        this.timeoutUnit = builder.getTimeoutUnit();
        this.timeout = builder.getTimeout();
        this.emojis = builder.getEmojis();
        this.predicate = builder.getPredicate();
        this.embeds = builder.getEmbeds();
        this.deleteOnTimeout = builder.isDeleteOnTimeout();
        this.waiter = builder.getWaiter();
        this.channelId = builder.getChannelId();
        this.jda = builder.getJDA();

        this.maxPage = embeds.size();

        this.jwtTokens = new ArrayList<>();

        String token1 = makeToken((short) random.nextInt());
        String token2 = makeToken((short) random.nextInt());
        String token3 = makeToken((short) random.nextInt());


        jwtTokens.add(token1);
        jwtTokens.add(token2);
        jwtTokens.add(token3);
    }

    public ActionRow getActionRow() {
        return ActionRow.of(
                //Forward
                page == 1 ? Button.primary(jwtTokens.get(0), emojis[0]).asDisabled() :
                        Button.primary(jwtTokens.get(0), emojis[0]).asEnabled(),
                //Backward
                page == maxPage ? Button.primary(jwtTokens.get(1), emojis[1]).asDisabled() :
                        Button.primary(jwtTokens.get(1), emojis[1]).asEnabled(),
                Button.danger(jwtTokens.get(2), emojis[2])
        );
    }

    @Override
    public void paginate() {
        send();
        doWait();
    }

    private void doWait() {
        waiter.waitForEvent(ButtonClickEvent.class, predicate, this::switchPage, timeout, timeoutUnit, () -> {
            if (this.deleteOnTimeout) {
                if (this.messageId == -1) {
                    LOGGER.error("ID not set (this should never happen)", new IllegalStateException());
                    return;
                }

                MessageChannel channel = getChannel();
                if (channel == null) {
                    LOGGER.error("Channel does not exist for ID " + this.channelId, new IllegalStateException());
                    return;
                }

                channel.deleteMessageById(this.messageId).queue();
            }
        });
    }

    private void switchPage(ButtonClickEvent event) {
        event.deferEdit().queue();
        String jwt = event.getComponentId();
        String emoji = event.getButton().getEmoji().getName();
        Message message = event.getMessage();

        if (!jwtTokens.contains(jwt)) {
            //Oh no
            MessageChannel channel = getChannel();
            if (channel != null) {
                channel.deleteMessageById(messageId).queue();
            }
            return;
        }

        if (!predicate.test(event)) {
            return;
        }

        switch (emoji) {
            case Constants.ARROW_LEFT_UNICODE:
                page--;
                if (page < 0) {
                    page = maxPage - 1;
                }
                break;
            case Constants.ARROW_RIGHT_UNICODE:
                page++;
                if (page >= maxPage) {
                    page = 0;
                }
                break;
            case Constants.WASTEBASKET_UNICODE:
                message.delete().queue();
                return;
        }
        message.editMessage(embeds.get(page))
                .setActionRows(getActionRow())
                .queue();
        doWait();
    }

    private void send() {
        MessageChannel channel = getChannel();

        if (channel == null) {
            throw new IllegalArgumentException("Channel does not exist for ID " + channelId);
        }

        channel.sendMessage(embeds.get(page))
                .setActionRows(getActionRow())
                .queue(m -> this.messageId = m.getIdLong());
    }

    private MessageChannel getChannel() {
        MessageChannel channel = jda.getTextChannelById(channelId);
        if (channel == null) {
            channel = jda.getPrivateChannelById(channelId);
        }

        return channel;
    }

    private String makeToken(int rng) {
        return Jwts.builder()
                .signWith(key)
                .setPayload(Long.toString(channelId) + rng)
                .compact();
    }

    @Override
    public int getTimeout() {
        return timeout;
    }


    @Override
    public TimeUnit getTimeoutUnit() {
        return this.timeoutUnit;
    }


    @Override
    public int getPage() {
        return page;
    }
}
