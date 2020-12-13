package com.neurchi.advisor.advisory.port.adapter.web;

import com.neurchi.advisor.advisory.application.data.GroupOfUserData;
import com.neurchi.advisor.advisory.application.group.GroupApplicationService;
import com.neurchi.advisor.advisory.application.group.NewGroupCommand;
import com.neurchi.advisor.common.port.adapter.service.facebook.types.UserGroup;
import com.restfb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.restfb.Version.VERSION_7_0;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.StreamSupport.stream;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/me")
public final class MeResource {

    @Value("${security.oauth2.client.client-id}")
    private String appId;

    @Value("${security.oauth2.client.client-secret}")
    private String appSecret;

    private final Logger logger = LoggerFactory.getLogger(MeResource.class);

    private final GroupApplicationService groupApplicationService;

    MeResource(final GroupApplicationService groupApplicationService) {
        this.groupApplicationService = groupApplicationService;
    }

    @RequestMapping(method = POST, path = "subscriptions", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> groupsToSubscribe(
            @RequestBody final List<String> groupIds,
            final Principal principal) {

        if (principal instanceof OAuth2Authentication authentication) {
            final OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
            final FacebookClient facebookClient = new DefaultFacebookClient(details.getTokenValue(), this.appSecret, VERSION_7_0);
            final Connection<UserGroup> groupConnection = facebookClient.fetchConnection("me/groups", UserGroup.class,
                    Parameter.with("limit", 1000),
                    Parameter.with("fields", "id, name, description, created_time, member_count, administrator, privacy, cover, icon"));

            final Map<String, UserGroup> groupsById = groupConnection.getData().stream().collect(toMap(UserGroup::getId, Function.identity()));

            final FacebookClient.AccessToken accessToken = facebookClient.obtainExtendedAccessToken(this.appId, this.appSecret, details.getTokenValue());

            final LocalDateTime expiresIn = LocalDateTime.ofInstant(accessToken.getExpires().toInstant(), ZoneId.systemDefault());

            groupIds.stream()
                    .filter(groupsById::containsKey)
                    .map(groupsById::get)
                    .forEach(userGroup ->
                        this.groupApplicationService
                                .newGroupWithSubscription(
                                        new NewGroupCommand(
                                                "Neurchi",
                                                userGroup.getId(),
                                                principal.getName(),
                                                userGroup.getName(),
                                                userGroup.getDescription(),
                                                userGroup.getMemberCount(),
                                                userGroup.getCreatedTime().toInstant().atZone(ZoneOffset.UTC.normalized()).toLocalDateTime(),
                                                userGroup.getCover() != null ? userGroup.getCover().getSource() : null)));

            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @RequestMapping(method = POST, path = "groups", consumes = APPLICATION_FORM_URLENCODED_VALUE, produces = APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<GroupsOfUserData> availableGroupsOfCurrentUser(
            @RequestParam(required = false, name = "pagination[page]", defaultValue = "1") final int page,
            @RequestParam(required = false, name = "pagination[pages]", defaultValue = "1") final int pages,
            @RequestParam(required = false, name = "pagination[perpage]", defaultValue = "10") final int limit,
            @RequestParam(required = false, name = "sort[field]", defaultValue = "status") final String field,
            @RequestParam(required = false, name = "sort[sort]", defaultValue = "asc") final String direction,
            final Principal principal) {

        if (principal instanceof OAuth2Authentication) {
            OAuth2Authentication authentication = (OAuth2Authentication) principal;
            OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
            FacebookClient facebookClient = new DefaultFacebookClient(details.getTokenValue(), this.appSecret, Version.VERSION_6_0);
//            User user = facebookClient.fetchObject("me", User.class, Parameter.with("fields", "first_name, installed, install_type"));
//            Connection<UserPermission> permissions = facebookClient.fetchConnection("me/permissions", UserPermission.class);
            Connection<UserGroup> groupConnection = facebookClient.fetchConnection("me/groups", UserGroup.class,
                    Parameter.with("limit", 100),
                    Parameter.with("fields", "id, name, description, member_count, administrator, privacy, picture.width(40).height(40), icon"));

            final GroupsOfUserMetaData groupsOfUserMetaData = new GroupsOfUserMetaData();
            groupsOfUserMetaData.setPage(page);
            groupsOfUserMetaData.setPages(pages);
            groupsOfUserMetaData.setPerpage(limit);
            groupsOfUserMetaData.setTotal(groupConnection.getData().size());

//            final AtomicInteger rand = new AtomicInteger(0);


            // SJW: zucked: 1491402944259898
//            UserGroup memeMaghrebin = facebookClient.fetchObject("715121222212324", UserGroup.class, Parameter.with("fields", "id, name, description, member_count, privacy, cover, icon"));
//            UserGroup finance = facebookClient.fetchObject("298448690577160", UserGroup.class, Parameter.with("fields", "id, name, description, member_count, privacy, cover, icon"));
//            try {
//                UserGroup game = facebookClient.fetchObject("401831656815056", UserGroup.class);
//            } catch (FacebookGraphException e) {
//                this.logger.error("Unable to get Neurchi de Games", e);
//            }
//
//            JsonObject groupList = facebookClient.fetchObjects(Arrays.asList("715121222212324", "298448690577160", "401831656815056"), JsonObject.class, Parameter.with("fields", "id, name, description, member_count, privacy, cover, icon"));

            final GroupsOfUserData groupsOfUserData = new GroupsOfUserData();
            groupsOfUserData.setMeta(groupsOfUserMetaData);
            groupsOfUserData.setData(stream(groupConnection.spliterator(), false)
                    .flatMap(Collection::stream)
                    .sorted(Comparator.comparing(UserGroup::isAdministrator))
                    .map(group -> {
                        GroupOfUserData groupOfUserData = new GroupOfUserData();
                        groupOfUserData.setId(group.getId());
                        groupOfUserData.setName(group.getName());
                        groupOfUserData.setDescription(textOverflowEllipsis(group.getDescription(), 100));
                        groupOfUserData.setPicture(group.getPicture().getUrl());
                        groupOfUserData.setStatus("Pending");
                        return groupOfUserData;
                    })
                    .collect(Collectors.toList()));

            return ResponseEntity.ok(groupsOfUserData);
        }

        return ResponseEntity.notFound().build();
    }

    private String textOverflowEllipsis(final String text, final int length) {
        if (text == null)
            return "";
        return text.length() > length ? text.substring(0, length - 4) + " ..." : text;
    }

    private Comparator<GroupOfUserData> comparing(final String field, final String direction) {
        final Comparator<GroupOfUserData> comparator;

        switch (field) {
            case "status":
                comparator = Comparator.comparing(GroupOfUserData::getStatus);
                break;
            case "name":
                comparator = Comparator.comparing(GroupOfUserData::getName);
                break;
            default:
                comparator = Comparator.comparing(GroupOfUserData::getId);
        }

        return "desc".equals(direction) ? comparator.reversed() : comparator;
    }

    private static final class GroupsOfUserData {
        private GroupsOfUserMetaData meta;
        private List<GroupOfUserData> data;

        public GroupsOfUserMetaData getMeta() {
            return meta;
        }

        public void setMeta(final GroupsOfUserMetaData meta) {
            this.meta = meta;
        }

        public List<GroupOfUserData> getData() {
            return data;
        }

        public void setData(final List<GroupOfUserData> data) {
            this.data = data;
        }
    }

    private static final class GroupsOfUserMetaData {
        private int page;
        private int pages;
        private int perpage;
        private int total;

        public int getPage() {
            return page;
        }

        public void setPage(final int page) {
            this.page = page;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(final int pages) {
            this.pages = pages;
        }

        public int getPerpage() {
            return perpage;
        }

        public void setPerpage(final int perpage) {
            this.perpage = perpage;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(final int total) {
            this.total = total;
        }
    }


}
