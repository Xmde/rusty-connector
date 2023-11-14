package group.aelysium.rustyconnector.plugin.velocity.lib.family.ranked_family.handlers;

import com.velocitypowered.api.proxy.server.ServerInfo;
import group.aelysium.rustyconnector.core.lib.packets.variants.RankedGameEndPacket;
import group.aelysium.rustyconnector.plugin.velocity.central.Tinder;
import group.aelysium.rustyconnector.plugin.velocity.lib.family.bases.BaseFamily;
import group.aelysium.rustyconnector.plugin.velocity.lib.family.ranked_family.RankedFamily;
import group.aelysium.rustyconnector.toolkit.core.packet.IPacket;
import group.aelysium.rustyconnector.toolkit.core.packet.PacketHandler;

public class RankedUpdateHandler implements PacketHandler {
    protected Tinder api;

    public RankedUpdateHandler(Tinder api) {
        this.api = api;
    }

    @Override
    public <TPacket extends IPacket> void execute(TPacket genericPacket) throws Exception {
        RankedGameEndPacket packet = (RankedGameEndPacket) genericPacket;

        BaseFamily family = api.services().family().find(packet.familyName());
        if(family == null) return;
        if(!(family instanceof RankedFamily)) return;

        ServerInfo serverInfo = new ServerInfo(
                packet.serverName(),
                packet.address()
        );
        if(!family.containsServer(serverInfo)) return;

        ((RankedFamily) family).gameManager().end(packet.uuid());
    }
}