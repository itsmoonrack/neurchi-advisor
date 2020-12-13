package com.neurchi.advisor.advisory.application.group;

import com.neurchi.advisor.advisory.application.group.data.GroupData;
import com.neurchi.advisor.common.application.data.SearchQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class GroupQueryService {

    public Page<GroupData> allGroupsData(final SearchQuery search, final Pageable pageable) {

        final GroupData group1 = new GroupData();
        group1.setName("Neurchi de Finances");
        group1.setCover("https://scontent.xx.fbcdn.net/v/t31.0-8/s720x720/18077249_10155365905701392_7071177520041339475_o.jpg?_nc_cat=110&_nc_ht=scontent.xx&oh=121afbeb333f57e163fb300359367542&oe=5D59934D");
//        group1.setCover("https://scontent.xx.fbcdn.net/v/t1.0-0/c20.0.50.50a/p50x50/18158020_10155365905701392_7071177520041339475_n.jpg?_nc_cat=110&_nc_ht=scontent.xx&oh=b4412b7e68660a4fa6a23549a9be12c6&oe=5D5CD6EF");
        group1.setCreationDate(LocalDate.of(2017, 4, 19));
        group1.setMemberCount(804);
        group1.setContributionCount(73);
        group1.setOpinionCount(7);
        group1.setCompleteness(0.65);
        group1.setVerified(true);

        final GroupData group2 = new GroupData();
        group2.setName("Neurchi de cuisine");
        group2.setCover("https://scontent.xx.fbcdn.net/v/t1.0-9/18222575_10212168802911320_7658136355017871614_n.jpg?_nc_cat=109&_nc_ht=scontent.xx&oh=b32067d62aab9013e9710d4f379fc220&oe=5D6C8F89");
//        group2.setCover("https://scontent.xx.fbcdn.net/v/t1.0-0/c18.0.50.50a/p50x50/18222575_10212168802911320_7658136355017871614_n.jpg?_nc_cat=109&_nc_ht=scontent.xx&oh=1bef50f8103db58f696dc34b64f146af&oe=5D5FA5DD");
        group2.setCreationDate(LocalDate.of(2017, 5, 5));
        group2.setDescription("Partager vos petites recettes neurchi OKLM, l'OC est privilégié bien entendu ;)");
        group2.setMemberCount(1398);
        group2.setContributionCount(36);
        group2.setOpinionCount(3);
        group2.setCompleteness(0.86);

        final List<GroupData> groups = Arrays.asList(group1, group2);

        return new PageImpl<>(groups, pageable, groups.size());
    }

    public Optional<String> groupIdOfExclusiveOwner(final String tenantId, final String exclusiveOwner) {
        return Optional.empty();
    }

}
