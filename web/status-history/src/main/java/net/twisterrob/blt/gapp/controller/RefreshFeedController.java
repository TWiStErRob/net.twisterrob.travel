package net.twisterrob.blt.gapp.controller;

import javax.annotation.Nonnull;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import jakarta.servlet.http.*;

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
	public void refreshFeed(@QueryValue("feed") @Nonnull Feed feed, HttpServletResponse resp) {
		RefreshResult result = useCase.refreshLatest(feed);
		Marker marker = MarkerFactory.getMarker(feed.name());
		if (result instanceof RefreshResult.NoChange noChange) {
			if (noChange.getLatest() instanceof StatusItem.SuccessfulStatusItem) {
				LOG.info(marker, "They have the same content.");
				resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
			} else {
				LOG.info(marker, "They have the same error.");
				resp.setStatus(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION);
			}
		} else if (result instanceof RefreshResult.Created) {
			LOG.info(marker, "It's new, stored...");
			resp.setStatus(HttpServletResponse.SC_CREATED);
		} else if (result instanceof RefreshResult.Refreshed) {
			LOG.info(marker, "They're different, stored...");
			resp.setStatus(HttpServletResponse.SC_ACCEPTED);
		} else {
			throw new IllegalStateException("Unknown result: " + result);
		}
	}
}
