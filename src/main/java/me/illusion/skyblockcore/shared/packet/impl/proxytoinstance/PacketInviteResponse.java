package me.illusion.skyblockcore.shared.packet.impl.proxytoinstance;

import lombok.Getter;
import me.illusion.skyblockcore.shared.data.IslandInvite;
import me.illusion.skyblockcore.shared.packet.data.ProxyToServerPacket;

@Getter
public class PacketInviteResponse extends ProxyToServerPacket {

    private final IslandInvite invite;
    private final Response response;

    public PacketInviteResponse(byte[] bytes) {
        super(bytes);

        invite = new IslandInvite(readUUID(), readUUID(), readString());
        response = Response.getResponse(readInt());
    }

    public PacketInviteResponse(String proxyId, String targetServer, IslandInvite invite, Response response) {
        super(proxyId, targetServer);

        this.invite = invite;
        this.response = response;

        writeUUID(invite.getInviteId());
        writeUUID(invite.getSender());
        writeString(invite.getTarget());
        writeInt(response.ordinal());
    }

    public enum Response {
        RESPONSE_NOT_FOUND,
        PLAYER_NOT_FOUND,
        INVITE_SENT,
        INVITE_ACCEPTED,
        INVITE_DENIED;

        private static final Response[] VALUES = values();

        public static Response getResponse(int id) {
            return VALUES[id];
        }

    }
}
