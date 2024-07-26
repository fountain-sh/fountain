package sh.fountain.fountain.runtime.dependency_injection.test_package_with_cycle;

import sh.fountain.fountain.api.dependency_injection.Injectable;
import sh.fountain.fountain.runtime.dependency_injection.test_package_with_cycle.simple_cycle.FirstClass;

@Injectable
public class MultiParamClass {

    public MultiParamClass(NonCyclicClass nonCyclicClass, FirstClass firstClass) {

    }
}
