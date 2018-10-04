package bnpreprocess;

import java.util.Arrays;

public class BNPreProcess {
    private static final int SIZE = 1000000;
//private static final int SIZE = 20;
    private static final int WIDTH = 20;
    private static final int MAX_VAL = 100;
    private static final int BLOCK_SIZE = 100;
    private static final boolean IS_COLUMN_STORE = true;

    public static void main(String[] args) {
        String[][] data = generateData();
        boolean[] blockPassed = new boolean[BLOCK_SIZE];
        long time;
        int result;

        result = tnProcess(data);
        result += tn2Process(data);
        result += bnProcess(data, blockPassed);
        System.out.println("test: " + result);

        time = System.currentTimeMillis();
        result = tnProcess(data);
        time = System.currentTimeMillis() - time;
        System.out.println("tn: " + time + " size: " + result);

        time = System.currentTimeMillis();
        result = tn2Process(data);
        time = System.currentTimeMillis() - time;
        System.out.println("tn2: " + time + " size: " + result);


        time = System.currentTimeMillis();
        result = bnProcess(data, blockPassed);
        time = System.currentTimeMillis() - time;
        System.out.println("bn: " + time + " size: " + result);

    }

    private static int tnProcess(String[][] data) {
        int count = 0;
        boolean passed;

        for (int i = 0; i < SIZE; i++) {
            passed = true;
            for (int j = 1; j < WIDTH; j++) {
                if (!get(data, i, j-1).equals(get(data, i, j))) {
                    passed = false;
                    break;
                }

            }

            if (passed) {
                count++;
            }
        }

        return count;
    }

    private static int tn2Process(String[][] data) {
        int count = 0;
        boolean passed;

        for (int i = 0; i < SIZE; i+=4) {
            passed = true;
            for (int j = 1; j < WIDTH; j++) {
                if (!get(data, i, j-1).equals(get(data, i, j))) {
                    passed = false;
                    break;
                }
            }

            for (int j = 1; j < WIDTH; j++) {
                if (!get(data, i+1, j-1).equals(get(data, i+1, j))) {
                    passed = false;
                    break;
                }
            }

            for (int j = 1; j < WIDTH; j++) {
                if (!get(data, i+2, j-1).equals(get(data, i+2, j))) {
                    passed = false;
                    break;
                }
            }

            for (int j = 1; j < WIDTH; j++) {
                if (!get(data, i+3, j-1).equals(get(data, i+3, j))) {
                    passed = false;
                    break;
                }
            }

            if (passed) {
                count++;
            }
        }

        return count;
    }

    private static int bnProcess(String[][] data, boolean[] blockPassed) {
        int count = 0;
        Arrays.fill(blockPassed, true);

        for (int i = 0; i < SIZE; i+= BLOCK_SIZE) {
            int thisBlock = Math.min(BLOCK_SIZE, SIZE - i);
//            System.out.println(thisBlock);
            for (int j = 1; j < WIDTH; j++) {
                for (int k = 0; k < thisBlock; k++) {
                    if (blockPassed[k]) {
                        blockPassed[k] =  get(data, i+k, j-1).equals(get(data, i+k, j));
                    }
                }
            }

            for (int k = 0; k < thisBlock; k++) {
                if (blockPassed[k]) {
                    count++;
                }
                blockPassed[k] = true;
            }
        }
        return count;
    }

    private static String[][] generateData() {
        String[][] data;

        if (IS_COLUMN_STORE) {
            data = new String[WIDTH][];

            for (int i = 0; i < WIDTH; i++) {
                data[i] = new String[SIZE];
            }
        } else {
            data = new String[SIZE][];
            for (int i = 0; i < SIZE; i++) {
                data[i] = new String[WIDTH];
            }
        }

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (j == 0) {
                    put(data, i, j, hash(hash(i), MAX_VAL));
                } else {
                    put(data, i, j, hash(hash(i)+hash(j), MAX_VAL));
                }
            }
        }

        return data;
    }

    private static int hash(int hashCode) {
        hashCode ^= Integer.rotateRight(hashCode, 20) ^ Integer.rotateRight(hashCode, 12);
        hashCode ^= Integer.rotateRight(hashCode, 7) ^ Integer.rotateRight(hashCode, 4);
        hashCode *= 0x5BD1E995;
        hashCode = Math.abs(hashCode);
        return hashCode;
    }

    private static String hash(int hashCode, int maxVal) {
        return "" + (hash(hashCode) % maxVal);
    }

    private static String get(String[][] data, int x, int y) {
        if (IS_COLUMN_STORE) {
            return data[y][x];
        } else {
            return data[x][y];
        }
    }

    private static void put(String[][] data, int x, int y, String val) {
        if (IS_COLUMN_STORE) {
            data[y][x] = val;
        } else {
            data[x][y] = val;
        }
    }


}
