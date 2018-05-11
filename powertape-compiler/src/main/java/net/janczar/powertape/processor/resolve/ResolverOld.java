package net.janczar.powertape.processor.resolve;


import net.janczar.powertape.processor.TypeUtil;
import net.janczar.powertape.processor.inject.Injector;
import net.janczar.powertape.processor.inject.Injectors;
import net.janczar.powertape.processor.provide.ConstructorProvider;
import net.janczar.powertape.log.Log;
import net.janczar.powertape.processor.inject.InjectedField;
import net.janczar.powertape.processor.provide.Provider;
import net.janczar.powertape.processor.provide.ProviderDependency;
import net.janczar.powertape.processor.provide.ProviderScope;
import net.janczar.powertape.processor.provide.ProviderType;
import net.janczar.powertape.processor.provide.Providers;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Elements;

public class ResolverOld {

    public static boolean resolveField(final Elements elements, final Providers providers, final InjectedField injectedField) {

        Provider provider = providers.getProvider(injectedField.typeName);
        if (provider == null) {
            TypeElement providerClass = elements.getTypeElement(injectedField.typeName+"Provider");
            if (providerClass == null) {
                Log.error(String.format("Field %s.%s cannot be injected, class %s is not provided!", injectedField.containingClass.getSimpleName(), injectedField.name, injectedField.typeName), injectedField.containingClass);
                return false;
            }
            return true;
        }

        return resolveProvider(elements, providers, provider);
    }

    public static boolean verifyScope(final Providers providers, final Injectors injectors, final Provider provider) {
        if (provider.providerScope.scopeType == ProviderScope.Type.TYPE) {
            DeclaredType wantedScope = provider.providerScope.scopeClass;
            List<InjectionPath> paths = new ArrayList<>();
            InjectionStack stack = new InjectionStack();
            findInjectionPaths(providers, injectors, provider, stack, paths);
            List<InjectionPath> unsatisfied = new ArrayList<>();
            for (InjectionPath path : paths) {
                if (path.length() > 0 && !path.hasScope(wantedScope)) {
                    unsatisfied.add(path);
                }
            }
            if (unsatisfied.size() > 0) {
                StringBuilder errorMessage = new StringBuilder();
                errorMessage.append("Class requires scope of injectedType "+wantedScope+", however there are "+unsatisfied.size()+" injection paths that do not provide instance of that class:\n\n");
                for (int i=0; i<unsatisfied.size(); i++) {
                    errorMessage.append(String.valueOf(i+1)).append(") ").append(unsatisfied.get(i).toString()).append("\n");
                }
                Log.error(errorMessage.toString(), provider.element);
            }
        }
        return true;
    }

    private static void findInjectionPaths(final Providers providers, final Injectors injectors, final Provider provider, final InjectionStack stack, final List<InjectionPath> paths) {
        boolean anyParent = false;
        for (Provider parentProvider : providers.all()) {
            if (parentProvider != provider) {
                for (ProviderDependency dependency : parentProvider.dependencies) {
                    if (TypeUtil.sameType(dependency.dependencyClass, provider.providedClass)) {
                        stack.push(parentProvider, dependency);
                        findInjectionPaths(providers, injectors, parentProvider, stack, paths);
                        stack.pop();
                        anyParent = true;
                    }
                }
            }
        }
        for (Injector injector : injectors.all()) {
            for (InjectedField field : injector.injectedFields) {
                if (TypeUtil.sameType(field.type, provider.providedClass)) {
                    stack.push(injector, field);
                    findInjectionPaths(providers, injectors, injector, stack, paths);
                    stack.pop();
                    anyParent = true;
                }
            }
        }
        if (!anyParent) {
            paths.add(stack.toPath());
        }
    }

    private static void findInjectionPaths(final Providers providers, final Injectors injectors, final Injector injector, final InjectionStack stack, final List<InjectionPath> paths) {
        boolean anyParent = false;
        for (Provider provider : providers.all()) {
            if (TypeUtil.sameType(provider.providedClass, injector.injectedClass)) {
                findInjectionPaths(providers, injectors, provider, stack, paths);
                anyParent = true;
            }
        }
        if (!anyParent) {
            paths.add(stack.toPath());
        }
    }

