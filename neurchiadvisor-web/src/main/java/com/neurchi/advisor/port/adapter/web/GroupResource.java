package com.neurchi.advisor.port.adapter.web;

import com.neurchi.advisor.application.data.GroupOfUserData;
import com.neurchi.advisor.common.port.adapter.facebook.UserGroup;
import com.restfb.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/me")
public final class GroupResource {

    @Value("${security.oauth2.client.client-secret}")
    private String appSecret;

    @RequestMapping(method = POST, path = "subscriptions", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> groupsToSubscribe(
            @RequestBody final List<String> groupIds,
            final Principal principal) {

        System.out.println(groupIds);

        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = POST, path = "groups", consumes = APPLICATION_FORM_URLENCODED_VALUE, produces = APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<GroupsOfUserData> availableGroupsOfCurrentUser(
            @RequestParam(required = false, name = "pagination[page]", defaultValue = "1") final int page,
            @RequestParam(required = false, name = "pagination[pages]", defaultValue = "1") final int pages,
            @RequestParam(required = false, name = "pagination[perpage]", defaultValue = "10") final int limit,
            @RequestParam(required = false, name = "sort[field]", defaultValue = "status") final String field,
            @RequestParam(required = false, name = "sort[sort]", defaultValue = "asc") final String direction,
            final Principal principal) {

        if (principal instanceof OAuth2Authentication) {
            OAuth2Authentication authentication = (OAuth2Authentication) principal;
            OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
            FacebookClient facebookClient = new DefaultFacebookClient(details.getTokenValue(), this.appSecret, Version.VERSION_3_3);
//            User user = facebookClient.fetchObject("me", User.class, Parameter.with("fields", "first_name, installed, install_type"));
//            Connection<UserPermission> permissions = facebookClient.fetchConnection("me/permissions", UserPermission.class);
            Connection<UserGroup> groupConnection = facebookClient.fetchConnection("me/groups", UserGroup.class,
                    Parameter.with("limit", 1000),
                    Parameter.with("admin_only", true),
                    Parameter.with("fields", "id, name, description, member_count, administrator, privacy, cover, icon"));

            final GroupsOfUserMetaData groupsOfUserMetaData = new GroupsOfUserMetaData();
            groupsOfUserMetaData.setPage(page);
            groupsOfUserMetaData.setPages(pages);
            groupsOfUserMetaData.setPerpage(limit);
            groupsOfUserMetaData.setTotal(groupConnection.getData().size());

            final AtomicInteger rand = new AtomicInteger(0);

            final GroupsOfUserData groupsOfUserData = new GroupsOfUserData();
            groupsOfUserData.setMeta(groupsOfUserMetaData);
            groupsOfUserData.setData(groupConnection.getData().stream()
                    .map(group -> {
                        GroupOfUserData groupOfUserData = new GroupOfUserData();
                        groupOfUserData.setId(group.getId());
                        groupOfUserData.setName(group.getName());
                        groupOfUserData.setDescription(textOverflowEllipsis(group.getDescription(), 150));
                        groupOfUserData.setCover(group.getCover() != null ? group.getCover().getSource() : group.getIcon());
                        groupOfUserData.setStatus("Pending");
                        return groupOfUserData; })
                    .sorted(comparing(field, direction))
                    .skip((page - 1) * limit)
                    .limit(limit)
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
