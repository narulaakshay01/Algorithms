import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class DTree {

	private static String input = "restaurant.csv";
	private static String output = "output.txt";
	private FileWriter writer;
	private static Map<SplitAreas, Set<String>> refer = new LinkedHashMap<SplitAreas, Set<String>>();

	// chi square values for 0.05 starting from degree of freedom 1
	private double[] chiSquare = { 3.84, 5.99, 7.81, 9.49, 11.07, 12.59, 14.07, 15.51, 16.92 };

	private boolean complete(List<String> list) {
		for (String s : list) {
			if (!s.equals(list.get(0)))
				return false;
		}
		return true;
	}

	private List<Integer> filterIndexes(List<String> list, String area) {
		List<Integer> temp = new ArrayList<>();
		int i = 0;
		for (i = 0; i < list.size(); i++) {
			if (list.get(i).equals(area)) {
				temp.add(i);
			}
		}
		return temp;
	}

	private void filterData(Map<SplitAreas, List<String>> areaSet, List<Integer> list) {
		for (SplitAreas f : areaSet.keySet()) {
			List<String> filteredAreas = new ArrayList<>();
			for (Integer row : list) {
				filteredAreas.add(areaSet.get(f).get(row));
			}
			areaSet.put(f, filteredAreas);
		}
	}

	private String getValue(List<String> domainValues) {
		Random rand = new Random();
		Map<String, Integer> countMap = new LinkedHashMap<>();
		for (String str : domainValues) {
			if (!countMap.containsKey(str)) {
				countMap.put(str, 0);
			}
			countMap.put(str, countMap.get(str) + 1);
		}
		int max = -1;
		String retStr = null;
		for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
			if (max == entry.getValue()) {
				retStr = rand.nextBoolean() ? entry.getKey() : retStr;
			}
			if (max < entry.getValue()) {
				max = entry.getValue();
				retStr = entry.getKey();
			}
		}
		return retStr;
	}

	private Node decisionTree(Map<SplitAreas, List<String>> areaSet, int split) throws IOException {
		if (areaSet.size() == 1) {
			Node node = new Node(getValue(areaSet.get(SplitAreas.Goal)));
			node.setValues(areaSet.get(SplitAreas.Goal));
			return node;
		}

		if (complete(areaSet.get(SplitAreas.Goal))) {
			Node node = new Node(areaSet.get(SplitAreas.Goal).get(0));
			node.setValues(areaSet.get(SplitAreas.Goal));
			return node;
		}

		writer.write("Split " + split + ":");
		writer.write("\n");
		List<SplitAreas> printList = new ArrayList<>(areaSet.keySet());
		List<Double> printGain = new ArrayList<Double>();
		printList.remove(SplitAreas.Goal);
		writer.write("Attributes :" + Arrays.toString(printList.toArray()));
		writer.write("\n");
		Double maxGain = Double.MIN_VALUE;
		double sourceEntropy = findEntropy(areaSet.get(SplitAreas.Goal));
		SplitAreas maxArea = null;
		for (SplitAreas sa : areaSet.keySet()) {
			if (sa.equals(SplitAreas.Goal)) {
				continue;
			}
			Double gain = findGain(sourceEntropy, areaSet.get(sa), areaSet.get(SplitAreas.Goal));
			printGain.add(gain);
			if (maxGain == null || maxGain < gain) {
				maxArea = sa;
				maxGain = gain;
			}
		}

		writer.write("Information Gain :" + Arrays.toString(printGain.toArray()));
		writer.write("\n");
		writer.write("With max gain of " + maxGain + " with split on " + maxArea);
		writer.write("\n");
		writer.write("\n");
		Node rootNode = new Node(maxArea.toString());
		rootNode.setValues(areaSet.get(SplitAreas.Goal));
		Set<String> area = new HashSet<>(areaSet.get(maxArea));
		Set<String> referArea = new HashSet<>(refer.get(maxArea));
		for (String temp : referArea) {
			if (!area.contains(temp)) {
				Node tempNode = new Node("Yes");
				tempNode.setValues(new ArrayList<String>());
				rootNode.getChildren().put(temp, tempNode);
				continue;
			}
			Map<SplitAreas, List<String>> filteredAreaSet = new LinkedHashMap<>(areaSet);
			filterData(filteredAreaSet, filterIndexes(areaSet.get(maxArea), temp));
			filteredAreaSet.remove(maxArea);

			Node node = decisionTree(filteredAreaSet, split + 1);
			rootNode.getChildren().put(temp, node);
		}
		return rootNode;
	}

	private void prune(Node node, String feature) {
		List<String> parent = node.getValues();
		double delta = 0;
		double p = 0;
		double n = 0;
		int d = -1;
		for (String s1 : parent) {
			if (s1.equals("Yes")) {
				p++;
			} else if (s1.equals("No")) {
				n++;
			}
		}
		for (String temp : node.getChildren().keySet()) {
			Node child = node.getChildren().get(temp);
			List<String> current = child.getValues();
			double pk = 0;
			double nk = 0;
			for (String s1 : current) {
				if (s1.equals("Yes")) {
					pk++;
				} else if (s1.equals("No")) {
					nk++;
				}
			}

			double tpk = p * ((pk + nk) / ((p + n)));
			double tnk = n * ((pk + nk) / ((p + n)));
			delta += (tpk != 0 ? (((pk - tpk) * (pk - tpk)) / tpk) : 0)
					+ (tnk != 0 ? (((nk - tnk) * (nk - tnk)) / tnk) : 0);
			d++;
		}

		if (d > 0 && chiSquare[d - 1] >= delta) {
			node.getChildren().clear();
			node.setData(p > n ? "Yes" : "No");
		}

		if (!node.getChildren().isEmpty()) {
			for (String s : node.getChildren().keySet())
				prune(node.getChildren().get(s), s);
		} else {

		}
	}

	private double findGain(double rootEntropy, List<String> area, List<String> targetArea) {
		Set<String> variables = new HashSet<>(area);
		double entropy = 0;
		for (String var : variables) {
			List<String> targetSubset = new ArrayList<>();
			int count = 0;
			int i = 0;
			for (String str : area) {
				if (str.equals(var)) {
					targetSubset.add(targetArea.get(i));
					count++;
				}
				i++;
			}
			double partialEntropy = (count / (double) area.size()) * findEntropy(targetSubset);
			entropy += partialEntropy;
		}
		return rootEntropy - entropy;
	}

	private double findEntropy(List<String> area) {
		Map<String, Integer> numberMap = new LinkedHashMap<>();

		for (String s : area) {
			if (!numberMap.containsKey(s)) {
				numberMap.put(s, 0);
			}
			numberMap.put(s, numberMap.get(s) + 1);
		}
		double entropy = 0;
		for (Map.Entry<String, Integer> entry : numberMap.entrySet()) {
			double p = entry.getValue() / (double) area.size();
			double tempEntropy = -p * (Math.log(p) / Math.log(2));
			entropy += tempEntropy;
		}
		return entropy;
	}

	private void printTree(String val, Node node, int tab) throws Exception {
		writer.write("\n");
		for (int i = 0; i < tab - 1; i++) {
			writer.write("|\t");
		}
		if (tab > 0) {
			writer.write("|>>>>");
		}
		if (val != null) {
			writer.write(val + " = ");
		}
		writer.write(node.getData());
		for (Map.Entry<String, Node> entry : node.getChildren().entrySet()) {
			printTree(entry.getKey(), entry.getValue(), tab + 1);
		}
	}

	private Map<SplitAreas, List<String>> readCSV() throws Exception {
		Map<SplitAreas, List<String>> areaMap = new LinkedHashMap<>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(input)));
		String input = null;
		while ((input = br.readLine()) != null) {
			String inputData[] = input.split(",");
			int i = 0;
			for (SplitAreas sa : SplitAreas.values()) {
				if (!areaMap.containsKey(sa)) {
					areaMap.put(sa, new ArrayList<>());
					refer.put(sa, new HashSet<>());
				}
				areaMap.get(sa).add(inputData[i].trim());
				refer.get(sa).add(inputData[i].trim());
				i++;
			}
		}
		br.close();
		return areaMap;

	}

	public void begin() throws Exception {
		writer = new FileWriter(output);
		Node root = decisionTree(readCSV(), 1);
		writer.write("Decision Tree");
		writer.write("\n");
		printTree(null, root, 0);
		writer.write("\n");
		prune(root, root.getData());
		writer.write("\n");
		writer.write("After Pruning");
		writer.write("\n");
		printTree(null, root, 0);

		writer.flush();
	}

	public static void main(String arg[]) throws Exception {
		DTree assignment13 = new DTree();
		assignment13.begin();
	}

	enum SplitAreas {
		Alt, Bar, Fri, Hun, Pat, Price, Rain, Res, Type, Est, Goal;
	}


	class Node {
		private String data;
		private Map<String, Node> children;
		private List<String> values;

		public Node(String data) {
			this.data = data;
			children = new LinkedHashMap<>();
		}

		public List<String> getValues() {
			return values;
		}

		public void setValues(List<String> values) {
			this.values = values;
		}

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

		public Map<String, Node> getChildren() {
			return children;
		}
	}

}