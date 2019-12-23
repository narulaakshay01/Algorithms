import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MDP {
	private static final String inputFile = "input.txt";
	private static final String outputFile = "output.txt";
	private static Double[][] grid;
	private static double reward;
	private static double gamma;
	private static double e;
	private static List<Double> directionProb = new ArrayList<>();
	private static int rows = -1, columns = -1;
	private static List<State> terminalStates = new ArrayList<>();
	private static Double d = Double.MIN_VALUE;

	public static void main(String args[]) throws Exception {
		List<String> ps = new ArrayList<String>(Arrays.asList(new String[] { "N", "S", "E", "W" }));

		List<String> input = getInputs();

		String[] size = input.get(0).split(":")[1].split(" ");
		grid = new Double[Integer.valueOf(size[2])][Integer.valueOf(size[1])];
		rows = grid.length;
		columns = grid[0].length;
		reward = getDoubleValue(input.get(3));
		gamma = getDoubleValue(input.get(5));
		e = getDoubleValue(input.get(6));

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				grid[i][j] = 0.0;
			}
		}

		setWall(input.get(1));

		setTerminalStates(input.get(2));

		setDirectionProbability(input.get(4));

		FileWriter fw = new FileWriter(outputFile);

		Double[][] prevGrid = new Double[rows][columns];
		State[][] policyGrid = new State[rows][columns];
		int valueCount = 0;

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				if (terminalStates.contains(new State(i, j))) {
					prevGrid[i][j] = 0.0;
					policyGrid[i][j] = new State(i, j);
					policyGrid[i][j].setVal(grid[i][j]);
					policyGrid[i][j].setPi("T");
					continue;
				}
				policyGrid[i][j] = new State(i, j);
				policyGrid[i][j].setVal(grid[i][j]);
				if (policyGrid[i][j].getVal() == null)
					policyGrid[i][j].setPi("-");
				else {

					policyGrid[i][j].setPi(ps.get(new Random().nextInt(4)));
				}
				prevGrid[i][j] = grid[i][j];
			}
		}

		do {
			d = 0.0;
			for (int i = 0; i < rows; i++)
				for (int j = 0; j < columns; j++) {
					if (prevGrid[i][j] == null) {
						grid[i][j] = prevGrid[i][j];
						continue;
					}
					if (terminalStates.contains(new State(i, j)))
						continue;
					Double up = directionProb.get(0) * getValue(prevGrid, i, j, i + 1, j)
							+ directionProb.get(1) * getValue(prevGrid, i, j, i, j - 1)
							+ directionProb.get(2) * getValue(prevGrid, i, j, i, j + 1)
							+ directionProb.get(3) * getValue(prevGrid, i, j, i - 1, j);

					Double down = directionProb.get(0) * getValue(prevGrid, i, j, i - 1, j)
							+ directionProb.get(1) * getValue(prevGrid, i, j, i, j + 1)
							+ directionProb.get(2) * getValue(prevGrid, i, j, i, j - 1)
							+ directionProb.get(3) * getValue(prevGrid, i, j, i + 1, j);

					Double left = directionProb.get(0) * getValue(prevGrid, i, j, i, j - 1)
							+ directionProb.get(1) * getValue(prevGrid, i, j, i - 1, j)
							+ directionProb.get(2) * getValue(prevGrid, i, j, i + 1, j)
							+ directionProb.get(3) * getValue(prevGrid, i, j, i, j + 1);

					Double right = directionProb.get(0) * getValue(prevGrid, i, j, i, j + 1)
							+ directionProb.get(1) * getValue(prevGrid, i, j, i + 1, j)
							+ directionProb.get(2) * getValue(prevGrid, i, j, i - 1, j)
							+ directionProb.get(3) * getValue(prevGrid, i, j, i, j - 1);
					grid[i][j] = Math.max(up, Math.max(down, Math.max(left, right))) * gamma + reward;

				}

			for (int i = 0; i < rows; i++)
				for (int j = 0; j < columns; j++)
					if (grid[i][j] != null && Math.abs(grid[i][j] - prevGrid[i][j]) > d) {
						d = Math.abs(grid[i][j] - prevGrid[i][j]);
					}

			writeInFile(fw, valueCount, prevGrid);
			valueCount++;

			for (int i = 0; i < rows; i++)
				for (int j = 0; j < columns; j++)
					prevGrid[i][j] = grid[i][j];

		} while (d > e * (1 - gamma) / gamma);

		writeInFile(fw, -1, prevGrid);

		// policy
		boolean unchanged = true;
		do {
			unchanged = true;
			for (int k = 0; k < 20; k++) {
				for (int i = 0; i < rows; i++)
					for (int j = 0; j < columns; j++) {
						if (policyGrid[i][j].getVal() == null) {
							continue;
						}
						if (terminalStates.contains(new State(i, j)))
							continue;
						Double sum = 0d;
						if (policyGrid[i][j].getPi().equals("S")) {
							sum = directionProb.get(0) * getValue(policyGrid, i, j, i + 1, j)
									+ directionProb.get(1) * getValue(policyGrid, i, j, i, j - 1)
									+ directionProb.get(2) * getValue(policyGrid, i, j, i, j + 1)
									+ directionProb.get(3) * getValue(policyGrid, i, j, i - 1, j);
						} else if (policyGrid[i][j].getPi().equals("N")) {
							sum = directionProb.get(0) * getValue(policyGrid, i, j, i - 1, j)
									+ directionProb.get(1) * getValue(policyGrid, i, j, i, j + 1)
									+ directionProb.get(2) * getValue(policyGrid, i, j, i, j - 1)
									+ directionProb.get(3) * getValue(policyGrid, i, j, i + 1, j);
						} else if (policyGrid[i][j].getPi().equals("W")) {
							sum = directionProb.get(0) * getValue(policyGrid, i, j, i, j - 1)
									+ directionProb.get(1) * getValue(policyGrid, i, j, i - 1, j)
									+ directionProb.get(2) * getValue(policyGrid, i, j, i + 1, j)
									+ directionProb.get(3) * getValue(policyGrid, i, j, i, j + 1);
						} else if (policyGrid[i][j].getPi().equals("E")) {
							sum = directionProb.get(0) * getValue(policyGrid, i, j, i, j + 1)
									+ directionProb.get(1) * getValue(policyGrid, i, j, i + 1, j)
									+ directionProb.get(2) * getValue(policyGrid, i, j, i - 1, j)
									+ directionProb.get(3) * getValue(policyGrid, i, j, i, j - 1);
						}
						policyGrid[i][j].setVal(reward + gamma * sum);

					}
			}
			// Processing states !
			for (int i = 0; i < rows; i++)
				for (int j = 0; j < columns; j++) {
					if (policyGrid[i][j].getVal() == null) {
						continue;
					}
					if (terminalStates.contains(new State(i, j)))
						continue;

					Double down = directionProb.get(0) * getValue(policyGrid, i, j, i + 1, j)
							+ directionProb.get(1) * getValue(policyGrid, i, j, i, j - 1)
							+ directionProb.get(2) * getValue(policyGrid, i, j, i, j + 1)
							+ directionProb.get(3) * getValue(policyGrid, i, j, i - 1, j);
					String maxPi = "S";
					Double max = down;

					Double up = directionProb.get(0) * getValue(policyGrid, i, j, i - 1, j)
							+ directionProb.get(1) * getValue(policyGrid, i, j, i, j + 1)
							+ directionProb.get(2) * getValue(policyGrid, i, j, i, j - 1)
							+ directionProb.get(3) * getValue(policyGrid, i, j, i + 1, j);
					if (up > max) {
						maxPi = "N";
						max = up;
					}

					Double left = directionProb.get(0) * getValue(policyGrid, i, j, i, j - 1)
							+ directionProb.get(1) * getValue(policyGrid, i, j, i - 1, j)
							+ directionProb.get(2) * getValue(policyGrid, i, j, i + 1, j)
							+ directionProb.get(3) * getValue(policyGrid, i, j, i, j + 1);
					if (left > max) {
						maxPi = "W";
						max = left;
					}

					Double right = directionProb.get(0) * getValue(policyGrid, i, j, i, j + 1)
							+ directionProb.get(1) * getValue(policyGrid, i, j, i + 1, j)
							+ directionProb.get(2) * getValue(policyGrid, i, j, i - 1, j)
							+ directionProb.get(3) * getValue(policyGrid, i, j, i, j - 1);
					if (right > max) {
						maxPi = "E";
						max = right;
					}

					Double sum = 0d;
					if (policyGrid[i][j].getPi().equals("S")) {
						sum = directionProb.get(0) * getValue(policyGrid, i, j, i + 1, j)
								+ directionProb.get(1) * getValue(policyGrid, i, j, i, j - 1)
								+ directionProb.get(2) * getValue(policyGrid, i, j, i, j + 1)
								+ directionProb.get(3) * getValue(policyGrid, i, j, i - 1, j);
					} else if (policyGrid[i][j].getPi().equals("N")) {
						sum = directionProb.get(0) * getValue(policyGrid, i, j, i - 1, j)
								+ directionProb.get(1) * getValue(policyGrid, i, j, i, j + 1)
								+ directionProb.get(2) * getValue(policyGrid, i, j, i, j - 1)
								+ directionProb.get(3) * getValue(policyGrid, i, j, i + 1, j);
					} else if (policyGrid[i][j].getPi().equals("W")) {
						sum = directionProb.get(0) * getValue(policyGrid, i, j, i, j - 1)
								+ directionProb.get(1) * getValue(policyGrid, i, j, i - 1, j)
								+ directionProb.get(2) * getValue(policyGrid, i, j, i + 1, j)
								+ directionProb.get(3) * getValue(policyGrid, i, j, i, j + 1);
					} else if (policyGrid[i][j].getPi().equals("E")) {
						sum = directionProb.get(0) * getValue(policyGrid, i, j, i, j + 1)
								+ directionProb.get(1) * getValue(policyGrid, i, j, i + 1, j)
								+ directionProb.get(2) * getValue(policyGrid, i, j, i - 1, j)
								+ directionProb.get(3) * getValue(policyGrid, i, j, i, j - 1);
					}

					if (max > sum) {
						if (!policyGrid[i][j].getPi().equals(maxPi)) {
							policyGrid[i][j].setPi(maxPi);
							unchanged = false;
						}
					}
				}
		} while (!unchanged);

		writeInFile(fw, policyGrid);
		fw.flush();
		fw.close();
	}

	private static void writeInFile(FileWriter fw, int it, Double[][] oldsquare) throws Exception {
		if (it == -1)
			fw.write("\nFinal Value After Convergence: ");
		else
			fw.write("\nIteration: " + it);
		it++;
		for (int i = 0; i < rows; i++) {
			fw.write("\n");
			for (int j = 0; j < columns; j++) {
				fw.write(" " + String.valueOf(oldsquare[i][j]));
			}
			fw.write("\n");
		}
	}

	private static void writeInFile(FileWriter fw, State[][] oldsquare) throws Exception {
		fw.write("\nFinal Policy: ");
		for (int i = 0; i < rows; i++) {
			fw.write("\n");
			for (int j = 0; j < columns; j++) {
				fw.write(" " + String.valueOf(oldsquare[i][j].getPi()));
			}
		}
	}

	private static Double getValue(Double[][] oldsquare, int curI, int curJ, int i, int j) {
		i = i < 0 ? 0 : i;
		i = i == rows ? rows - 1 : i;
		j = j < 0 ? 0 : j;
		j = j == columns ? columns - 1 : j;
		if (oldsquare[i][j] == null)
			return oldsquare[curI][curJ];
		return oldsquare[i][j];
	}

	private static Double getValue(State[][] oldsquare, int curI, int curJ, int i, int j) {
		i = i < 0 ? 0 : i;
		i = i == rows ? rows - 1 : i;
		j = j < 0 ? 0 : j;
		j = j == columns ? columns - 1 : j;
		if (oldsquare[i][j].getVal() == null)
			return oldsquare[curI][curJ].getVal();
		return oldsquare[i][j].getVal();
	}

	private static void setDirectionProbability(String data) {
		String[] probList = data.split(":")[1].split(" ");
		for (String prob : probList) {
			if (prob.trim().equals(""))
				continue;
			directionProb.add(Double.valueOf(prob.trim()));
		}
	}

	private static Double getDoubleValue(String data) {
		return Double.valueOf(data.split(":")[1].trim());
	}

	private static void setTerminalStates(String data) {
		String terminalStrList[] = data.split(":")[1].split(",");
		for (String terminalStr : terminalStrList) {
			String terminalVals[] = terminalStr.split(" ");
			int x = -1, y = -1;
			Double val = -1.0;
			for (String terminalId : terminalVals) {
				if (terminalId.trim().equals(""))
					continue;
				if (x == -1) {
					x = Integer.valueOf(terminalId.trim());
					continue;
				}
				if (y == -1) {
					y = Integer.valueOf(terminalId.trim());
					continue;
				}
				val = Double.valueOf(terminalId.trim());
			}
			x--;
			y--;
			grid[rows - 1 - y][x] = val;
			terminalStates.add(new State(rows - 1 - y, x));

		}
	}

	private static void setWall(String data) {
		String wallStrList[] = data.split(":")[1].split(",");
		for (String wallStr : wallStrList) {
			String wallLoc[] = wallStr.split(" ");
			int x = -1, y = -1;
			for (String wallId : wallLoc) {
				if (wallId.trim().equals("")) {
					continue;
				}
				if (x == -1)
					x = Integer.valueOf(wallId.trim());
				else
					y = Integer.valueOf(wallId.trim());
			}
			x--;
			y--;
			grid[rows - 1 - y][x] = null;
		}
	}

	private static List<String> getInputs() throws Exception {
		List<String> input = new ArrayList<>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
		String tmp = null;
		while ((tmp = br.readLine()) != null) {
			if (tmp.equals("") || tmp.charAt(0) == '#')
				continue;
			input.add(tmp);
		}
		br.close();
		return input;
	}
}

class State {
	private int x;
	private int y;
	private String pi;
	private Double val;

	public State(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String getPi() {
		return pi;
	}

	public void setPi(String pi) {
		this.pi = pi;
	}

	public Double getVal() {
		return val;
	}

	public void setVal(Double val) {
		this.val = val;
	}

	@Override
	public boolean equals(Object o) {
		State p = (State) o;
		return this.x == p.getX() && this.y == p.getY();
	}

	@Override
	public int hashCode() {
		return (x * 31 + y * 23) / 119;
	}
}
