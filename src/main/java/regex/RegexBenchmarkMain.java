package regex;


import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;

import java.util.Arrays;

public class RegexBenchmarkMain {
    public static void main(String[] args) {
        String[] data = makeStringData(1000000);

        Benchmark1 benchmark1 = new Benchmark1(data, "ab.*ef");
        runBenchmark("benchmark1", benchmark1);
    }

    private static void runBenchmark(String name, RegexBenchmark benchmark) {
        System.out.println("=== Running benchmark: "  + name + " ===");
        int javaCount = benchmark.javaBenchmark();
        int re2jCount = benchmark.re2jBenchmark();
        int automatonCount = benchmark.automatonBenchmark();
        int handCodeCount = benchmark.handCodeBenchmark();
        handCodeCount = benchmark.handCodeBenchmark();
        handCodeCount = benchmark.handCodeBenchmark();

        if (javaCount != re2jCount || javaCount != automatonCount || javaCount != handCodeCount) {
            throw new RuntimeException(javaCount + " != "  + re2jCount + " != " + handCodeCount);
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

        time = System.currentTimeMillis();
        handCodeCount = benchmark.handCodeBenchmark();
        time = System.currentTimeMillis() - time;
        System.out.println("hand code: " + time + "ms");

        if (javaCount != re2jCount || javaCount != automatonCount || javaCount != handCodeCount) {
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
        private RunAutomaton automatonRegex;
        private String[] data;

        public Benchmark1(String[] data, String regex) {
            this.data = data;
            javaRegex = java.util.regex.Pattern.compile(regex);
            re2jRegex = com.google.re2j.Pattern.compile(regex);
            automatonRegex = new RunAutomaton(new RegExp(regex).toAutomaton());
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

        private boolean handCodeRegex(String str) {
            int state = 0;
            for (int i = 0; i < str.length(); i++) {
                switch (state) {
                    case 0:
                        if (str.charAt(i) != 'a') {
                            return false;
                        }
                        i++;

                        if (i >= str.length()) return false;

                        if (str.charAt(i) != 'b') {
                            return false;
                        }
                        state = 1;
                        i++;
                        if (i >= str.length()) return false;
                    case 1:
                        if (str.charAt(i) == 'e') {
                            i++;
                            if (i >= str.length()) return false;
                            state = 2;
                        } else {
                            break;
                        }
                    case 2:
                        if (str.charAt(i) == 'f') {
                            state = 3;
                            break;
                        } else if (str.charAt(i) == 'e') {
                            break;
                        } else {
                            state = 1;
                            break;
                        }
                    case 3:
                        if (str.charAt(i) == 'e') {
                            state = 2;
                        } else {
                            state = 1;
                        }
                        break;
                }
            }
            return state == 3;
        }

        @Override
        public int handCodeBenchmark() {
            int count = 0;
            for (String row : data) {
                if (handCodeRegex(row)) {
                    count++;
                }
            }

            return count;
        }
    }
}
