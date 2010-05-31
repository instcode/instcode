package me.instcode.common.map;

import java.io.IOException;
import java.util.Random;

public class MapTest {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		testHashing();
		System.gc();
		testTinyMap2();
		System.gc();
		testTinyMap();
		System.gc();
	}

	static void testHashing() {
		Random random = new Random(System.currentTimeMillis());
		int n = 1 << 26;
		int length = 1 << 25;
		int[] index = new int[length];
		long[] value = new long[length];
		int size = 0, count = 0, convince = 0, believe = 0;
		System.out.println("n/length " + n + "/" + length);
		for (int i = 0; i < n; i++) {
			long key = Math.abs(random.nextLong());
			int hash = TinyMap.hash(key) & (length - 1);
			if (index[hash] == 0) {
				index[hash] = size;
				value[size] = key;
				size++;
			}
			else {
				long oldkey = value[index[hash]];
				believe++;
				if ((TinyMap.hash(key) & (length - 1)) == (TinyMap.hash(oldkey) & (length - 1))) {
					convince++;
				}
				if ((TinyMap.hash64(key) & (length - 1)) == (TinyMap.hash64(oldkey) & (length - 1))) {
					count++;
				}
			}
		}
		System.out.println("size/count/convince " + size + "/" + count + "/" + convince + " => " + (believe == convince));
	}

	static void testTinyMap() {
		System.out.println("Tiny Map/get&put...");
		long save = System.currentTimeMillis();
		
		int n = 0x1FFFFFF;
		TinyMap map = new TinyMap(n + 1);
		Runtime runtime = Runtime.getRuntime();
		System.out.println("Size: " + n);
		long memUsage = (runtime.totalMemory() - runtime.freeMemory()) / 1024;
		System.out.println("Used: " + memUsage + "/" + (runtime.totalMemory() / 1024) + " KBs");
		System.out.println("Time: " + ((System.currentTimeMillis() - save)));
		
		Random random = new Random(System.currentTimeMillis());
		for (int i = 0; i < n * 2; i++) {
			long key = i % n;
			int index = map.get(key);
			if (index < 0) {
				map.put(key, (int)key);
			}
			//System.out.println("key: " + key + " value: " + value);
		}

		System.out.println("Total: " + map);
		System.out.println("Time: " + ((System.currentTimeMillis() - save)));
		
		for (int i = 0; i < n * 2; i++) {
			long key = Math.abs(random.nextLong()) % n;
			int value = map.get(key);
			if (value != key) {
				System.out.println("key: " + key + " value: " + value);
			}
		}
		System.out.println("Time: " + ((System.currentTimeMillis() - save)));
	}
	
	static void testTinyMap2() {
		System.out.println("Tiny Map/look&Update...");
		long save = System.currentTimeMillis();
		
		int n = 0x1FFFFFF;
		TinyMap map = new TinyMap(n + 1);
		Runtime runtime = Runtime.getRuntime();
		System.out.println("Size: " + n);
		long memUsage = (runtime.totalMemory() - runtime.freeMemory()) / 1024;
		System.out.println("Used: " + memUsage + "/" + (runtime.totalMemory() / 1024) + " KBs");
		System.out.println("Time: " + ((System.currentTimeMillis() - save)));
		
		Random random = new Random(System.currentTimeMillis());
		for (int i = 0; i < n * 2; i++) {
			long key = i % n;
			map.lookUpdate(key, (int)key);
			//System.out.println("key: " + key + " value: " + value);
		}

		System.out.println("Total: " + map);
		System.out.println("Time: " + ((System.currentTimeMillis() - save)));
		
		for (int i = 0; i < n * 2; i++) {
			long key = Math.abs(random.nextLong()) % n;
			int value = map.get(key);
			if (value != key) {
				System.out.println("key: " + key + " value: " + value);
			}
		}
		System.out.println("Time: " + ((System.currentTimeMillis() - save)));
	}
}
