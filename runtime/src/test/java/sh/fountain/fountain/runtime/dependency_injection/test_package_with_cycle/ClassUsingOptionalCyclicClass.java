package sh.fountain.fountain.runtime.dependency_injection.test_package_with_cycle;

import sh.fountain.fountain.api.dependency_injection.Injectable;
import sh.fountain.fountain.runtime.dependency_injection.test_package_with_cycle.trivial_cycle.CyclicClass;

import java.util.Optional;

@Injectable
public class ClassUsingOptionalCyclicClass {

    public ClassUsingOptionalCyclicClass(Optional<CyclicClass> maybeCyclic) {

    }
}
