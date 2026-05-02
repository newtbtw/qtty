package net.nwtech.qtty.application.requests;

import lombok.Builder;
import net.nwtech.qtty.domain.model.UserModel;

@Builder
public record VoteRequest(
        Integer movieId,
        UserModel voter,
        Double rating
) {
}
