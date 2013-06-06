/*
 * Copyright 2013 Peter Lawrey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.higherfrequencytrading.chronicle.examples;

import com.higherfrequencytrading.chronicle.Excerpt;
import com.higherfrequencytrading.chronicle.impl.IndexedChronicle;
import com.higherfrequencytrading.chronicle.tools.ChronicleTools;

import java.io.IOException;

/**
 * @author peter.lawrey
 */
public class ExampleSimpleWriteReadMain {
    public static void main(String... args) throws IOException {
        final long runs = 1000 * 1000000L;
        final int batchSize = 10;
        System.out.printf("Messages to write %,d in batches of %,d%n", runs, batchSize);
        long start = System.nanoTime();
        final String basePath = System.getProperty("java.io.tmpdir") + "/ExampleSimpleWriteReadMain";
        ChronicleTools.deleteOnExit(basePath);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    IndexedChronicle ic = new IndexedChronicle(basePath);
                    ic.useUnsafe(true); // for benchmarks
                    Excerpt excerpt = ic.createExcerpt();
                    for (long i = 1; i <= runs; i += batchSize) {
                        excerpt.startExcerpt(13 * batchSize);
                        for (int k = 0; k < batchSize; k++) {
                            excerpt.writeUnsignedByte('M'); // message type
                            excerpt.writeLong(i); // e.g. time stamp
                            excerpt.writeFloat(i);
                        }
                        excerpt.finish();
                    }
                    ic.close();
                } catch (IOException e) {
                    throw new AssertionError(e);
                }
            }
        }).start();

        IndexedChronicle ic = new IndexedChronicle(basePath);
        ic.useUnsafe(true); // for benchmarks
        Excerpt excerpt = ic.createExcerpt();
        int blocks = 1000000;
        for (long j = 0; j < runs; j += blocks) {
            for (long i = j + 1; i <= j + blocks; i += batchSize) {
                while (!excerpt.nextIndex()) {
                    // busy wait
                }
                for (int k = 0; k < batchSize; k++) {
                    char ch = (char) excerpt.readUnsignedByte();
                    long l = excerpt.readLong();
                    float d = excerpt.readFloat();
                    assert ch == 'M';
                    assert l == i;
                    assert d == (float) i;
                }
                excerpt.finish();
            }
            if (((j + blocks) % 100000000) == 0) {
                long time = System.nanoTime() - start;
                System.out.printf("... Took %.2f to write and read %,d entries%n", time / 1e9, j + blocks);
            }
        }
        ic.close();

        long time = System.nanoTime() - start;
        System.out.printf("Took %.2f to write and read %,d entries%n", time / 1e9, runs);
    }
}

