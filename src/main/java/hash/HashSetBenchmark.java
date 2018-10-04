package hash;

import com.koloboke.collect.set.hash.HashIntSet;
import com.koloboke.collect.set.hash.HashIntSets;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class HashSetBenchmark {
    private static class Benchmark {
        Set<Integer> set;
        int range;
        Random random;
        public Benchmark(Set<Integer> set, int range, int num_elements) {
            this.set = set;
            this.range = range;

            this.random = new Random(1);

            for (int i = 0; i < num_elements; i++) {
                set.add(random.nextInt(range));
            }
        }

        public int run(int iters) {
            int count = 0;
            for (int i = 0; i < iters; i++) {
                if (this.set.contains(random.nextInt(range))) count++;
            }

            return count;
        }
    }

    private static final int RANGE = 100000;
    private static final int NUM_ELEMENTS = 1000;
    private static final int ITERS = 10000000;

    public static void main(String[] args) {
        Benchmark hash = new Benchmark(new HashSet<>(), RANGE, NUM_ELEMENTS);
        Benchmark koloboke = new Benchmark(HashIntSets.newMutableSet(), RANGE, NUM_ELEMENTS);

        int hashCount = 0;
        int kolobokeCount = 0;

        hashCount += hash.run(ITERS);
        hashCount += hash.run(ITERS);
        hashCount += hash.run(ITERS);

        kolobokeCount += koloboke.run(ITERS);
        kolobokeCount += koloboke.run(ITERS);
        kolobokeCount += koloboke.run(ITERS);

        if (hashCount != kolobokeCount) throw new RuntimeException();

        long time;

        time = System.currentTimeMillis();
        hashCount = hash.run(ITERS);
        time = System.currentTimeMillis() - time;
        System.out.println("hash: " + time + "ms");

        time = System.currentTimeMillis();
        kolobokeCount= koloboke.run(ITERS);
        time = System.currentTimeMillis() - time;
        System.out.println("hash: " + time + "ms");

        if (hashCount != kolobokeCount) throw new RuntimeException();
    }
}
