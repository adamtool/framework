package uniolunisaar.adam.util;

import java.util.HashMap;
import java.util.Map;
import uniol.apt.adt.extension.Extensible;
import uniol.apt.adt.extension.ExtensionProperty;
import uniolunisaar.adam.tools.Logger;

/**
 *
 * @author Manuel Gieseking
 */
public class ExtensionManagement {

    private static ExtensionManagement instance = null;

    public static ExtensionManagement getInstance() {
        if (instance == null) {
            instance = new ExtensionManagement();
        }
        return instance;
    }

    private final Map<String, IAdamExtensions> extensionKeys;

    private ExtensionManagement() {
        extensionKeys = new HashMap<>();
    }

    public void registerExtensions(IAdamExtensions... values) {
        registerExtensions(true, values);
    }

    public void registerExtensions(boolean overwrite, IAdamExtensions... values) {
        for (IAdamExtensions value : values) {
            if (!overwrite && extensionKeys.containsKey(value.name())) {
                Logger.getInstance().addWarning("The value '" + value.name() + "' is already available.");
            } else {
                extensionKeys.put(value.name(), value);
            }
        }
    }

    public <Ex extends Extensible> boolean hasExtension(Ex extensible, IAdamExtensions extensionKey) {
        return extensible.hasExtension(extensionKey.name());
    }

    public <Ex extends Extensible> void putExtension(Ex extensible, IAdamExtensions extensionKey, Object object, ExtensionProperty... extensionProperty) {
        extensible.putExtension(extensionKey.name(), object);
    }

    public <Ex extends Extensible> void removeExtension(Ex extensible, IAdamExtensions extensionKey) {
        extensible.removeExtension(extensionKey.name());
    }

    /**
     * Returns the extension with key 'extensionKey' of the 'extensible'. The
     * TYPE provided by the clazz must match the type stored under
     * 'extensionKey'.
     *
     * @param <Ex>
     * @param <TYPE>
     * @param extensible
     * @param extensionKey
     * @param clazz
     * @return
     */
    public <Ex extends Extensible, TYPE> TYPE getExtension(Ex extensible, IAdamExtensions extensionKey, Class<TYPE> clazz) {
        @SuppressWarnings("unchecked")
        TYPE obj = (TYPE) extensible.getExtension(extensionKey.name());
        return obj;
    }

}
