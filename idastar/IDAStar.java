import java.util.Scanner;
import java.util.Stack;
import java.util.ArrayList;
import java.util.List;

public class IDAStar {

	private static int nodesCount = 0;
	private static Node finalNode;

	public static void solveManhattan(Node start) {

		int depth = 0;
		long begin = System.currentTimeMillis();
		boolean possible = false;

		depth = start.getHeuristic();
		Stack<Node> stack = new Stack<Node>();

		while (!possible) {
			stack.push(start);
			possible = idaStarManhattan(depth, "M", stack, possible);
			depth++;
		}

		printSolution();
		System.out.println("Number of Nodes expanded: " + nodesCount);
		System.out.println("Time taken: " + (System.currentTimeMillis() - begin) + "ms");
	}

	public static boolean idaStarManhattan(int depth, String heuristic, Stack<Node> stack, boolean done) {
		while (!stack.isEmpty()) {

			Node node = stack.pop();

			if (checkGoal(node)) {
				done = true;
				finalNode = node;
				stack.clear();
			}

			if (depth >= node.getHeuristic()) {

				if (node.expandable()) {
					stack.addAll(node.expantion(heuristic));
					nodesCount++;
				}

			}
		}
		return done;
	}

	// Misplaced tiles
	static void solveHamming(Node start) throws Exception {
		int depth = start.getLevel();
		long begin = System.currentTimeMillis();
		Stack<Node> stack = new Stack<Node>();
		stack.add(start);
		while (true) {
			int temp = idaStarHamming(stack, 0, depth);
			if (temp == 0 || temp == Integer.MAX_VALUE)
				break;
			if ((System.currentTimeMillis() - begin) > 5000)
				throw new Exception("Can not be done!");
			depth = temp;
		}

		printSolution();
		System.out.println("Number of Nodes expanded: " + nodesCount);
		System.out.println("Time taken: " + (System.currentTimeMillis() - begin) + "ms");
	}

	static int idaStarHamming(Stack<Node> stack, int distance, int depth) {
		Node node = stack.get(stack.size() - 1);

		if (checkGoal(node)) {
			finalNode = node;
			return 0;
		}
		nodesCount++;

		int func = distance + node.getLevel();
		if (func > depth)
			return func;

		int min = Integer.MAX_VALUE;
		for (Node child : node.expantion("H")) {
			if (checkForNode(stack, child)) {
				stack.push(child);
				int temp = idaStarHamming(stack, distance + child.getHeuristic(), depth);
				if (temp == 0)
					return 0;
				if (temp < min)
					min = temp;
				stack.pop();
			}
		}
		return min;
	}

	static boolean checkForNode(Stack<Node> stack, Node child) {
		for (Node c : stack) {
			if (child.getId().equals(c.getId()))
				return false;
		}
		return true;
	}

	public static void printSolution() {

		Node solState = finalNode;
		String path = "";

		while (solState != null && !solState.getMove().equals("root")) {

			path = solState.getMove() + path;
			solState = solState.getParent();
		}

		if (path == "" && !solState.getMove().equals("root")) {
			path = "Can not be done!";
		}
		System.out.println("Moves: " + path);
	}

