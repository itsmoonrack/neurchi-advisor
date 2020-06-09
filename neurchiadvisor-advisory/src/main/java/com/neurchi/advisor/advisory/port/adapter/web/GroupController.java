package com.neurchi.advisor.advisory.port.adapter.web;

import com.neurchi.advisor.common.application.data.SearchQuery;
import com.neurchi.advisor.subscription.application.data.GroupData;
import com.neurchi.advisor.subscription.application.group.GroupQueryService;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public final class GroupController {

    private final GroupQueryService groupQueryService;

    public GroupController(final GroupQueryService groupQueryService) {
        this.groupQueryService = groupQueryService;
    }

    @RequestMapping(value = "/apps/group/list-default.html", method = GET)
    public String groupsData(
            final Model model,
            final @ModelAttribute("search") SearchQuery search,
            final Principal principal,
            final Pageable pageable) {

        final Page<GroupData> page = this.groupQueryService.allGroupsData(search, pageable);

        LoggerFactory.getLogger(GroupController.class).info("Pageable: {}, totalPage {}", page, page.getTotalPages());

        model.addAttribute("page", page);

        return "apps/group/list-default";
    }

    @RequestMapping(value = "/apps/group/add-group.html", method = GET)
    public String createGroup(final Principal principal) {

        return "apps/group/add-group";
    }

}