/* i7 950 with  24 GB of memory OCZ SSD drive.
... Took 5.11 to write and read 100,000,000 entries
... Took 10.21 to write and read 200,000,000 entries
... Took 15.54 to write and read 300,000,000 entries
... Took 20.69 to write and read 400,000,000 entries
... Took 25.84 to write and read 500,000,000 entries
... Took 31.14 to write and read 600,000,000 entries
... Took 36.46 to write and read 700,000,000 entries
... Took 41.67 to write and read 800,000,000 entries
... Took 46.84 to write and read 900,000,000 entries
... Took 51.98 to write and read 1,000,000,000 entries
... Took 63.55 to write and read 1,100,000,000 entries
... Took 69.13 to write and read 1,200,000,000 entries
... Took 74.69 to write and read 1,300,000,000 entries
... Took 80.06 to write and read 1,400,000,000 entries
... Took 85.76 to write and read 1,500,000,000 entries
... Took 94.18 to write and read 1,600,000,000 entries
... Took 100.72 to write and read 1,700,000,000 entries
... Took 106.24 to write and read 1,800,000,000 entries
... Took 111.81 to write and read 1,900,000,000 entries
... Took 117.47 to write and read 2,000,000,000 entries
... Took 124.13 to write and read 2,100,000,000 entries
... Took 132.82 to write and read 2,200,000,000 entries
... Took 138.13 to write and read 2,300,000,000 entries
... Took 143.99 to write and read 2,400,000,000 entries
... Took 149.84 to write and read 2,500,000,000 entries
... Took 155.57 to write and read 2,600,000,000 entries
... Took 164.56 to write and read 2,700,000,000 entries
... Took 169.92 to write and read 2,800,000,000 entries
... Took 175.39 to write and read 2,900,000,000 entries
... Took 181.18 to write and read 3,000,000,000 entries
... Took 186.69 to write and read 3,100,000,000 entries
... Took 192.10 to write and read 3,200,000,000 entries
... Took 201.12 to write and read 3,300,000,000 entries
... Took 206.71 to write and read 3,400,000,000 entries
... Took 212.28 to write and read 3,500,000,000 entries
... Took 217.87 to write and read 3,600,000,000 entries
... Took 223.40 to write and read 3,700,000,000 entries
... Took 231.81 to write and read 3,800,000,000 entries
... Took 237.22 to write and read 3,900,000,000 entries
... Took 242.88 to write and read 4,000,000,000 entries
... Took 248.61 to write and read 4,100,000,000 entries
... Took 254.19 to write and read 4,200,000,000 entries
... Took 263.11 to write and read 4,300,000,000 entries
... Took 268.63 to write and read 4,400,000,000 entries
... Took 274.23 to write and read 4,500,000,000 entries
... Took 279.68 to write and read 4,600,000,000 entries
... Took 285.27 to write and read 4,700,000,000 entries
... Took 290.96 to write and read 4,800,000,000 entries
... Took 299.82 to write and read 4,900,000,000 entries
... Took 305.58 to write and read 5,000,000,000 entries
... Took 311.39 to write and read 5,100,000,000 entries
... Took 317.05 to write and read 5,200,000,000 entries
... Took 322.77 to write and read 5,300,000,000 entries
... Took 331.23 to write and read 5,400,000,000 entries
... Took 336.82 to write and read 5,500,000,000 entries
... Took 342.47 to write and read 5,600,000,000 entries
... Took 348.14 to write and read 5,700,000,000 entries
... Took 354.02 to write and read 5,800,000,000 entries
... Took 362.48 to write and read 5,900,000,000 entries
... Took 367.95 to write and read 6,000,000,000 entries
... Took 373.62 to write and read 6,100,000,000 entries
... Took 379.07 to write and read 6,200,000,000 entries
... Took 384.76 to write and read 6,300,000,000 entries
... Took 391.36 to write and read 6,400,000,000 entries
... Took 398.83 to write and read 6,500,000,000 entries
... Took 404.75 to write and read 6,600,000,000 entries
... Took 410.50 to write and read 6,700,000,000 entries
... Took 416.11 to write and read 6,800,000,000 entries
... Took 421.84 to write and read 6,900,000,000 entries
... Took 430.10 to write and read 7,000,000,000 entries
... Took 435.82 to write and read 7,100,000,000 entries
... Took 441.42 to write and read 7,200,000,000 entries
... Took 447.15 to write and read 7,300,000,000 entries
... Took 452.86 to write and read 7,400,000,000 entries
... Took 461.35 to write and read 7,500,000,000 entries
... Took 466.79 to write and read 7,600,000,000 entries
... Took 472.43 to write and read 7,700,000,000 entries
... Took 477.97 to write and read 7,800,000,000 entries
... Took 483.58 to write and read 7,900,000,000 entries
... Took 491.25 to write and read 8,000,000,000 entries
... Took 497.84 to write and read 8,100,000,000 entries
... Took 503.51 to write and read 8,200,000,000 entries
... Took 509.04 to write and read 8,300,000,000 entries
... Took 514.73 to write and read 8,400,000,000 entries
... Took 520.34 to write and read 8,500,000,000 entries
... Took 528.83 to write and read 8,600,000,000 entries
... Took 534.58 to write and read 8,700,000,000 entries
... Took 540.40 to write and read 8,800,000,000 entries
... Took 546.35 to write and read 8,900,000,000 entries
... Took 551.89 to write and read 9,000,000,000 entries
... Took 559.96 to write and read 9,100,000,000 entries
... Took 565.31 to write and read 9,200,000,000 entries
... Took 570.84 to write and read 9,300,000,000 entries
... Took 576.72 to write and read 9,400,000,000 entries
... Took 582.31 to write and read 9,500,000,000 entries
... Took 590.77 to write and read 9,600,000,000 entries
... Took 596.42 to write and read 9,700,000,000 entries
... Took 601.93 to write and read 9,800,000,000 entries
... Took 607.47 to write and read 9,900,000,000 entries
... Took 613.31 to write and read 10,000,000,000 entries
... Took 620.75 to write and read 10,100,000,000 entries
... Took 627.60 to write and read 10,200,000,000 entries
... Took 633.19 to write and read 10,300,000,000 entries
... Took 638.82 to write and read 10,400,000,000 entries
... Took 644.52 to write and read 10,500,000,000 entries
... Took 650.09 to write and read 10,600,000,000 entries
... Took 658.36 to write and read 10,700,000,000 entries
... Took 663.99 to write and read 10,800,000,000 entries
... Took 669.74 to write and read 10,900,000,000 entries
... Took 675.50 to write and read 11,000,000,000 entries
... Took 681.15 to write and read 11,100,000,000 entries
... Took 689.26 to write and read 11,200,000,000 entries
... Took 694.71 to write and read 11,300,000,000 entries
... Took 700.32 to write and read 11,400,000,000 entries
... Took 706.01 to write and read 11,500,000,000 entries
... Took 712.74 to write and read 11,600,000,000 entries
... Took 720.07 to write and read 11,700,000,000 entries
... Took 725.70 to write and read 11,800,000,000 entries
... Took 731.19 to write and read 11,900,000,000 entries
... Took 736.80 to write and read 12,000,000,000 entries
... Took 742.34 to write and read 12,100,000,000 entries
... Took 750.12 to write and read 12,200,000,000 entries
... Took 756.87 to write and read 12,300,000,000 entries
... Took 762.51 to write and read 12,400,000,000 entries
... Took 768.10 to write and read 12,500,000,000 entries
... Took 773.71 to write and read 12,600,000,000 entries
... Took 779.45 to write and read 12,700,000,000 entries
... Took 787.67 to write and read 12,800,000,000 entries
... Took 793.39 to write and read 12,900,000,000 entries
... Took 799.11 to write and read 13,000,000,000 entries
... Took 804.84 to write and read 13,100,000,000 entries
... Took 810.35 to write and read 13,200,000,000 entries
... Took 818.24 to write and read 13,300,000,000 entries
... Took 823.80 to write and read 13,400,000,000 entries
... Took 829.47 to write and read 13,500,000,000 entries
... Took 835.07 to write and read 13,600,000,000 entries
... Took 840.88 to write and read 13,700,000,000 entries
... Took 849.47 to write and read 13,800,000,000 entries
... Took 854.86 to write and read 13,900,000,000 entries
... Took 860.40 to write and read 14,000,000,000 entries
... Took 866.05 to write and read 14,100,000,000 entries
... Took 871.87 to write and read 14,200,000,000 entries
... Took 880.51 to write and read 14,300,000,000 entries
... Took 886.12 to write and read 14,400,000,000 entries
... Took 891.78 to write and read 14,500,000,000 entries
... Took 897.40 to write and read 14,600,000,000 entries
... Took 903.24 to write and read 14,700,000,000 entries
... Took 910.87 to write and read 14,800,000,000 entries
... Took 917.12 to write and read 14,900,000,000 entries
... Took 922.64 to write and read 15,000,000,000 entries
... Took 928.17 to write and read 15,100,000,000 entries
... Took 933.75 to write and read 15,200,000,000 entries
... Took 941.33 to write and read 15,300,000,000 entries
... Took 947.85 to write and read 15,400,000,000 entries
... Took 953.40 to write and read 15,500,000,000 entries
... Took 959.21 to write and read 15,600,000,000 entries
... Took 964.94 to write and read 15,700,000,000 entries
... Took 970.72 to write and read 15,800,000,000 entries
... Took 978.65 to write and read 15,900,000,000 entries
... Took 984.50 to write and read 16,000,000,000 entries
... Took 990.20 to write and read 16,100,000,000 entries
... Took 996.00 to write and read 16,200,000,000 entries
... Took 1001.81 to write and read 16,300,000,000 entries
... Took 1009.76 to write and read 16,400,000,000 entries
... Took 1015.37 to write and read 16,500,000,000 entries
... Took 1021.09 to write and read 16,600,000,000 entries
... Took 1027.08 to write and read 16,700,000,000 entries
... Took 1032.95 to write and read 16,800,000,000 entries
... Took 1040.88 to write and read 16,900,000,000 entries
... Took 1046.32 to write and read 17,000,000,000 entries
... Took 1052.05 to write and read 17,100,000,000 entries
... Took 1057.76 to write and read 17,200,000,000 entries
... Took 1063.40 to write and read 17,300,000,000 entries
... Took 1071.56 to write and read 17,400,000,000 entries
... Took 1077.14 to write and read 17,500,000,000 entries
... Took 1082.83 to write and read 17,600,000,000 entries
... Took 1088.47 to write and read 17,700,000,000 entries
... Took 1094.13 to write and read 17,800,000,000 entries
... Took 1102.38 to write and read 17,900,000,000 entries
... Took 1108.25 to write and read 18,000,000,000 entries
... Took 1113.97 to write and read 18,100,000,000 entries
... Took 1119.61 to write and read 18,200,000,000 entries
... Took 1126.43 to write and read 18,300,000,000 entries
... Took 1134.06 to write and read 18,400,000,000 entries
... Took 1140.27 to write and read 18,500,000,000 entries
... Took 1145.88 to write and read 18,600,000,000 entries
... Took 1151.58 to write and read 18,700,000,000 entries
... Took 1157.17 to write and read 18,800,000,000 entries
... Took 1164.67 to write and read 18,900,000,000 entries
... Took 1171.01 to write and read 19,000,000,000 entries
... Took 1176.81 to write and read 19,100,000,000 entries
... Took 1183.16 to write and read 19,200,000,000 entries
... Took 1189.48 to write and read 19,300,000,000 entries
... Took 1197.08 to write and read 19,400,000,000 entries
... Took 1203.93 to write and read 19,500,000,000 entries
... Took 1209.65 to write and read 19,600,000,000 entries
... Took 1215.39 to write and read 19,700,000,000 entries
... Took 1220.98 to write and read 19,800,000,000 entries
... Took 1228.14 to write and read 19,900,000,000 entries
... Took 1235.11 to write and read 20,000,000,000 entries
Took 1247.07 to write and read 20,000,000,000 entries

batchSize=10
... Took 1.48 to write and read 100,000,000 entries
... Took 3.47 to write and read 200,000,000 entries
... Took 5.22 to write and read 300,000,000 entries
... Took 7.85 to write and read 400,000,000 entries
... Took 9.81 to write and read 500,000,000 entries
... Took 12.14 to write and read 600,000,000 entries
... Took 14.15 to write and read 700,000,000 entries
... Took 16.39 to write and read 800,000,000 entries
... Took 18.52 to write and read 900,000,000 entries
... Took 20.29 to write and read 1,000,000,000 entries
... Took 22.52 to write and read 1,100,000,000 entries
... Took 24.66 to write and read 1,200,000,000 entries
... Took 26.19 to write and read 1,300,000,000 entries
... Took 27.89 to write and read 1,400,000,000 entries
... Took 29.68 to write and read 1,500,000,000 entries
... Took 31.55 to write and read 1,600,000,000 entries
... Took 40.31 to write and read 1,700,000,000 entries
... Took 42.34 to write and read 1,800,000,000 entries
... Took 44.47 to write and read 1,900,000,000 entries
... Took 46.65 to write and read 2,000,000,000 entries
... Took 48.80 to write and read 2,100,000,000 entries
... Took 51.05 to write and read 2,200,000,000 entries
... Took 53.15 to write and read 2,300,000,000 entries
... Took 55.44 to write and read 2,400,000,000 entries
... Took 60.76 to write and read 2,500,000,000 entries
... Took 62.61 to write and read 2,600,000,000 entries
... Took 64.90 to write and read 2,700,000,000 entries
... Took 67.01 to write and read 2,800,000,000 entries
... Took 69.20 to write and read 2,900,000,000 entries
... Took 71.48 to write and read 3,000,000,000 entries
... Took 73.55 to write and read 3,100,000,000 entries
... Took 75.86 to write and read 3,200,000,000 entries
... Took 81.97 to write and read 3,300,000,000 entries
... Took 83.75 to write and read 3,400,000,000 entries
... Took 86.02 to write and read 3,500,000,000 entries
... Took 88.19 to write and read 3,600,000,000 entries
... Took 90.43 to write and read 3,700,000,000 entries
... Took 92.68 to write and read 3,800,000,000 entries
... Took 94.94 to write and read 3,900,000,000 entries
... Took 97.27 to write and read 4,000,000,000 entries
... Took 102.70 to write and read 4,100,000,000 entries
... Took 104.75 to write and read 4,200,000,000 entries
... Took 107.00 to write and read 4,300,000,000 entries
... Took 109.26 to write and read 4,400,000,000 entries
... Took 111.47 to write and read 4,500,000,000 entries
... Took 113.67 to write and read 4,600,000,000 entries
... Took 115.79 to write and read 4,700,000,000 entries
... Took 118.06 to write and read 4,800,000,000 entries
... Took 121.48 to write and read 4,900,000,000 entries
... Took 125.64 to write and read 5,000,000,000 entries
... Took 127.85 to write and read 5,100,000,000 entries
... Took 130.01 to write and read 5,200,000,000 entries
... Took 132.20 to write and read 5,300,000,000 entries
... Took 134.46 to write and read 5,400,000,000 entries
... Took 136.95 to write and read 5,500,000,000 entries
... Took 139.28 to write and read 5,600,000,000 entries
... Took 141.44 to write and read 5,700,000,000 entries
... Took 146.98 to write and read 5,800,000,000 entries
... Took 149.00 to write and read 5,900,000,000 entries
... Took 151.29 to write and read 6,000,000,000 entries
... Took 153.45 to write and read 6,100,000,000 entries
... Took 155.78 to write and read 6,200,000,000 entries
... Took 157.90 to write and read 6,300,000,000 entries
... Took 160.19 to write and read 6,400,000,000 entries
... Took 162.36 to write and read 6,500,000,000 entries
... Took 167.97 to write and read 6,600,000,000 entries
... Took 170.02 to write and read 6,700,000,000 entries
... Took 172.25 to write and read 6,800,000,000 entries
... Took 174.41 to write and read 6,900,000,000 entries
... Took 176.64 to write and read 7,000,000,000 entries
... Took 178.94 to write and read 7,100,000,000 entries
... Took 181.21 to write and read 7,200,000,000 entries
... Took 183.34 to write and read 7,300,000,000 entries
... Took 189.11 to write and read 7,400,000,000 entries
... Took 191.17 to write and read 7,500,000,000 entries
... Took 193.18 to write and read 7,600,000,000 entries
... Took 195.60 to write and read 7,700,000,000 entries
... Took 197.80 to write and read 7,800,000,000 entries
... Took 200.07 to write and read 7,900,000,000 entries
... Took 202.29 to write and read 8,000,000,000 entries
... Took 204.50 to write and read 8,100,000,000 entries
... Took 210.27 to write and read 8,200,000,000 entries
... Took 212.10 to write and read 8,300,000,000 entries
... Took 214.28 to write and read 8,400,000,000 entries
... Took 216.64 to write and read 8,500,000,000 entries
... Took 218.78 to write and read 8,600,000,000 entries
... Took 221.04 to write and read 8,700,000,000 entries
... Took 223.17 to write and read 8,800,000,000 entries
... Took 225.26 to write and read 8,900,000,000 entries
... Took 231.22 to write and read 9,000,000,000 entries
... Took 233.21 to write and read 9,100,000,000 entries
... Took 235.35 to write and read 9,200,000,000 entries
... Took 237.71 to write and read 9,300,000,000 entries
... Took 239.96 to write and read 9,400,000,000 entries
... Took 242.16 to write and read 9,500,000,000 entries
... Took 244.36 to write and read 9,600,000,000 entries
... Took 246.65 to write and read 9,700,000,000 entries
... Took 252.19 to write and read 9,800,000,000 entries
... Took 254.25 to write and read 9,900,000,000 entries
... Took 256.60 to write and read 10,000,000,000 entries
... Took 258.87 to write and read 10,100,000,000 entries
... Took 261.18 to write and read 10,200,000,000 entries
... Took 263.39 to write and read 10,300,000,000 entries
... Took 265.58 to write and read 10,400,000,000 entries
... Took 267.90 to write and read 10,500,000,000 entries
... Took 273.56 to write and read 10,600,000,000 entries
... Took 275.71 to write and read 10,700,000,000 entries
... Took 277.81 to write and read 10,800,000,000 entries
... Took 280.15 to write and read 10,900,000,000 entries
... Took 282.36 to write and read 11,000,000,000 entries
... Took 284.60 to write and read 11,100,000,000 entries
... Took 286.80 to write and read 11,200,000,000 entries
... Took 288.94 to write and read 11,300,000,000 entries
... Took 294.63 to write and read 11,400,000,000 entries
... Took 297.04 to write and read 11,500,000,000 entries
... Took 299.30 to write and read 11,600,000,000 entries
... Took 301.50 to write and read 11,700,000,000 entries
... Took 303.75 to write and read 11,800,000,000 entries
... Took 306.03 to write and read 11,900,000,000 entries
... Took 308.25 to write and read 12,000,000,000 entries
... Took 310.40 to write and read 12,100,000,000 entries
... Took 315.62 to write and read 12,200,000,000 entries
... Took 318.12 to write and read 12,300,000,000 entries
... Took 320.36 to write and read 12,400,000,000 entries
... Took 322.57 to write and read 12,500,000,000 entries
... Took 324.87 to write and read 12,600,000,000 entries
... Took 327.11 to write and read 12,700,000,000 entries
... Took 329.32 to write and read 12,800,000,000 entries
... Took 331.53 to write and read 12,900,000,000 entries
... Took 336.56 to write and read 13,000,000,000 entries
... Took 339.12 to write and read 13,100,000,000 entries
... Took 341.44 to write and read 13,200,000,000 entries
... Took 343.63 to write and read 13,300,000,000 entries
... Took 345.89 to write and read 13,400,000,000 entries
... Took 348.05 to write and read 13,500,000,000 entries
... Took 350.41 to write and read 13,600,000,000 entries
... Took 352.79 to write and read 13,700,000,000 entries
... Took 357.53 to write and read 13,800,000,000 entries
... Took 360.26 to write and read 13,900,000,000 entries
... Took 362.39 to write and read 14,000,000,000 entries
... Took 364.58 to write and read 14,100,000,000 entries
... Took 366.91 to write and read 14,200,000,000 entries
... Took 368.97 to write and read 14,300,000,000 entries
... Took 371.33 to write and read 14,400,000,000 entries
... Took 373.50 to write and read 14,500,000,000 entries
... Took 378.20 to write and read 14,600,000,000 entries
... Took 381.01 to write and read 14,700,000,000 entries
... Took 383.23 to write and read 14,800,000,000 entries
... Took 385.43 to write and read 14,900,000,000 entries
... Took 387.73 to write and read 15,000,000,000 entries
... Took 389.90 to write and read 15,100,000,000 entries
... Took 392.28 to write and read 15,200,000,000 entries
... Took 394.56 to write and read 15,300,000,000 entries
... Took 399.47 to write and read 15,400,000,000 entries
... Took 401.99 to write and read 15,500,000,000 entries
... Took 404.23 to write and read 15,600,000,000 entries
... Took 406.49 to write and read 15,700,000,000 entries
... Took 408.85 to write and read 15,800,000,000 entries
... Took 410.97 to write and read 15,900,000,000 entries
... Took 413.25 to write and read 16,000,000,000 entries
... Took 415.41 to write and read 16,100,000,000 entries
... Took 420.64 to write and read 16,200,000,000 entries
... Took 423.19 to write and read 16,300,000,000 entries
... Took 425.52 to write and read 16,400,000,000 entries
... Took 427.71 to write and read 16,500,000,000 entries
... Took 430.04 to write and read 16,600,000,000 entries
... Took 432.21 to write and read 16,700,000,000 entries
... Took 434.53 to write and read 16,800,000,000 entries
... Took 436.74 to write and read 16,900,000,000 entries
... Took 441.75 to write and read 17,000,000,000 entries
... Took 444.73 to write and read 17,100,000,000 entries
... Took 446.80 to write and read 17,200,000,000 entries
... Took 449.03 to write and read 17,300,000,000 entries
... Took 451.40 to write and read 17,400,000,000 entries
... Took 453.55 to write and read 17,500,000,000 entries
... Took 455.81 to write and read 17,600,000,000 entries
... Took 458.02 to write and read 17,700,000,000 entries
... Took 463.34 to write and read 17,800,000,000 entries
... Took 466.19 to write and read 17,900,000,000 entries
... Took 468.43 to write and read 18,000,000,000 entries
... Took 470.58 to write and read 18,100,000,000 entries
... Took 472.76 to write and read 18,200,000,000 entries
... Took 475.12 to write and read 18,300,000,000 entries
... Took 477.42 to write and read 18,400,000,000 entries
... Took 479.68 to write and read 18,500,000,000 entries
... Took 484.76 to write and read 18,600,000,000 entries
... Took 487.17 to write and read 18,700,000,000 entries
... Took 489.41 to write and read 18,800,000,000 entries
... Took 491.74 to write and read 18,900,000,000 entries
... Took 493.85 to write and read 19,000,000,000 entries
... Took 496.16 to write and read 19,100,000,000 entries
... Took 498.49 to write and read 19,200,000,000 entries
... Took 500.84 to write and read 19,300,000,000 entries
... Took 506.05 to write and read 19,400,000,000 entries
... Took 508.80 to write and read 19,500,000,000 entries
... Took 510.96 to write and read 19,600,000,000 entries
... Took 513.25 to write and read 19,700,000,000 entries
... Took 515.60 to write and read 19,800,000,000 entries
... Took 517.88 to write and read 19,900,000,000 entries
... Took 520.11 to write and read 20,000,000,000 entries
Took 525.09 to write and read 20,000,000,000 entries
 */