	public static boolean checkGoal(Node node) {
		if (node.getId().equals("1-2-3-4-5-6-7-8-9-10-11-12-13-14-15-0")) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {

		System.out.println("Please enter the puzzle matrix: ");

		// 1 0 3 4 5 2 6 8 9 10 7 11 13 14 15 12 | DRDRD //
		// 1 2 3 4 5 6 8 0 9 11 7 12 13 10 14 15 | LDLDRR //
		// 1 0 2 4 5 7 3 8 9 6 11 12 13 10 14 15 | RDLDDRR //
		// 1 2 0 4 6 7 3 8 5 9 10 12 13 14 11 15 | DLLDRRDR //
		// 1 3 4 8 5 2 0 6 9 10 7 11 13 14 15 12 | RULLDRDRD//
		// 5 2 4 8 10 3 11 14 6 0 9 12 13 1 15 7 | drruulldrruulldldrrdluulurrddrd
		// 5 2 4 8 10 0 3 14 13 6 11 12 1 15 9 7 |
		// rddllurdrruulldrruulldldrrdluulurrddrd

		Scanner in = new Scanner(System.in);
		String inputString = in.nextLine();
		in.close();

		String[] puzzle = inputString.split(" ");

		System.out.println();
		System.out.println("--------------Manhattan------------------");
		System.gc();
		long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		solveManhattan(new Node(String.join("-", puzzle), puzzle, null, 0, "root", "M"));
		long memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		System.out.println("Memory Used : " + Long.toString((memoryAfter - memoryBefore) / (1024 * 8)) + " Kb");

		System.out.println();
		System.out.println("-----------------Hamming------------------");
		System.gc();
		nodesCount = 0;
		try {
			memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			solveHamming(new Node(String.join("-", puzzle), puzzle, null, 0, "root", "H"));
			memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			System.out.println("Memory Used : " + Long.toString((memoryAfter - memoryBefore) / (1024 * 8)) + " Kb");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

public static class Node {

	private String id;

	private String[] data;

	private Node parent;

	private List<Node> children;

	private int level;

	private String move;

	private int heuristic;

	public Node(String id, String[] data, Node parent, int level, String move, String heuristic) {
		super();
		this.id = id;
		this.data = data;
		this.parent = parent;
		this.children = new ArrayList<Node>();
		this.level = level;
		this.move = move;
		this.heuristic = heuristic.equals("M") ? manhattanDistance() : hammingDistance();
	}

	public Node() {
		super();
	}

	public int getHeuristic() {
		return heuristic;
	}

	public void setHeuristic(int heuristic) {
		this.heuristic = heuristic;
	}

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

	public boolean expandable() {
		if (this.getLevel() > 1) {
			if (!this.getParent().getParent().getId().equals(this.getId())) {
				return true;
			}
		} else {
			return true;
		}
		return false;
	}

	private int findIndex(String s, String[] data) {
		int index = -1;
		for (int i = 0; i < data.length; i++) {
			if (s.equals(data[i])) {
				index = i;
				break;
			}
		}
		return index;
	}

	private int manhattanDistance() {

		int distance = 0;

		for (int i = 0; i < this.getData().length; i++) {
			if (!this.getData()[i].equals("0")) {
				int index = findIndex(this.getData()[i], this.getData());

				int srcX = index % 4;
				int srcY = index / 4;

				int destX = (Integer.parseInt(this.getData()[i]) - 1) % 4;
				int destY = (Integer.parseInt(this.getData()[i]) - 1) / 4;

				distance += Math.abs(srcX - destX) + Math.abs(srcY - destY);
			}
		}

		return distance + this.getLevel();
	}

	private int hammingDistance() {
		int[] output = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0 };
		int count = 0;
		for (int i = 0; i < output.length; i++) {
			if (output[i] != Integer.parseInt(this.getData()[i]))
				count++;
		}
		return count;
	}

	private void swap(String[] data, int i, int j) {

		String temp = data[i];
		data[i] = data[j];
		data[j] = temp;
	}

	public List<Node> expantion(String heuristic) {

		List<Node> children = new ArrayList<>();
		String[] expantionData = this.getData().clone();

		int i;
		for (i = 0; i < expantionData.length; i++)
			if (expantionData[i].equals("0"))
				break;

		if (i > 3) {
			swap(expantionData, i, i - 4); // move up
			Node child = new Node(String.join("-", expantionData), expantionData.clone(), this, this.getLevel() + 1,
					"U", heuristic);
			children.add(child);
			swap(expantionData, i, i - 4);
		}
		if (i < 12) {
			swap(expantionData, i, i + 4); // move down
			Node child = new Node(String.join("-", expantionData), expantionData.clone(), this, this.getLevel() + 1,
					"D", heuristic);
			children.add(child);
			swap(expantionData, i, i + 4);
		}
		if (i % 4 != 0) {
			swap(expantionData, i, i - 1); // move left
			Node child = new Node(String.join("-", expantionData), expantionData.clone(), this, this.getLevel() + 1,
					"L", heuristic);
			children.add(child);
			swap(expantionData, i, i - 1);
		}
		if (i % 4 != 3) {
			swap(expantionData, i, i + 1); // move right
			Node child = new Node(String.join("-", expantionData), expantionData.clone(), this, this.getLevel() + 1,
					"R", heuristic);
			children.add(child);
			swap(expantionData, i, i + 1);
		}

		return children;
	}

}
}
