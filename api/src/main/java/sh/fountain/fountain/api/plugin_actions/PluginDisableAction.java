package sh.fountain.fountain.api.plugin_actions;

/**
 * Defines an action to be run when the plugin is disabled by Paper.
 *  *
 *  * @see <a href="https://docs.papermc.io/paper/dev/how-do-plugins-work#plugin-lifecycle">Paper Plugin Lifecycle</a>
 *  */
public interface PluginDisableAction {

    /**
     * Method run when the plugin is disabled
     *
     * @throws Exception wraps any exception thrown by contained code
     */
    void onPluginDisable() throws Exception;
}
