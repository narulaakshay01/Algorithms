import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class cpath {

	public static void main(String[] args) throws Exception {
		cpath path = new cpath();
		path.begin(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
	}

	@SuppressWarnings({ "resource", "unchecked" })
	public void begin(String fileName, Integer source, Integer dest, Integer budget) throws Exception {

		File file = new File(fileName);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String st;
		// skipping the first line
		br.readLine();

		// fetching the number of vertices
		int vert = Integer.parseInt(br.readLine().trim());
		ArrayList<Edge>[] vertices = new ArrayList[vert];
		Map<String, Edge> edges = new HashMap<String, Edge>();
		ArrayList<Edge>[] pathVertices = new ArrayList[vert];

		for (int i = 0; i < vert; i++) {
			vertices[i] = new ArrayList<Edge>();
			pathVertices[i] = new ArrayList<Edge>();
		}

		// gettimg edges
		while ((st = br.readLine()) != null) {
			String[] temp = st.trim().split(" ");
			Edge tempEdge = new Edge(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2]),
					Integer.parseInt(temp[3]));
			edges.put(temp[0] + "-" + temp[1], tempEdge);
			vertices[tempEdge.getFrom()].add(tempEdge);
		}

		// initialize queue
		PriorityQueue<Edge> q = new PriorityQueue<Edge>();
		q.add(new Edge(source, source, 0, 0));

		while (!q.isEmpty()) {
			Edge e = q.poll();
			int to = e.getTo(), c = e.getCost(), t = e.getTime();

			// adding to path
			if (pathVertices[e.getTo()].isEmpty()) {
				pathVertices[e.getTo()].add(e);
			} else {
				for (int i = 0; i < pathVertices[to].size(); i++) {
					if (toIgnoreEdge(c, t, pathVertices[to].get(i))) {
						continue;
					} else {
						pathVertices[to].add(e);
						break;
					}
				}
			}

			// adding to priority queue
			for (int j = 0; j < vertices[to].size(); j++) {
				Edge eg = new Edge(vertices[to].get(j));
				eg.setCost(eg.getCost() + c);
				eg.setTime(eg.getTime() + t);
				if (eg.getCost() > budget)
					break;
				if (pathVertices[eg.getTo()].isEmpty()) {
					q.add(eg);
				} else {
					for (int i = 0; i < pathVertices[eg.getTo()].size(); i++) {
						if (toIgnoreEdge(eg.getCost(), eg.getTime(), pathVertices[eg.getTo()].get(i))) {
							continue;
						} else {
							q.add(eg);
							break;
						}
					}
				}
			}
		}

		int bestTime = Integer.MAX_VALUE;
		Edge bestEdge = null;

		ArrayList<Edge> target = pathVertices[dest];
		Collections.sort(target, new CustomComparator());

		// finding best time in budgest and printing other possibilities
		System.out.println("All paths in the given budget of $" + budget);
		for (Edge e : target) {
			if (e.getTime() < bestTime) {
				bestEdge = new Edge(e);
				bestTime = e.getTime();
			}
			System.out.println(source + " to " + dest + " in " + e.getTime() + " unit time costs $" + e.getCost());
		}

		// find pathe for the best cost
		if (bestEdge == null) {
			System.out.println("No path Found!");
		} else {
			int from = bestEdge.getFrom(), to = bestEdge.getTo(), c = bestEdge.getCost(), t = bestEdge.getTime();
			ArrayList<Integer> temp = new ArrayList<Integer>();
			while (bestEdge != null) {
				temp.add(0, to);
				c -= edges.get(from + "-" + to).getCost();
				t -= edges.get(from + "-" + to).getTime();

				for (Edge eg : pathVertices[from]) {
					if (eg.getCost() == c && eg.getTime() == t) {
						from = eg.getFrom();
						to = eg.getTo();
						break;
					}
				}

				if (from == source && to == source) {
					temp.add(0, to);
					System.out.println("---------Shortest time in the given budget----------");
					System.out.println(source + " to " + dest + " in " + bestEdge.getTime() + " unit time costs $"
							+ bestEdge.getCost());
					System.out.println("Path: " + Arrays.toString(temp.toArray()));

					break;
				}
			}
		}
	}

	// cases where the edge does not need to be explored further
	public Boolean toIgnoreEdge(Integer cost, Integer time, Edge e) {
		return (cost > e.getCost() && time > e.getTime()) || (cost == e.getCost() && time > e.getTime())
				|| (cost > e.getCost() && time == e.getTime() || (cost == e.getCost() && time == e.getTime()));
	}

	// Comparator for dorting in order by cost asc and time desc
	@SuppressWarnings("rawtypes")
	class CustomComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			Edge s1 = (Edge) o1;
			Edge s2 = (Edge) o2;
			int ret;
			if (s1.getCost() == s2.getCost()) {
				if (s1.getTime() == s2.getTime())
					ret = 0;
				else if (s1.getTime() < s2.getTime())
					ret = 1;
				else
					ret = -1;
			} else if (s1.getCost() > s2.getCost())
				ret = 1;
			else
				ret = -1;
			return ret;
		}
	}

	// edge class
	class Edge implements Comparable<Edge> {

		private Integer from;
		private Integer to;
		private Integer cost;
		private Integer time;

		public Edge(Integer from, Integer to, Integer cost, Integer time) {
			super();
			this.from = from;
			this.to = to;
			this.cost = cost;
			this.time = time;
		}

		public Edge(Edge e) {
			super();
			this.from = e.getFrom();
			this.to = e.getTo();
			this.cost = e.getCost();
			this.time = e.getTime();
		}

		public Integer getFrom() {
			return from;
		}

		public void setFrom(Integer from) {
			this.from = from;
		}

		public Integer getTo() {
			return to;
		}

		public void setTo(Integer to) {
			this.to = to;
		}

		public Integer getCost() {
			return cost;
		}

		public void setCost(Integer cost) {
			this.cost = cost;
		}

		public Integer getTime() {
			return time;
		}

		public void setTime(Integer time) {
			this.time = time;
		}

		@Override
		public String toString() {
			return "Edge [from=" + from + ", to=" + to + ", cost=" + cost + ", time=" + time + "]";
		}

		@Override
		public int compareTo(Edge otherEdge) {
			int ret = this.getCost().compareTo(otherEdge.getCost());
			ret = ret == 0 ? this.getTime().compareTo(otherEdge.getTime()) : ret;
			return ret;
		}
	}

	// code to find all the paths
	// for (Edge e : pathVertices[dest]) {
	// int from = e.getFrom(), to = e.getTo(), c = e.getCost(), t = e.getTime();
	// ArrayList<Integer> temp = new ArrayList<Integer>();
	// while (true) {
	// temp.add(0, to);
	// c -= edges.get(from + "-" + to).getCost();
	// t -= edges.get(from + "-" + to).getTime();
	//
	// for (Edge eg : pathVertices[from]) {
	// if (eg.getCost() == c && eg.getTime() == t) {
	// from = eg.getFrom();
	// to = eg.getTo();
	// break;
	// }
	// }
	//
	// if (from == source && to == source) {
	// temp.add(0, to);
	// System.out.println(source + " to " + dest + " in $" + e.getCost() + " in " +
	// e.getTime()
	// + " unit time, using the path:");
	// System.out.println(Arrays.toString(temp.toArray()));
	//
	// if (e.getTime() < bestTime) {
	// bestEdge = e;
	// bestPath = temp;
	// bestTime = e.getTime();
	// }
	// break;
	// }
	// }
	// }

}
