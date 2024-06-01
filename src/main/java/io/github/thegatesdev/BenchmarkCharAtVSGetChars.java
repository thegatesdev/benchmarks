package io.github.thegatesdev;

import java.nio.charset.Charset;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

public class BenchmarkCharAtVSGetChars {

    @State(Scope.Benchmark)
    public static class Data {

        String value;
        char[] copyBuffer = new char[100];

        @Setup
        public void setup() {
            byte[] buf = new byte[50];
            new Random(1234).nextBytes(buf);
            value = new String(buf, Charset.forName("UTF-8"));
        }
    }

    @Benchmark
    @Fork(value = 1, warmups = 1)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measure_charAt_iteration(Data data, Blackhole blackhole) {
        String value = data.value;
        int len = value.length();

        for (int i = 0; i < len; i++) {
            blackhole.consume(value.charAt(i));
        }
    }

    @Benchmark
    @Fork(value = 1, warmups = 1)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void measure_bufferedGetChars_iteration(Data data, Blackhole blackhole) {
        String value = data.value;
        char[] buffer = data.copyBuffer;
        int max = buffer.length;
        int len = value.length();
        int index = 0;

        while (index < len) {
            int count = Math.min(len - index, max);
            value.getChars(index, index + count, buffer, 0);
            blackhole.consume(buffer);
            index += count;
        }
    }
}
