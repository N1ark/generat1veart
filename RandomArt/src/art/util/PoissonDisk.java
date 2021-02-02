package art.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class PoissonDisk {
	@SuppressWarnings("hiding")
	public static class PVector {
		public int x;
		public int y;
		
		public PVector(double x, double y) {
			this.x = (int) x;
			this.y = (int) y;
		}
		
		public PVector(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public PVector add(PVector v) {
			this.x += v.x;
			this.y += v.y;
			return this;
		}
		public PVector add(int x, int y) {
			this.x += x;
			this.y += y;
			return this;
		}
		public double dist(double x2, double x1) {
			return Math.sqrt(Math.pow(this.x - x2, 2) + Math.pow(this.y - x1, 2));
		}
		public double dist(PVector v) {
			return dist(v.x, v.y);
		}
		
		public float dot(PVector v) {
			return this.x * v.x + this.y * v.y;
		}
	}

	public static ArrayList<PVector> poissonDiskSampling(long seed, int r, int size) {
		Random random = new Random(seed);
		ArrayList<PVector> samples = new ArrayList<>();
		ArrayList<PVector> active_ArrayList = new ArrayList<>();
		float pi = (float) Math.PI;
		active_ArrayList.add(new PVector(random.nextInt(size), random.nextInt(size)));

		int len;
		while ((len = active_ArrayList.size()) > 0) {
			// picks random index uniformly at random from the active ArrayList
			int index = random.nextInt(len);
			Collections.swap(active_ArrayList, len - 1, index);
			PVector sample = active_ArrayList.get(len - 1);
			boolean found = false;
			for (int i = 0; i < 30; ++i) {
				// generates a point uniformly at random in the sample's
				// disk situated at a distance from r to 2*r
				float angle = 2 * pi * random.nextFloat();
				float radius = random.nextInt(r) + r;
				PVector dv = new PVector(radius * Math.cos(angle), radius * Math.sin(angle));
				PVector new_sample = dv.add(sample);

				boolean ok = true;
				for (PVector j : samples) {
					if (new_sample.dist(j) <= r) {
						ok = false;
						break;
					}
				}

				if (ok) {
					if (0 <= new_sample.x && new_sample.x < size && 0 <= new_sample.y && new_sample.y < size) {
						samples.add(new_sample);
						active_ArrayList.add(new_sample);
						len++;
						found = true;
					}
				}
			}
			if (!found)
				active_ArrayList.remove(active_ArrayList.size() - 1);
		}
		return samples;
	}

}
