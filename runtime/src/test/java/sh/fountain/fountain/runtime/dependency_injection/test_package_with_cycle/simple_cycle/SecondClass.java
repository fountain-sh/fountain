package sh.fountain.fountain.runtime.dependency_injection.test_package_with_cycle.simple_cycle;

import sh.fountain.fountain.api.dependency_injection.Injectable;

@Injectable
public class SecondClass {

    public SecondClass(ThirdClass thirdClass) {

    }
}
