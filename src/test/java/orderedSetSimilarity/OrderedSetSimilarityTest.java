package orderedSetSimilarity;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static orderedSetSimilarity.OrderedSetSimilarity.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderedSetSimilarityTest {

    private static List<Integer> generateRandomList(Random random) {
        int size = random.nextInt(50); // Random size between 0 and 49
        Set<Integer> list = new HashSet<>(size);
        for (int i = 0; i < size; i++) {
            list.add(random.nextInt(100)); // Random integers between 0 and 99
        }
        return list.stream().toList();
    }

    @Test
    public void compareVariousLists_orderedSetSimilarity() {
        assertEquals(1.0, orderedSetSimilarity(Arrays.asList(1, 2, 3, 4, 5), Arrays.asList(1, 2, 3, 4, 5)));
        assertEquals(1.0, orderedSetSimilarity(List.of(1), List.of(1)));
        assertEquals(1.0, orderedSetSimilarity(Collections.emptyList(), Collections.emptyList()));
        assertEquals(0.92, orderedSetSimilarity(Arrays.asList(1, 3, 2, 4, 5), Arrays.asList(1, 2, 3, 4, 5)));
        assertEquals(0.8055555555555556, orderedSetSimilarity(Arrays.asList(4, 7, 3, 9, 2), Arrays.asList(4, 7, 3, 9, 5, 2)));
        assertEquals(0.7222222222222223, orderedSetSimilarity(Arrays.asList(4, 7, 3, 8, 9, 2), Arrays.asList(4, 3, 7, 2, 9)));
        assertEquals(0.7142857142857143, orderedSetSimilarity(Arrays.asList(4, 7, 3, 8, 9, 2), Arrays.asList(4, 7, 3, 5, 9, 2)));
        assertEquals(0.6, orderedSetSimilarity(Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 3, 4, 5)));
        assertEquals(0.52, orderedSetSimilarity(Arrays.asList(5, 4, 3, 2, 1), Arrays.asList(1, 2, 3, 4, 5)));
        assertEquals(0.5, orderedSetSimilarity(Arrays.asList(1, 2), Arrays.asList(2, 1)));
        assertEquals(0.4444444444444444, orderedSetSimilarity(Arrays.asList(4, 7, 3, 8, 9, 2), Arrays.asList(4, 3, 7)));
        assertEquals(0.4, orderedSetSimilarity(Arrays.asList(1, 2), Arrays.asList(1, 2, 3, 4, 5)));
        assertEquals(0.2777777777777778, orderedSetSimilarity(Arrays.asList(4, 7, 3, 8, 9, 2), Arrays.asList(3, 9, 2)));
        assertEquals(0.25, orderedSetSimilarity(Arrays.asList(4, 7, 3, 8, 9, 2), Arrays.asList(5, 7, 1, 8)));
        assertEquals(0.007142857142857141, orderedSetSimilarity(Arrays.asList(1, 2, 3, 4, 5), Arrays.asList(14, 13, 12, 11, 10, 9, 8, 7, 6, 1)));
        assertEquals(0.0, orderedSetSimilarity(List.of(5), List.of(1)));
        assertEquals(0.0, orderedSetSimilarity(Collections.emptyList(), List.of(1)));
        assertEquals(0.0, orderedSetSimilarity(Arrays.asList(4, 7), Collections.emptyList()));
    }

    @Test
    public void performanceTest() {
        Random random = new Random();
        int numComparisons = 1500000;

        LocalDateTime start = LocalDateTime.now();
        List<Integer> list1 = generateRandomList(random);

        List<Map.Entry<List<Integer>, Double>> listSimilarities = new ArrayList<>(numComparisons);
        for (int i = 0; i < numComparisons; i++) {
            List<Integer> list2 = generateRandomList(random);
            listSimilarities.add(new AbstractMap.SimpleEntry<>(list2, orderedSetSimilarity(list1, list2)));
        }
        System.out.println("Comparing similarity took: " + (LocalDateTime.now().getSecond() - start.getSecond()) + " seconds");

        listSimilarities.sort(Comparator.<Map.Entry<List<Integer>, Double>>comparingDouble(Map.Entry::getValue).reversed());
        System.out.println(list1);
        listSimilarities.subList(0, 10).forEach(System.out::println);
        System.out.println("...");
        listSimilarities.subList(listSimilarities.size() - 10, listSimilarities.size()).forEach(System.out::println);
    }

    @Nested
    public class JaccardSimilarityTest {
        @Test
        public void equal() {
            List<Integer> list1 = Arrays.asList(1, 2, 3, 4, 5);
            List<Integer> list2 = Arrays.asList(1, 2, 3, 4, 5);
            assertEquals(1.0, jaccardSimilarity(list1, list2), 0.0);
            assertEquals(jaccardSimilarity(list1, list2), jaccardSimilarity(list2, list1));
        }

        @Test
        public void smallIntersection() {
            List<Integer> list1 = Arrays.asList(1, 2);
            List<Integer> list2 = Arrays.asList(1, 2, 3, 4, 5);
            assertEquals(0.4, jaccardSimilarity(list1, list2), 0.0);
            assertEquals(jaccardSimilarity(list1, list2), jaccardSimilarity(list2, list1));
        }

        @Test
        public void reversedOrder() {
            List<Integer> list1 = Arrays.asList(5, 4, 3, 2, 1);
            List<Integer> list2 = Arrays.asList(1, 2, 3, 4, 5);
            assertEquals(1.0, jaccardSimilarity(list1, list2), 0.0);
            assertEquals(jaccardSimilarity(list1, list2), jaccardSimilarity(list2, list1));
        }

        @Test
        public void smallList_unequal() {
            List<Integer> list1 = List.of(5);
            List<Integer> list2 = List.of(1);
            assertEquals(0.0, jaccardSimilarity(list1, list2), 0.0);
            assertEquals(jaccardSimilarity(list1, list2), jaccardSimilarity(list2, list1));
        }

        @Test
        public void smallList_equal() {
            List<Integer> list1 = List.of(1);
            List<Integer> list2 = List.of(1);
            assertEquals(1.0, jaccardSimilarity(list1, list2), 0.0);
            assertEquals(jaccardSimilarity(list1, list2), jaccardSimilarity(list2, list1));
        }

        @Test
        public void oneEmptyList() {
            List<Integer> list1 = Collections.emptyList();
            List<Integer> list2 = List.of(1);
            assertEquals(0.0, jaccardSimilarity(list1, list2), 0.0);
            assertEquals(jaccardSimilarity(list1, list2), jaccardSimilarity(list2, list1));
        }

        @Test
        public void bothEmptyList() {
            List<Integer> list1 = Collections.emptyList();
            List<Integer> list2 = Collections.emptyList();
            assertEquals(1.0, jaccardSimilarity(list1, list2), 0.0);
            assertEquals(jaccardSimilarity(list1, list2), jaccardSimilarity(list2, list1));
        }
    }

    @Nested
    public class KendallSimilarityTest {
        @Test
        public void equal() {
            List<Integer> list1 = Arrays.asList(1, 2, 3, 4, 5);
            List<Integer> list2 = Arrays.asList(1, 2, 3, 4, 5);
            assertEquals(1.0, kendallSimilarity(list1, list2), 0.0);
            assertEquals(kendallSimilarity(list1, list2), kendallSimilarity(list2, list1));
        }

        @Test
        public void differentElements_differentOrder() {
            List<Integer> list1 = Arrays.asList(1, 3, 2, 4, 5);
            List<Integer> list2 = Arrays.asList(1, 2, 3, 4, 5);
            assertEquals(0.9, kendallSimilarity(list1, list2), 0.0);
            assertEquals(kendallSimilarity(list1, list2), kendallSimilarity(list2, list1));
        }

        @Test
        public void reversedOrder() {
            List<Integer> list1 = Arrays.asList(5, 4, 3, 2, 1);
            List<Integer> list2 = Arrays.asList(1, 2, 3, 4, 5);
            assertEquals(0.0, kendallSimilarity(list1, list2), 0.0);
            assertEquals(kendallSimilarity(list1, list2), kendallSimilarity(list2, list1));
        }

        @Test
        public void differentSizes_sameOrder() {
            List<Integer> list1 = Arrays.asList(1, 2, 3);
            List<Integer> list2 = Arrays.asList(1, 2, 3, 4, 5);
            assertEquals(1.0, kendallSimilarity(list1, list2), 0.0);
            assertEquals(kendallSimilarity(list1, list2), kendallSimilarity(list2, list1));
        }

        @Test
        public void differentElements_differentSizes_differentOrder() {
            List<Integer> list1 = Arrays.asList(4, 7, 3, 8, 9, 2);
            List<Integer> list2 = Arrays.asList(4, 3, 7);
            assertEquals(0.6666666666666666, kendallSimilarity(list1, list2), 0.0);
            assertEquals(kendallSimilarity(list1, list2), kendallSimilarity(list2, list1));
        }

        @Test
        public void differentElements_differentSizes_sameOrder() {
            List<Integer> list1 = Arrays.asList(4, 7, 3, 8, 9, 2);
            List<Integer> list2 = Arrays.asList(3, 9, 2);
            assertEquals(1.0, kendallSimilarity(list1, list2), 0.0);
            assertEquals(kendallSimilarity(list1, list2), kendallSimilarity(list2, list1));
        }

        @Test
        public void differentSizes_differentOrder() {
            List<Integer> list1 = Arrays.asList(4, 7, 3, 8, 9, 2);
            List<Integer> list2 = Arrays.asList(4, 3, 7, 2, 9);
            assertEquals(0.8, kendallSimilarity(list1, list2), 0.0);
            assertEquals(kendallSimilarity(list1, list2), kendallSimilarity(list2, list1));
        }

        @Test
        public void differentElements_sameOrder() {
            List<Integer> list1 = Arrays.asList(4, 7, 3, 8, 9, 2);
            List<Integer> list2 = Arrays.asList(4, 7, 3, 5, 9, 2);
            assertEquals(1.0, kendallSimilarity(list1, list2), 0.0);
            assertEquals(kendallSimilarity(list1, list2), kendallSimilarity(list2, list1));
        }

        @Test
        public void smallList_unequal() {
            List<Integer> list1 = List.of(5);
            List<Integer> list2 = List.of(1);
            assertEquals(1.0, kendallSimilarity(list1, list2), 0.0);
            assertEquals(kendallSimilarity(list1, list2), kendallSimilarity(list2, list1));
        }

        @Test
        public void smallList_equal() {
            List<Integer> list1 = List.of(1);
            List<Integer> list2 = List.of(1);
            assertEquals(1.0, kendallSimilarity(list1, list2), 0.0);
            assertEquals(kendallSimilarity(list1, list2), kendallSimilarity(list2, list1));
        }

        @Test
        public void oneEmptyList() {
            List<Integer> list1 = Collections.emptyList();
            List<Integer> list2 = List.of(1);
            assertEquals(1.0, kendallSimilarity(list1, list2), 0.0);
            assertEquals(kendallSimilarity(list1, list2), kendallSimilarity(list2, list1));
        }

        @Test
        public void bothEmptyList() {
            List<Integer> list1 = Collections.emptyList();
            List<Integer> list2 = Collections.emptyList();
            assertEquals(1.0, kendallSimilarity(list1, list2), 0.0);
            assertEquals(kendallSimilarity(list1, list2), kendallSimilarity(list2, list1));
        }

        @Test
        public void singlePair() {
            List<Integer> list1 = Arrays.asList(4, 7);
            List<Integer> list2 = Collections.emptyList();
            assertEquals(1.0, kendallSimilarity(list1, list2), 0.0);
            assertEquals(kendallSimilarity(list1, list2), kendallSimilarity(list2, list1));
        }
    }

    @Nested
    public class DisplacementSimilarityTest {
        @Test
        public void equal() {
            List<Integer> list1 = Arrays.asList(1, 2, 3, 4, 5);
            List<Integer> list2 = Arrays.asList(1, 2, 3, 4, 5);
            assertEquals(1.0, displacementSimilarity(list1, list2), 0.0);
            assertEquals(displacementSimilarity(list1, list2), displacementSimilarity(list2, list1));
        }

        @Test
        public void sameSize_unequal() {
            List<Integer> list1 = Arrays.asList(1, 3, 2, 4, 5);
            List<Integer> list2 = Arrays.asList(1, 2, 3, 4, 5);
            assertEquals(0.92, displacementSimilarity(list1, list2), 0.0);
            assertEquals(displacementSimilarity(list1, list2), displacementSimilarity(list2, list1));
        }

        @Test
        public void reversedOrder() {
            List<Integer> list1 = Arrays.asList(5, 4, 3, 2, 1);
            List<Integer> list2 = Arrays.asList(1, 2, 3, 4, 5);
            assertEquals(0.52, displacementSimilarity(list1, list2), 0.0);
            assertEquals(displacementSimilarity(list1, list2), displacementSimilarity(list2, list1));
        }

        @Test
        public void differentSizes_equal() {
            List<Integer> list1 = Arrays.asList(1, 2, 3);
            List<Integer> list2 = Arrays.asList(1, 2, 3, 4, 5);
            assertEquals(1.0, displacementSimilarity(list1, list2), 0.0);
            assertEquals(displacementSimilarity(list1, list2), displacementSimilarity(list2, list1));
        }

        @Test
        public void differentSizes_differentElements_highlyUnequal() {
            List<Integer> list1 = Arrays.asList(4, 7, 3, 8, 9, 2);
            List<Integer> list2 = Arrays.asList(3, 9, 2);
            assertEquals(0.5555555555555556, displacementSimilarity(list1, list2), 0.0);
            assertEquals(displacementSimilarity(list1, list2), displacementSimilarity(list2, list1));
        }

        @Test
        public void differentSizes_differentElements_unequal() {
            List<Integer> list1 = Arrays.asList(4, 7, 3, 8, 9, 2);
            List<Integer> list2 = Arrays.asList(4, 3, 7, 2, 9);
            assertEquals(0.8666666666666667, displacementSimilarity(list1, list2), 0.0);
            assertEquals(displacementSimilarity(list1, list2), displacementSimilarity(list2, list1));
        }

        @Test
        public void differentSizes_differentElements_equal() {
            List<Integer> list1 = Arrays.asList(4, 7, 3, 8, 9, 2);
            List<Integer> list2 = Arrays.asList(5, 7, 1, 8);
            assertEquals(1.0, displacementSimilarity(list1, list2), 0.0);
            assertEquals(displacementSimilarity(list1, list2), displacementSimilarity(list2, list1));
        }

        @Test
        public void differentElements_equal() {
            List<Integer> list1 = Arrays.asList(4, 7, 3, 8, 9, 2);
            List<Integer> list2 = Arrays.asList(4, 7, 3, 5, 9, 2);
            assertEquals(1.0, displacementSimilarity(list1, list2), 0.0);
            assertEquals(displacementSimilarity(list1, list2), displacementSimilarity(list2, list1));
        }

        @Test
        public void minimumDifference() {
            List<Integer> list1 = Arrays.asList(4, 7, 3, 9, 2);
            List<Integer> list2 = Arrays.asList(4, 7, 3, 9, 5, 2);
            assertEquals(0.9666666666666667, displacementSimilarity(list1, list2), 0.0);
            assertEquals(displacementSimilarity(list1, list2), displacementSimilarity(list2, list1));
        }

        @Test
        public void maximumDifference() {
            List<Integer> list1 = Arrays.asList(1, 2, 3, 4, 5);
            List<Integer> list2 = Arrays.asList(14, 13, 12, 11, 10, 9, 8, 7, 6, 1);
            assertEquals(0.09999999999999998, displacementSimilarity(list1, list2), 0.0);
            assertEquals(displacementSimilarity(list1, list2), displacementSimilarity(list2, list1));
        }


        @Test
        public void smallList_unequal() {
            List<Integer> list1 = List.of(5);
            List<Integer> list2 = List.of(1);
            assertEquals(1.0, displacementSimilarity(list1, list2), 0.0);
            assertEquals(displacementSimilarity(list1, list2), displacementSimilarity(list2, list1));
        }

        @Test
        public void smallList_equal() {
            List<Integer> list1 = List.of(1);
            List<Integer> list2 = List.of(1);
            assertEquals(1.0, displacementSimilarity(list1, list2), 0.0);
            assertEquals(displacementSimilarity(list1, list2), displacementSimilarity(list2, list1));
        }

        @Test
        public void oneEmptyList() {
            List<Integer> list1 = Collections.emptyList();
            List<Integer> list2 = List.of(1);
            assertEquals(1.0, displacementSimilarity(list1, list2), 0.0);
            assertEquals(displacementSimilarity(list1, list2), displacementSimilarity(list2, list1));
        }

        @Test
        public void bothEmptyList() {
            List<Integer> list1 = Collections.emptyList();
            List<Integer> list2 = Collections.emptyList();
            assertEquals(1.0, displacementSimilarity(list1, list2), 0.0);
            assertEquals(displacementSimilarity(list1, list2), displacementSimilarity(list2, list1));
        }
    }
}