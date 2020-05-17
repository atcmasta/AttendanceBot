package nl.jkalter.discord.facade.role;

public class Role implements IRole {
    private final discord4j.core.object.entity.Role wrappedRole;

    public Role(discord4j.core.object.entity.Role wrappedRole) {
        this.wrappedRole = wrappedRole;
    }

    @Override
    public String getName() {
        return wrappedRole.getName();
    }
}
