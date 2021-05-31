package me.arynxd.button_utils.builder.pagination;

import javax.annotation.Nonnull;

import me.arynxd.button_utils.Constants;
import me.arynxd.button_utils.builder.Builder;
import me.arynxd.button_utils.pagination.Paginator;
import me.arynxd.button_utils.pagination.StandardButtonPaginator;
import net.dv8tion.jda.api.entities.Emoji;

public class StandardPaginatorBuilder extends PaginatorBuilder {
    private Emoji[] emojis = new Emoji[]{
            Constants.ARROW_LEFT_EMOJI, Constants.ARROW_RIGHT_EMOJI, Constants.WASTEBASKET_EMOJI};
    @Nonnull
    @Override
    protected Paginator compile() {
        return new StandardButtonPaginator(this);
    }

    public Builder<Paginator> setEmojis(Emoji... emojis) {
        if (emojis.length != 3) {
            throw new IllegalStateException("Emojis must be 3 values");
        }
        this.emojis = emojis;
        return this;
    }
    public Emoji[] getEmojis() {
        return emojis;
    }
}
