package regex;

public interface RegexBenchmark {
    int javaBenchmark();
    int re2jBenchmark();
    int automatonBenchmark();
    int handCodeBenchmark();
}
