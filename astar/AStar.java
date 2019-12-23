import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AStar {

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

		System.out.println();
		System.out.println("----Hamming (Number of misplaced tiles)----");
		// collect garbage
		System.gc();
		try {
			aStarHamming(new Node(String.join("-", puzzle), puzzle, null, 0, "root"));
		} catch (Exception e) {
			System.out.println("Could not be solved!");
		}
		
		System.out.println();
		System.out.println("----Manhattan (Difference in position)----");
		// clear garbage
		System.gc();
		try {
			aStarManhattan(new Node(String.join("-", puzzle), puzzle, null, 0, "root"));
		} catch (Exception e) {
			System.out.println("Could not be solved!");
		}
	}

	public static void aStarManhattan(Node root) {

		ArrayList<Node> unexpandedNodes = new ArrayList<Node>();
		unexpandedNodes.add(root);

		int expandedNodes = 0;
		long startTime = System.currentTimeMillis();

		Node focus = new Node();
		while (true) {
			int min = Integer.MAX_VALUE;
			
			//choose the next state based on the heuristic
			for (Node temp : unexpandedNodes) {
				int manDistSum = manhattanDistance(temp.getData());
				if (manDistSum + temp.getLevel() < min) {
					min = manDistSum + temp.getLevel();
					focus = temp;
				}
			}
			unexpandedNodes.remove(focus);

			if (manhattanDistance(focus.getData()) != 0) {
				expandedNodes++;
				unexpandedNodes.addAll(expantion(focus));
			} else {
				System.out.println("Moves: " + track(focus));
				System.out.println("Number of Nodes expanded: " + expandedNodes);
				System.out.println("Time Taken: " + (System.currentTimeMillis() - startTime));
				Runtime runtime = Runtime.getRuntime();
				System.out.println("Memory Used: " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024)));
				break;
			}
		}
	}

	public static void aStarHamming(Node root) {
		ArrayList<Node> unexpandedNodes = new ArrayList<Node>();
		unexpandedNodes.add(root);

		int expandedNodes = 0;
		long startTime = System.currentTimeMillis();

		Node focus = null;
		while (true) {
			int min = Integer.MAX_VALUE;
			
			//choose the next state based on the heuristic
			for (Node temp : unexpandedNodes) {
				int hamDist = hammingDistance(temp.getData());
				if (hamDist + temp.getLevel() < min) {
					min = hamDist + temp.getLevel();
					focus = temp;
				}
			}
			unexpandedNodes.remove(focus);

			if (hammingDistance(focus.getData()) != 0) {
				expandedNodes++;
				unexpandedNodes.addAll(expantion(focus));
			} else {
				System.out.println("Moves: " + track(focus));
				System.out.println("Number of Nodes expanded: " + expandedNodes);
				System.out.println("Time Taken: " + (System.currentTimeMillis() - startTime));
				Runtime runtime = Runtime.getRuntime();
				System.out.println("Memory Used: " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024)));
				break;
			}
		}
	}

	// manhattan distance sum
	public static int manhattanDistance(String[] data) {
		int sum = 0;
		for (int i = 0; i < data.length; i++) {
			int y, x;
			int val = Integer.parseInt(data[i]);
			if (val >= 1 && val <= 4) {
				y = 0;
				x = val - 1;
			} else if (val >= 5 && val <= 8) {
				y = 1;
				x = val - 5;
			} else if (val >= 9 && val <= 12) {
				y = 2;
				x = val - 9;
			} else if (val >= 13 && val <= 15) {
				y = 3;
				x = val - 13;
			} else {
				y = 3;
				x = 3;
			}

			sum = sum + Math.abs((i / 4) - y) + Math.abs((i % 4) - x);
		}
		return sum;
	}

	// Hamming distance (Misplaced tiles)
	public static int hammingDistance(String[] data) {
		int[] output = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0 };
		int count = 0;
		for (int i = 0; i < output.length; i++) {
			if (output[i] != Integer.parseInt(data[i]))
				count++;
		}
		return count;
	}

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

	public static String track(Node solution) {
		Node focus = solution;
		String path = "";
		while (true) {
			if (focus.getMove().equals("root"))
				break;
			path = focus.getMove() + path;
			focus = focus.getParent();
		}
		return path;
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