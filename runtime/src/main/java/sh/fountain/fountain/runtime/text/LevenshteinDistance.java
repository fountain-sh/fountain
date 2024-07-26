package sh.fountain.fountain.runtime.text;

public final class LevenshteinDistance {

    private LevenshteinDistance() {

    }

    /**
     * This method computes the <a href="https://wikipedia.org/wiki/Levenshtein_distance">Levenshtein distance</a>
     * between the strings {@code left} and {@code right}.
     * <br>
     * The Levenshtein distance is a metric measuring the distance between to strings in terms of single-character
     * edits (insertions, deletions, substitutions) required to change one word into the other.
     *
     * @param left left string
     * @param right right string
     * @return Levenshtein distance of left and right
     */
    public static int compare(final String left, final String right) {
        final int lengthLeft = left.length() + 1;
        final int lengthRight = right.length() + 1;

        int[] cost = new int[lengthLeft];
        int[] newCost = new int[lengthLeft];

        for (int i = 0; i < lengthLeft; i++) {
            cost[i] = i;
        }

        for (int rightIndex = 1; rightIndex < lengthRight; rightIndex++) {
            newCost[0] = rightIndex;

            for (int leftIndex = 1; leftIndex < lengthLeft; leftIndex++) {
                final int match = match(left.charAt(leftIndex - 1), right.charAt(rightIndex - 1));

                final int replaceCost = cost[leftIndex - 1] + match;
                final int insertCost  = cost[leftIndex] + 1;
                final int deleteCost  = newCost[leftIndex - 1] + 1;

                newCost[leftIndex] = min(replaceCost, insertCost, deleteCost);
            }

            final int[] swap = cost;
            cost = newCost;
            newCost = swap;
        }

        return cost[lengthLeft - 1];
    }

    private static int match(char left, char right) {
        return (left == right) ? 0 : 1;
    }

    private static int min(int replaceCost, int insertCost, int deleteCost) {
        return Math.min(Math.min(replaceCost, insertCost), deleteCost);
    }
}
