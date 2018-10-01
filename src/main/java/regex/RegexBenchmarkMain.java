package regex;


import java.util.Arrays;

public class RegexBenchmarkMain {
    public static void main(String[] args) {
        String[] data = makeStringData(1000000);

        Benchmark1 benchmark1 = new Benchmark1(data, "ab.*def");
        runBenchmark("benchmark1", benchmark1);
    }

    private static void runBenchmark(String name, RegexBenchmark benchmark) {
        System.out.println("=== Running benchmark: "  + name + " ===");
        int javaCount = benchmark.javaBenchmark();
        int re2jCount = benchmark.re2jBenchmark();
        int automatonCount = benchmark.automatonBenchmark();

        if (javaCount != re2jCount || javaCount != automatonCount) {
            throw new RuntimeException(javaCount + " != "  + re2jCount);
        }

        long time = System.currentTimeMillis();
        javaCount = benchmark.javaBenchmark();
        time = System.currentTimeMillis() - time;
        System.out.println("java: " + time + "ms");

        time = System.currentTimeMillis();
        re2jCount = benchmark.re2jBenchmark();
        time = System.currentTimeMillis() - time;
        System.out.println("re2j: " + time + "ms");

        time = System.currentTimeMillis();
        automatonCount = benchmark.automatonBenchmark();
        time = System.currentTimeMillis() - time;
        System.out.println("automaton: " + time + "ms");

        if (javaCount != re2jCount || javaCount != automatonCount) {
            throw new RuntimeException(javaCount + " != "  + re2jCount);
        }

        System.out.println("count: " + javaCount);

    }

    private static String[] makeStringData(int num) {
        String[] strings = new String[num];
        StringBuilder sb = new StringBuilder();
        sb.append('a');
        strings[0] = sb.toString();
        for (int i = 1; i < num; i++) {
            boolean carry = true;
            int j = sb.length() - 1;
            while (carry) {
                if (j < 0) {
                    sb.insert(0, 'a');
                    break;
                }
                char lastChar = sb.charAt(j);
                if (lastChar == 'z') {
                    lastChar = 'a';
                } else {
                    lastChar = (char)(lastChar + 1);
                    carry = false;
                }
                sb.setCharAt(j, lastChar);
                j--;
            }
            strings[i] = sb.toString();
        }
        return strings;
    }

    private static class Benchmark1 implements RegexBenchmark {
        private java.util.regex.Pattern javaRegex;
        private com.google.re2j.Pattern re2jRegex;
        private dk.brics.automaton.Automaton automatonRegex;
        private String[] data;

        public Benchmark1(String[] data, String regex) {
            this.data = data;
            javaRegex = java.util.regex.Pattern.compile(regex);
            re2jRegex = com.google.re2j.Pattern.compile(regex);
            automatonRegex = new dk.brics.automaton.RegExp(regex).toAutomaton();
            System.out.println(automatonRegex.toString());
        }

        @Override
        public int javaBenchmark() {
            int count = 0;
            for (String row : data) {
                if (javaRegex.matcher(row).matches()) {
                    count++;
                }
            }

            return count;
        }

        @Override
        public int re2jBenchmark() {
            int count = 0;
            for (String row : data) {
                if (re2jRegex.matcher(row).matches()) {
                    count++;
                }
            }

            return count;
        }

        @Override
        public int automatonBenchmark() {
            int count = 0;
            for (String row : data) {
                if (automatonRegex.run(row)) {
                    count++;
                }
            }

            return count;
        }
    }
}
