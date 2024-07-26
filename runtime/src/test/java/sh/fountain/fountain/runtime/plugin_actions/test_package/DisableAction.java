package sh.fountain.fountain.runtime.plugin_actions.test_package;

import sh.fountain.fountain.api.dependency_injection.Injectable;
import sh.fountain.fountain.api.plugin_actions.PluginDisableAction;

@Injectable
public class DisableAction implements PluginDisableAction {

    @Override
    public void onPluginDisable() {
    }

}
