package sh.fountain.fountain.runtime.dependency_injection.test_package_with_generic_injectables;

import sh.fountain.fountain.api.dependency_injection.Injectable;

@Injectable
public class ProviderOne implements GenericProviderInterface<String, Integer> {

}
