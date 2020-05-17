package nl.jkalter.discord.facade.client.member;

import discord4j.core.object.entity.Member;

public class MemberFacade implements IMember {
    private final Member member;

    public MemberFacade(Member member) {
        this.member = member;
    }

    public String getDisplayName() {
        return member.getDisplayName();
    }

}
