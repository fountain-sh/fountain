package sh.fountain.fountain.runtime.command.model;

public interface CommandModelVisitor<T> {
    T visit(CompositeCommandModel command);

    T visit(SingleCommandModel command);
}
