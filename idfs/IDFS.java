import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class IDFS {

	private static int expanded = 0;
	private static Map<String, Integer> closed = new HashMap<String, Integer>();

	private static String[] output = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13",
			"14", "15", "0" };
	private static String outputId = String.join("-", output);

	private static Boolean solvable = true;

	private static String path = "";

	private static void swap(String[] data, int i, int j) {

		String temp = data[i];
		data[i] = data[j];
		data[j] = temp;
	}

	private static List<Node> expantion(Node parent) {

		List<Node> children = new ArrayList<>();
		String[] expantionData = parent.getData().clone();

		int i;
		for (i = 0; i < expantionData.length; i++)
			if (expantionData[i].equals("0"))
				break;

		if (i > 3) {
			swap(expantionData, i, i - 4); // move up
			Node child = new Node(String.join("-", expantionData), expantionData.clone(), parent, parent.getLevel() + 1,
					"U");
			children.add(child);
			swap(expantionData, i, i - 4);
		}
		if (i < 12) {
			swap(expantionData, i, i + 4); // move down
			Node child = new Node(String.join("-", expantionData), expantionData.clone(), parent, parent.getLevel() + 1,
					"D");
			children.add(child);
			swap(expantionData, i, i + 4);
		}
		if (i % 4 != 0) {
			swap(expantionData, i, i - 1); // move left
			Node child = new Node(String.join("-", expantionData), expantionData.clone(), parent, parent.getLevel() + 1,
					"L");
			children.add(child);
			swap(expantionData, i, i - 1);
		}
		if (i % 4 != 3) {
			swap(expantionData, i, i + 1); // move right
			Node child = new Node(String.join("-", expantionData), expantionData.clone(), parent, parent.getLevel() + 1,
					"R");
			children.add(child);
			swap(expantionData, i, i + 1);
		}

		return children;
	}

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		System.out.println("Please enter the puzzle matrix:");
		String inputArray = sc.nextLine();
		// 1 0 3 4 5 2 6 8 9 10 7 11 13 14 15 12 | DRDRD //
		// 1 2 3 4 5 6 8 0 9 11 7 12 13 10 14 15 | LDLDRR //
		// 1 0 2 4 5 7 3 8 9 6 11 12 13 10 14 15 | RDLDDRR //
		// 1 2 0 4 6 7 3 8 5 9 10 12 13 14 11 15 | DLLDRRDR //
		// 1 3 4 8 5 2 0 6 9 10 7 11 13 14 15 12 | RULLDRDRD//
		sc.close();
		String[] puzzle = inputArray.split(" ");

		long startTime = System.nanoTime();
		long beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

		for (int depth = 0; depth < Integer.MAX_VALUE; depth++) {
			Node found = IDDFS(new Node(String.join("-", puzzle), puzzle, null, 0, "root"), depth);
			if (found != null) {
				solvable = true;
				break;
			} else {
				solvable = false;
			}
			closed.clear();
		}

		long endTime = System.nanoTime();
		long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

		System.out.println("Moves: " + (solvable ? path : "Not Solvable"));
		System.out.println("Number of Nodes expanded: " + expanded);
		System.out.println("Time Taken: " + ((endTime - startTime) / 1000000) + " ms");
		System.out.println("Memory Used: " + (afterUsedMem - beforeUsedMem) / (1024L) + " kb");
	}

	public static Node IDDFS(Node current, int depth) {
		expanded++;
		if (depth == 0 && current.getId().equals(outputId)) {
			return current;
		}
		if (depth > 0) {
			for (Node child : expantion(current)) {
				if (closed.get(child.getId()) == null) {
					closed.put(child.getId(), depth);
				} else {
					if (closed.get(child.getId()) < depth) {
						closed.put(child.getId(), depth);
					} else {
						continue;
					}
				}
				Node found = IDDFS(child, depth - 1);
				if (found != null) {
					path = child.getMove() + path;
					return found;
				}
			}
		}
		return null;
	}



	public static class Node {

		private String id;

		private String[] data;

		private Node parent;

		private List<Node> children;

		private int level;

		private String move;
		
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String[] getData() {
			return data;
		}

		public void setData(String[] data) {
			this.data = data;
		}

		public Node getParent() {
			return parent;
		}

		public void setParent(Node parent) {
			this.parent = parent;
		}

		public List<Node> getChildren() {
			return children;
		}

		public void setChildren(List<Node> children) {
			this.children = children;
		}

		public int getLevel() {
			return level;
		}

		public void setLevel(int level) {
			this.level = level;
		}

		public String getMove() {
			return move;
		}

		public void setMove(String move) {
			this.move = move;
		}

		public Node(String id, String[] data, Node parent, int level, String move) {
			super();
			this.id = id;
			this.data = data;
			this.parent = parent;
			this.children = new ArrayList<Node>();
			this.level = level;
			this.move = move;
		}

		public Node() {
			super();
		}

	}
}