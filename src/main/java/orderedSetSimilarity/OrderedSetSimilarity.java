package orderedSetSimilarity;

import java.util.*;

public class OrderedSetSimilarity {

    /**
     * compute the similarity between 2 lists more efficiently by combining Jaccard, Kendall's, and Displacement similarity.
     * @param list1
     * @param list2
     * @return
     */
    public static double orderedSetSimilarity(List<Integer> list1, List<Integer> list2) {
        if (list1.isEmpty() && list2.isEmpty()) {
            // empty set always equals itself
            return 1.0;
        }

        double jaccardSimilarity;
        double kendallSimilarity;
        double displacementSimilarity;

        // map each item to its index in each list
        Map<Integer, Integer> indexMap1 = new HashMap<>();
        Map<Integer, Integer> indexMap2 = new HashMap<>();
        for (int i = 0; i < list1.size(); i++) {
            indexMap1.put(list1.get(i), i);
        }
        for (int i = 0; i < list2.size(); i++) {
            indexMap2.put(list2.get(i), i);
        }


        // intersection similarity
        int intersectionSize = (int) indexMap1.keySet().stream().filter(indexMap2::containsKey).count();
        int unionSize = indexMap1.size() + indexMap2.size() - intersectionSize;
        jaccardSimilarity = (double) intersectionSize / unionSize;


        // ordered pair similarity
        if (intersectionSize < 2) {
            // if there are no common pairs, the lists are incomparable
            kendallSimilarity = 1.0;
        } else {
            int concordant = 0;
            int discordant = 0;
            for (int i = 0; i < list1.size() - 1; i++) {
                Integer indexOfI = indexMap2.get(list1.get(i));
                if (indexOfI == null) {
                    continue;
                }
                for (int j = i + 1; j < list1.size(); j++) {
                    Integer indexOfJ = indexMap2.get(list1.get(j));
                    if (indexOfJ == null) {
                        continue;
                    }

                    if (indexOfI < indexOfJ) {
                        concordant++;
                    } else {
                        discordant++;
                    }
                }
            }

            kendallSimilarity = 0.5 * (1 + (concordant - discordant) / (0.5 * intersectionSize * (intersectionSize - 1)));
        }


        // displacement similarity
        int maxListSize = Math.max(list1.size(), list2.size());
        if (maxListSize <= 1 || intersectionSize == 0) {
            // either the lists have no common element or are equal
            displacementSimilarity = 1.0;
        } else {
            double totalPenalty = 0.0;
            for (Integer element : indexMap1.keySet()) {
                if (indexMap2.containsKey(element)) {
                    totalPenalty += Math.abs(indexMap1.get(element) - indexMap2.get(element));
                }
            }

            double maxPenalty = maxListSize * intersectionSize;
            displacementSimilarity =  1.0 - (totalPenalty / maxPenalty);
        }

        return jaccardSimilarity * kendallSimilarity * displacementSimilarity;
    }

    /**
     * compute each similarity metric separately and multiply them together to get the final similarity score.
     * @param list1
     * @param list2
     * @return
     */
    public static double compoundOrderedSetSimilarity(List<Integer> list1, List<Integer> list2) {
        double jaccardSimilarity = jaccardSimilarity(list1, list2);
        double kendallSimilarity = kendallSimilarity(list1, list2);
        double displacementSimilarity = displacementSimilarity(list1, list2);
        return jaccardSimilarity * kendallSimilarity * displacementSimilarity;
    }

    /**
     * calculate the Jaccard similarity between 2 lists. This compares intersection size to union size.
     * @param list1
     * @param list2
     * @return
     */
    public static double jaccardSimilarity(List<Integer> list1, List<Integer> list2) {
        if (list1.isEmpty() && list2.isEmpty()) {
            // empty set always equals itself
            return 1.0;
        }

        Set<Integer> set1 = new HashSet<>(list1);
        Set<Integer> set2 = new HashSet<>(list2);

        Set<Integer> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<Integer> union = new HashSet<>(set1);
        union.addAll(set2);

        return (double) intersection.size() / union.size();
    }

    /**
     * calculate the similarity between 2 lists by comparing whether the order of common pairs is the same.
     * Any pair containing an element not in the other list will be ignored.
     * If either list contains less than 2 elements, the similarity will be 1.0, because there is no pair to compare.
     * @param list1
     * @param list2
     * @return
     */
    public static double kendallSimilarity(List<Integer> list1, List<Integer> list2) {
        if (list1.size() < 2 || list2.size() < 2) {
            // if there are no pairs, the lists are incomparable
            return 1.0;
        }

        Set<Integer> set1 = new HashSet<>(list1);
        Set<Integer> set2 = new HashSet<>(list2);

        Set<Integer> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        int intersectionSize = intersection.size();

        if (intersectionSize < 2) {
            // if there are no common pairs then the lists are incomparable
            return 1.0;
        }

        int concordant = 0;
        int discordant = 0;

        // Create a map to store the index of each element in list2
        Map<Integer, Integer> indexMap = new HashMap<>();
        for (int i = 0; i < list2.size(); i++) {
            indexMap.put(list2.get(i), i);
        }

        for (int i = 0; i < list1.size() - 1; i++) {
            Integer indexOfI = indexMap.get(list1.get(i));
            if (indexOfI == null) {
                continue;
            }
            for (int j = i + 1; j < list1.size(); j++) {
                Integer indexOfJ = indexMap.get(list1.get(j));
                if (indexOfJ == null) {
                    continue;
                }

                if (indexOfI < indexOfJ) {
                    concordant++;
                } else {
                    discordant++;
                }
            }
        }

        return 0.5 * (1 + (concordant - discordant) / (0.5 * intersectionSize * (intersectionSize - 1)));
    }

    /**
     * calculate the similarity between 2 lists by comparing the difference between the index of common elements.
     * If there is no common element, the similarity will be 1.0 because there is no displacement to penalize.
     * @param list1
     * @param list2
     * @return
     */
    public static double displacementSimilarity(List<Integer> list1, List<Integer> list2) {
        int maxListSize = Math.max(list1.size(), list2.size());
        if (maxListSize <= 1) {
            // either the lists have no common element or are equal
            return 1.0;
        }

        Map<Integer, Integer> indexMap1 = new HashMap<>();
        Map<Integer, Integer> indexMap2 = new HashMap<>();

        for (int i = 0; i < list1.size(); i++) {
            indexMap1.put(list1.get(i), i);
        }

        for (int i = 0; i < list2.size(); i++) {
            indexMap2.put(list2.get(i), i);
        }

        double totalPenalty = 0.0;
        int commonElements = 0;

        for (Integer element : indexMap1.keySet()) {
            if (indexMap2.containsKey(element)) {
                int index1 = indexMap1.get(element);
                int index2 = indexMap2.get(element);
                totalPenalty += Math.abs(index1 - index2);
                commonElements++;
            }
        }

        if (commonElements == 0) {
            return 1.0;
        }

        double maxPenalty = maxListSize * commonElements;
        return 1.0 - (totalPenalty / maxPenalty);
    }
}
