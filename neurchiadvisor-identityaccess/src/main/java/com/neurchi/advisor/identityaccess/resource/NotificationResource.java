package com.neurchi.advisor.identityaccess.resource;

import com.neurchi.advisor.common.media.NeurchiAdvisorMediaType;
import com.neurchi.advisor.common.notification.NotificationLog;
import com.neurchi.advisor.common.serializer.ObjectSerializer;
import com.neurchi.advisor.identityaccess.application.representation.NotificationLogRepresentation;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
@RequestMapping("/notifications")
public class NotificationResource extends AbstractResource {

    @GetMapping(produces = NeurchiAdvisorMediaType.ID_NEURCHI_TYPE)
    public ResponseEntity<StreamingResponseBody> getCurrentNotificationLog() {

        NotificationLog currentNotificationLog =
                this.notificationApplicationService()
                        .currentNotificationLog();

        if (currentNotificationLog == null) {
            return ResponseEntity.notFound().build();
        }

        return this.currentNotificationLogResponse(currentNotificationLog);
    }

    @GetMapping(path = "{notificationId}", produces = NeurchiAdvisorMediaType.ID_NEURCHI_TYPE)
    public ResponseEntity<StreamingResponseBody> getNotificationLog(
            final @PathVariable String notificationId) {

        NotificationLog notificationLog =
                this.notificationApplicationService()
                        .notificationLog(notificationId);

        if (notificationLog == null) {
            return ResponseEntity.notFound().build();
        }

        return this.notificationLogResponse(notificationLog);
    }

    private ResponseEntity<StreamingResponseBody> currentNotificationLogResponse(
            final NotificationLog currentNotificationLog) {

        NotificationLogRepresentation log =
                new NotificationLogRepresentation(currentNotificationLog);

        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add(HttpHeaders.LINK, this.selfLink(currentNotificationLog).toString());

        if (currentNotificationLog.hasPreviousNotificationLog()) {
            httpHeaders.add(HttpHeaders.LINK, this.previousLink(currentNotificationLog).toString());
        }

        return ResponseEntity
                .ok()
                .headers(httpHeaders)
                .cacheControl(CacheControl.maxAge(1, MINUTES))
                .body(response -> ObjectSerializer.instance().serialize(response, log));
    }

    private ResponseEntity<StreamingResponseBody> notificationLogResponse(
            final NotificationLog notificationLog) {

        NotificationLogRepresentation log =
                new NotificationLogRepresentation(notificationLog);

        HttpHeaders httpHeaders = new HttpHeaders();

        if (notificationLog.hasNextNotificationLog()) {
            httpHeaders.add(HttpHeaders.LINK, this.nextLink(notificationLog).toString());
        }

        httpHeaders.add(HttpHeaders.LINK, this.selfLink(notificationLog).toString());

        if (notificationLog.hasPreviousNotificationLog()) {
            httpHeaders.add(HttpHeaders.LINK, this.previousLink(notificationLog).toString());
        }

        return ResponseEntity
                .ok()
                .headers(httpHeaders)
                .cacheControl(CacheControl.maxAge(1, HOURS))
                .body(response -> ObjectSerializer.instance().serialize(response, log));
    }

    private Link linkFor(
            final LinkRelation rel,
            final String id) {

        Link link = null;

        if (id != null) {
            link = linkTo(methodOn(NotificationResource.class)
                    .getNotificationLog(id))
                    .withRel(rel);
        }

        return link;
    }

    private Link nextLink(final NotificationLog notificationLog) {
        return this.linkFor(
                IanaLinkRelations.NEXT,
                notificationLog.nextNotificationLogId());
    }

    private Link previousLink(final NotificationLog notificationLog) {
        return this.linkFor(
                IanaLinkRelations.PREVIOUS,
                notificationLog.previousNotificationLogId());
    }

    private Link selfLink(final NotificationLog notificationLog) {
        return this.linkFor(
                IanaLinkRelations.SELF,
                notificationLog.notificationLogId());
    }
}
