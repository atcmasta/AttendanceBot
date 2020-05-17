package nl.jkalter.discord.facade.author;

import discord4j.core.object.entity.Member;
import nl.jkalter.discord.facade.role.Role;

import java.util.List;
import java.util.Objects;

public class Author implements IAuthor {

    final Member member;

    public Author(Member member) {
        this.member = member;
    }

    @Override
    public String getAuthorName() {
        return member.getUsername();
    }

    @Override
    public void sendPrivateMessage(String message) {
        Objects.requireNonNull(member.getPrivateChannel().block()).createMessage(message);
    }

    @Override
    public long getAuthorId() {
        return member.getId().asLong();
    }

    @Override
    public List<Role> getRoles() {
        return member.getRoles().map(Role::new).collectList().block();
    }
}
