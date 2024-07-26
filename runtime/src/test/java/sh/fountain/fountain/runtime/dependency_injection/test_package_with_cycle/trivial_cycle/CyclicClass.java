package sh.fountain.fountain.runtime.dependency_injection.test_package_with_cycle.trivial_cycle;

import sh.fountain.fountain.api.dependency_injection.Injectable;

@Injectable
public class CyclicClass {

    public CyclicClass(CyclicClass cyclicClass) {

    }
}
