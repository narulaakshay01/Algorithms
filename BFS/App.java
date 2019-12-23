import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {

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
		sc.close();
		String[] puzzle = inputArray.split(" ");
		String[] output = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14",
				"15", "0" };
		String outputId = String.join("-", output);
		Boolean solvable = true;

		long startTime = System.nanoTime();
		long beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

		int expanded = 0;
		List<String> closedList = new ArrayList<String>();
		List<Node> openList = new ArrayList<Node>();

		openList.add(new Node(String.join("-", puzzle), puzzle, null, 0, "ROOT"));
		Node currentNode = new Node();
		while (true) {
			currentNode = openList.remove(0);
			
			currentNode.setChildren(expantion(currentNode));
			for (Node n : currentNode.getChildren()) {
				if (!closedList.contains(n.getId())) {
					closedList.add(n.getId());
					openList.add(n);
				}
			}

			if (currentNode.getId().equals(outputId))
				break;
			else if (openList.size() == 0 || expanded>=10000) {
				solvable = false;
				break;
			}
			expanded++;
		}

		long endTime = System.nanoTime();
		long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

		String moves = "";

		while (solvable) {
			if (currentNode.getParent() != null) {
				moves = currentNode.getMove() + moves;
				currentNode = currentNode.getParent();
			} else
				break;
		}
		System.out.println("Moves: " + (solvable ? moves : "Not Solvable"));
		System.out.println("Number of Nodes expanded: " + expanded);
		System.out.println("Time Taken: " + ((endTime - startTime) / 1000000) + " ms");
		System.out.println("Memory Used: " + (afterUsedMem - beforeUsedMem) / (1024L) + " kb");
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
