package me.arynxd.button_utils.builder;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import me.arynxd.button_utils.pagination.Paginator;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.internal.utils.Checks;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public abstract class Builder<T> {

    private final List<Runnable> checks = new ArrayList<>();
    private int timeout = Paginator.DEFAULT_TIMEOUT;
    private TimeUnit timeoutUnit = Paginator.DEFAULT_TIMEOUT_UNIT;
    private boolean deleteOnTimeout = Paginator.DEFAULT_DELETE_ON_TIMEOUT;
    private EventWaiter waiter = null;
    private Predicate<ButtonClickEvent> predicate = null;
    private long channelId = -1;
    private List<MessageEmbed> embeds = new ArrayList<>();
    private JDA jda = null;

    protected Builder() {
        this.checks.addAll(Arrays.asList(
                () -> Checks.notEmpty(embeds, "Embeds"),
                () -> Checks.notNull(waiter, "Event Waiter"),
                () -> Checks.notNull(predicate, "Predicate"),
                () -> Checks.notNull(jda, "JDA"),
                () -> Checks.notNegative(channelId, "Channel ID")
                )
        );
    }

    public int getTimeout() {
        return timeout;
    }

    public Builder<T> setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public TimeUnit getTimeoutUnit() {
        return timeoutUnit;
    }

    public Builder<T> setTimeoutUnit(TimeUnit timeoutUnit) {
        this.timeoutUnit = timeoutUnit;
        return this;
    }

    public boolean isDeleteOnTimeout() {
        return deleteOnTimeout;
    }

    public Builder<T> setDeleteOnTimeout(boolean behavior) {
        this.deleteOnTimeout = behavior;
        return this;
    }

    public EventWaiter getWaiter() {
        return waiter;
    }

    public Builder<T> setWaiter(EventWaiter waiter) {
        this.waiter = waiter;
        return this;
    }

    public Predicate<ButtonClickEvent> getPredicate() {
        return predicate;
    }

    public Builder<T> setPredicate(Predicate<ButtonClickEvent> predicate) {
        this.predicate = predicate;
        return this;
    }

    public long getChannelId() {
        return channelId;
    }

    public List<MessageEmbed> getEmbeds() {
        return embeds;
    }

    public Builder<T> setEmbeds(List<MessageEmbed> embeds) {
        this.embeds = embeds;
        return this;
    }

    public JDA getJDA() {
        return jda;
    }

    public Builder<T> setJDA(JDA jda) {
        this.jda = jda;
        return this;
    }

    public Builder<T> setChannel(MessageChannel channel) {
        this.channelId = channel.getIdLong();
        return this;
    }

    public Builder<T> setChannel(long id) {
        this.channelId = id;
        return this;
    }

    private void validate() {
        checks.forEach(Runnable::run);
    }

    public void addCheck(Runnable check) {
        checks.add(check);
    }

    @Nonnull
    protected abstract T compile();


    public T build() {
        validate();
        return compile();
    }

}
