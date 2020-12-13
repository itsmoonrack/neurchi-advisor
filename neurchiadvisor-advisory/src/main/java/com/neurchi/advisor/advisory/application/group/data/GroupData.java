package com.neurchi.advisor.advisory.application.group.data;

import java.time.LocalDate;

public final class GroupData {

    private String name;
    private String cover;
    private LocalDate creationDate;
    private String description;
    private boolean verified;
    private int memberCount;
    private int contributionCount;
    private int opinionCount;
    private double completeness;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(final String cover) {
        this.cover = cover;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(final LocalDate created) {
        this.creationDate = created;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(final boolean verified) {
        this.verified = verified;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(final int memberCount) {
        this.memberCount = memberCount;
    }

    public int getOpinionCount() {
        return opinionCount;
    }

    public void setOpinionCount(final int opinionCount) {
        this.opinionCount = opinionCount;
    }

    public int getContributionCount() {
        return contributionCount;
    }

    public void setContributionCount(final int contributionCount) {
        this.contributionCount = contributionCount;
    }

    public double getCompleteness() {
        return completeness;
    }

    public void setCompleteness(final double completeness) {
        this.completeness = completeness;
    }

    public String getCompletenessPercentage() {
        return (int) (completeness * 100) + "%";
    }

}
