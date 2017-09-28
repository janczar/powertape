package net.janczar.powertape.processor.resolve;


import net.janczar.powertape.processor.provide.ConstructorProvider;
import net.janczar.powertape.processor.Log;
import net.janczar.powertape.processor.inject.InjectedField;
import net.janczar.powertape.processor.provide.Provider;
import net.janczar.powertape.processor.provide.ProviderDependency;
import net.janczar.powertape.processor.provide.ProviderType;
import net.janczar.powertape.processor.provide.Providers;

import javax.lang.model.element.TypeElement;

public class Resolver {

    public static boolean resolve(final Providers providers, final InjectedField injectedField) {

        Provider provider = providers.getProvider(injectedField.className);
        if (provider == null) {
            Log.error(String.format("Field %s cannot be injected, class %s is not provided!", injectedField.name, injectedField.className), injectedField.classElement);
            return true;
        }

        return resolve(providers, provider);
    }

    private static boolean resolve(final Providers providers, final Provider provider) {
        boolean result = true;
        for (int i=0; i<provider.dependencies.length; i++) {
            if (!resolve(providers, provider, provider.dependencies[i])) {
                result = false;
            }
        }
        return result;
    }

    private static boolean resolve(final Providers providers, final Provider sourceProvider, final ProviderDependency dependency) {

        String dependencyClassName = ((TypeElement)dependency.dependencyClass.asElement()).getQualifiedName().toString();
        Provider provider = providers.getProvider(dependencyClassName);
        if (provider == null) {
            if (sourceProvider.type == ProviderType.CONSTRUCTOR) {
                Log.error(String.format("Constructor's argument %s cannot be resolved, class %s is not provided!", dependency.name, dependencyClassName), ((ConstructorProvider)sourceProvider).element);
            }
            return false;
        }

        return resolve(providers, provider);
    }
}
