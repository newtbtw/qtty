package net.nwtech.qtty.domain.model;

import lombok.Builder;

import java.util.List;

@Builder
public record MovieModel(
        Long id,
        Long tmdbId,
        Long discordRatingMessageId,
        MovieStatus status,
        String title,
        String synopsis,
        String imagePath,
        String releaseDate,
        List<RatingRecordModel> ratings
) {

    public String getFormattedReleaseDate()
    {
        var splitted = releaseDate.split("-");
        var year = splitted[0];
        var month = splitted[1];
        var date = splitted[2];
        return date + "/" + month + "/" + year;
    }

    public String getCurrentRating() {
        double ratingSum = 0;
        for (RatingRecordModel rating : ratings) {
            ratingSum += rating.rating();
        }
        if  (ratingSum == 0) return "0";
        return String.format("%.2f", ratingSum/ratings.size());
    }

    public String getVotersMentions() {
        StringBuilder mentions = new StringBuilder();
        for (var rating : ratings) {
            if (rating.user() == null) {
                continue;
            }
            mentions.append("<@").append(rating.user().discordId()).append(">").append(" - ").append(String.format("%.2f", rating.rating()));
            mentions.append("\n");
        }
        return mentions.toString();
    }
}
