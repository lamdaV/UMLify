package utility;

public interface IExpander<A, B> {

    static <A> Iterable<A> merge(Iterable<Iterable<A>> ls) {
        IExpander<Iterable<A>, A> merger = (x) -> x;
        return merger.expand(ls);
    }

    /**
     * @param data
     * @return the iterable of B that we want to expand to
     */
    Iterable<B> expand(A data);

    /**
     * @param iterable The iterable we want to filter
     * @return the aggregate iterable of all elements expanded
     */
    default Iterable<B> expand(Iterable<? extends A> iterable) {
        return () -> new ExpandIterator<A, B>(IExpander.this, iterable);
    }
}
