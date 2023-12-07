package group.aelysium.rustyconnector.plugin.velocity.lib.dynamic_teleport.tpa;

import com.velocitypowered.api.proxy.Player;
import group.aelysium.rustyconnector.toolkit.velocity.dynamic_teleport.tpa.ITPAHandler;
import group.aelysium.rustyconnector.plugin.velocity.central.Tinder;
import group.aelysium.rustyconnector.plugin.velocity.lib.lang.ProxyLang;

import java.util.List;
import java.util.Vector;

public class TPAHandler implements ITPAHandler<TPARequest> {
    private final Vector<TPARequest> requests = new Vector<>();

    public TPARequest findRequest(Player sender, Player target) {
        return this.requests.stream()
                .filter(request -> request.sender().equals(sender) && request.target().equals(target))
                .findFirst()
                .orElse(null);
    }

    public TPARequest findRequestSender(Player sender) {
        return this.requests.stream()
                .filter(request -> request.sender().equals(sender))
                .findFirst()
                .orElse(null);
    }

    public List<TPARequest> findRequestsForTarget(Player target) {
        return this.requests.stream()
                .filter(request -> request.target().equals(target))
                .toList();
    }

    public TPARequest newRequest(Player sender, Player target) {
        Tinder api = Tinder.get();
        TPAService tpaService = api.services().dynamicTeleport().orElseThrow()
                                   .services().tpa().orElseThrow();

        TPARequest tpaRequest = new TPARequest(sender, target, tpaService.settings().expiration());
        requests.add(tpaRequest);

        return tpaRequest;
    }

    public void remove(TPARequest request) {
        this.requests.remove(request);
    }

    public void clearExpired() {
        this.requests.stream().filter(TPARequest::expired).forEach(request -> {
            request.sender().sendMessage(ProxyLang.TPA_REQUEST_EXPIRED.build(request.target().getUsername()));
            this.requests.remove(request);
        });
    }
    public List<TPARequest> dump() {
        return this.requests;
    }

    public void decompose() {
        this.requests.clear();
    }
}
