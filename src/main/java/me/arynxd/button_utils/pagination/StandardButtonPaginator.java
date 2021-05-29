package me.arynxd.button_utils.pagination;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.ActionRow;
import net.dv8tion.jda.api.interactions.button.Button;
import net.dv8tion.jda.internal.utils.Checks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final ActionRow actionRow;
    private final List<String> jwtTokens;
    private long messageId = -1;
    private int page = 0;

    public StandardButtonPaginator(Builder builder) {
        this.timeoutUnit = builder.timeoutUnit;
        this.timeout = builder.timeout;

        this.predicate = builder.predicate;
        this.embeds = builder.embeds;
        this.deleteOnTimeout = builder.deleteOnTimeout;
        this.waiter = builder.waiter;
        this.channelId = builder.channelId;
        this.jda = builder.jda;

        this.maxPage = embeds.size();

        this.jwtTokens = new ArrayList<>();

        String token1 = makeToken((short) random.nextInt());
        String token2 = makeToken((short) random.nextInt());
        String token3 = makeToken((short) random.nextInt());

        this.actionRow = ActionRow.of(
                Button.primary(token1, ARROW_LEFT_EMOJI),
                Button.primary(token2, ARROW_RIGHT_EMOJI),
                Button.danger(token3, WASTEBASKET_EMOJI)
        );

        jwtTokens.add(token1);
        jwtTokens.add(token2);
        jwtTokens.add(token3);
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
                    LOGGER.error("ID not set (this should never happen)", new IllegalArgumentException());
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
            case ARROW_LEFT_UNICODE:
                page--;
                if (page < 0) {
                    page = maxPage - 1;
                }
                break;
            case ARROW_RIGHT_UNICODE:
                page++;
                if (page >= maxPage) {
                    page = 0;
                }
                break;
            case WASTEBASKET_UNICODE:
                message.delete().queue();
                return;
        }
        message.editMessage(embeds.get(page))
                .setActionRows(actionRow)
                .queue();
        doWait();
    }

    private void send() {
        MessageChannel channel = getChannel();

        if (channel == null) {
            throw new IllegalArgumentException("Channel does not exist for ID " + channelId);
        }

        channel.sendMessage(embeds.get(page))
                .setActionRows(actionRow)
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

    public static class Builder {
        private int timeout = DEFAULT_TIMEOUT;
        private TimeUnit timeoutUnit = DEFAULT_TIMEOUT_UNIT;
        private boolean deleteOnTimeout = DEFAULT_DELETE_ON_TIMEOUT;

        private EventWaiter waiter = null;
        private Predicate<ButtonClickEvent> predicate = null;
        private long channelId = -1;
        private List<MessageEmbed> embeds = new ArrayList<>();
        private JDA jda = null;


        public Builder setPredicate(Predicate<ButtonClickEvent> predicate) {
            this.predicate = predicate;
            return this;
        }

        public Builder setJDA(JDA jda) {
            this.jda = jda;
            return this;
        }

        public Builder setDeleteOnTimeout(boolean behavior) {
            this.deleteOnTimeout = behavior;
            return this;
        }

        public Builder setChannel(MessageChannel channel) {
            this.channelId = channel.getIdLong();
            return this;
        }

        public Builder setChannel(long id) {
            this.channelId = id;
            return this;
        }

        public Builder setEmbeds(List<MessageEmbed> embeds) {
            this.embeds = embeds;
            return this;
        }

        public Builder setTimeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder setTimeoutUnit(TimeUnit timeoutUnit) {
            this.timeoutUnit = timeoutUnit;
            return this;
        }

        public Builder setWaiter(EventWaiter waiter) {
            this.waiter = waiter;
            return this;
        }

        private void validate() {
            Checks.notEmpty(embeds, "Embeds");
            Checks.notNull(waiter, "Event Waiter");
            Checks.notNull(predicate, "Predicate");
            Checks.notNull(jda, "JDA");
            Checks.notNegative(channelId, "Channel ID");
        }

        @Nonnull
        public StandardButtonPaginator build() {
            validate();
            return new StandardButtonPaginator(this);
        }
    }
}
