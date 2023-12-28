package net.twisterrob.travel.statushistory.controller;

import javax.annotation.Nonnull;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;

import org.slf4j.*;

import net.twisterrob.travel.domain.london.status.Feed;
import net.twisterrob.travel.domain.london.status.StatusItem;
import net.twisterrob.travel.domain.london.status.api.RefreshResult;
import net.twisterrob.travel.domain.london.status.api.RefreshUseCase;

@Controller
public class RefreshFeedController {

	private static final Logger LOG = LoggerFactory.getLogger(RefreshFeedController.class);

	private final RefreshUseCase useCase;

	public RefreshFeedController(RefreshUseCase useCase) {
		this.useCase = useCase;
	}

	@Get("/refresh")
	@Produces(MediaType.TEXT_PLAIN)
	public HttpResponse<String> refreshFeed(@QueryValue("feed") @Nonnull Feed feed) {
		RefreshResult result = useCase.refreshLatest(feed);
		Marker marker = MarkerFactory.getMarker(feed.name());
		if (result instanceof RefreshResult.NoChange noChange) {
			if (noChange.getLatest() instanceof StatusItem.SuccessfulStatusItem) {
				LOG.info(marker, "They have the same content.");
				return HttpResponse.noContent();
			} else {
				LOG.info(marker, "They have the same error.");
				return HttpResponse.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION);
			}
		} else if (result instanceof RefreshResult.Created created) {
			LOG.info(marker, "It's new, stored...");
			return HttpResponse.created(created.getCurrent().getRetrievedDate().toString());
		} else if (result instanceof RefreshResult.Refreshed) {
			LOG.info(marker, "They're different, stored...");
			return HttpResponse.accepted();
		} else {
			throw new IllegalStateException("Unknown result: " + result);
		}
	}
}
