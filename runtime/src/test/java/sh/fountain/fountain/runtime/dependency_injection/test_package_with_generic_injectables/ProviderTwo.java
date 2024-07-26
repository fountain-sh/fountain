package sh.fountain.fountain.runtime.dependency_injection.test_package_with_generic_injectables;

import sh.fountain.fountain.api.dependency_injection.Injectable;

import java.util.Optional;

@Injectable
public class ProviderTwo implements GenericProviderInterface<Optional<String>, Integer> {

}
