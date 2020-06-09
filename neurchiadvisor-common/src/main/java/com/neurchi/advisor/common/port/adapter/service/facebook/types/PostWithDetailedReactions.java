package com.neurchi.advisor.common.port.adapter.service.facebook.types;

import com.restfb.Facebook;
import com.restfb.types.Post;
import com.restfb.types.Reactions;

public class PostWithDetailedReactions extends Post {

    @Facebook("reactions_like")
    private Reactions reactionsLike;

    @Facebook("reactions_love")
    private Reactions reactionsLove;

    @Facebook("reactions_wow")
    private Reactions reactionsWow;

    @Facebook("reactions_haha")
    private Reactions reactionsHaha;

    @Facebook("reactions_sad")
    private Reactions reactionsSad;

    @Facebook("reactions_angry")
    private Reactions reactionsAngry;

    public Reactions getReactionsLike() {
        return reactionsLike;
    }

    public Reactions getReactionsLove() {
        return reactionsLove;
    }

    public Reactions getReactionsWow() {
        return reactionsWow;
    }

    public Reactions getReactionsHaha() {
        return reactionsHaha;
    }

    public Reactions getReactionsSad() {
        return reactionsSad;
    }

    public Reactions getReactionsAngry() {
        return reactionsAngry;
    }
}
