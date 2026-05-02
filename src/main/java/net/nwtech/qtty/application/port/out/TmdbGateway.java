package net.nwtech.qtty.application.port.out;

import net.nwtech.qtty.domain.model.MovieModel;

import java.util.List;

public interface TmdbGateway {

     List<MovieModel> searchMoviesFromTitle(String title);

}
