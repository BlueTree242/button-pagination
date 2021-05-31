package me.arynxd.button_utils.builder.pagination;

import me.arynxd.button_utils.Constants;
import me.arynxd.button_utils.builder.Builder;
import me.arynxd.button_utils.pagination.Paginator;
import me.arynxd.button_utils.pagination.StandardButtonPaginator;
import net.dv8tion.jda.api.entities.Emoji;

import javax.annotation.Nonnull;

public class StandardPaginatorBuilder extends PaginatorBuilder {
    private Emoji[] emojis = new Emoji[]{
            Constants.ARROW_LEFT_EMOJI, Constants.ARROW_RIGHT_EMOJI, Constants.WASTEBASKET_EMOJI};

    @Nonnull
    @Override
    protected Paginator compile() {
        return new StandardButtonPaginator(this);
    }

    /**
     * @param emojis 3 emojis of buttons in order: backward, forward, delete
     * @return The current Builder of chaining
     * @throws IllegalStateException if {@param emojis} is not 3 values
     */
    public Builder<Paginator> setEmojis(Emoji... emojis) {
        if (emojis.length != 3) {
            throw new IllegalArgumentException("Emojis must be 3 values");
        }
        this.emojis = emojis;
        return this;
    }

    public Emoji[] getEmojis() {
        return emojis;
    }
}