    private static boolean resolveProvider(final Elements elements, final Providers providers, final Provider provider) {
        boolean result = true;
        for (int i=0; i<provider.dependencies.length; i++) {
            if (!resolveDependency(elements, providers, provider, provider.dependencies[i])) {
                result = false;
            }
        }
        return result;
    }

    private static boolean resolveDependency(final Elements elements, final Providers providers, final Provider sourceProvider, final ProviderDependency dependency) {

        String dependencyClassName = ((TypeElement)dependency.dependencyClass.asElement()).getQualifiedName().toString();
        Provider provider = providers.getProvider(dependencyClassName);
        if (provider == null) {
            TypeElement providerClass = elements.getTypeElement(dependencyClassName+"Provider");
            if (providerClass == null) {
                if (sourceProvider.type == ProviderType.CONSTRUCTOR) {
                    Log.error(String.format("Constructor's argument %s cannot be resolved, class %s is not provided!", dependency.name, dependencyClassName), ((ConstructorProvider) sourceProvider).element);
                } else {
                    Log.error(String.format("Provider's dependency %s cannot be resolved, class %s is not provided!",dependency.name,dependencyClassName), dependency.dependencyClass.asElement());
                }
                return false;
            } else {
                return true;
            }
        }

        return resolveProvider(elements, providers, provider);
    }

    private static class InjectionStack {
        private final List<InjectionPathItem> stack = new ArrayList<>();

        public void push(final Injector injector, final InjectedField field) {
            stack.add(new InjectorPathItem(injector, field));
        }

        public void push(final Provider provider, final ProviderDependency dependency) {
            stack.add(new ProviderPathItem(provider, dependency));
        }

        public void pop() {
            stack.remove(stack.size()-1);
        }

        public InjectionPath toPath() {
            InjectionPath path = new InjectionPath();
            path.path.addAll(stack);
            return path;
        }
    }

    private static class InjectionPath {
        private final List<InjectionPathItem> path = new ArrayList<>();

        public int length() {
            return path.size();
        }

        public String toString() {
            StringBuilder result = new StringBuilder();
            for (int i=path.size()-1; i>=0; i--) {
                Object item = path.get(i);
                if (i < path.size()-1) {
                    result.append("-    ");
                }
                if (item instanceof InjectorPathItem) {
                    InjectorPathItem injector = (InjectorPathItem)item;
                    result.append(injector.injector.injectedClass.toString()).append(" : field ").append(injector.field.name).append(" of injectedType ").append(injector.field.type.toString()).append("\n");
                } else if (item instanceof ProviderPathItem) {
                    ProviderPathItem provider = (ProviderPathItem)item;
                    result.append(provider.provider.providedClass.toString()).append(" : constructor's param ").append(provider.dependency.name).append(" of injectedType ").append(provider.dependency.dependencyClass.toString()).append("\n");
                }
            }
            return result.toString();
        }

        public boolean hasScope(final DeclaredType type) {
            boolean hasScope = false;
            for (Object item : path) {
                if (item instanceof InjectorPathItem) {
                    InjectorPathItem injector = (InjectorPathItem)item;
                    if (TypeUtil.sameType(injector.injector.injectedClass, type)) {
                        hasScope = true;
                    }
                }
            }
            return hasScope;
        }
    }

    private static class InjectionPathItem {
    }

    private static class ProviderPathItem extends InjectionPathItem {
        public final Provider provider;
        public final ProviderDependency dependency;
        public ProviderPathItem(final Provider provider, final ProviderDependency dependency) {
            this.provider = provider;
            this.dependency = dependency;
        }

    }

    private static class InjectorPathItem extends InjectionPathItem {
        public final Injector injector;
        public final InjectedField field;
        public InjectorPathItem(final Injector injector, final InjectedField field) {
            this.injector = injector;
            this.field = field;
        }
    }
}
