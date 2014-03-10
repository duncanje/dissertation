import java.io.*;
import java.util.*;

public class BinderScanParser {
	
	/* Map interface to respective scan results */
	private HashMap<String, List<ScanResult>> interfaceResultMap;
	
	private List<ScanResult> currentResultList;
	private ScanResult currentResult;
	
	private String scanFile;
	private String outDir;
	
	public BinderScanParser(String scanFile, String outDir) {
		interfaceResultMap = new HashMap<String, List<ScanResult>>();
		this.scanFile = scanFile;
		this.outDir = outDir;
		
		parseInput();
		writeOutput();
	}
	
	/*
	 * Sort result list in descending order of signal strength
	 * Not exactly optimal, but works fine for this use case
	 */
	private void insertResult() {
		int i = 0;
		
		while (i < currentResultList.size()) {
			// If passed result is greater than result at current pos then break (to insert)
			if (currentResult.getSignalStrength() > currentResultList.get(i).getSignalStrength())
				break;
			else
				i++;
		}
		currentResultList.add(i, currentResult);
	}
	
	/*
	 * Parse the input file
	 */
	public void parseInput() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(scanFile));
			String line;
			
			/* Loop through all lines in file */
			while ((line = reader.readLine()) != null) {
				/* Start of an interface scan results */
				if (line.contains("wlan") || line.contains("ath")) {
					currentResultList = new ArrayList<ScanResult>();
					interfaceResultMap.put(line, currentResultList);
					currentResult = new ScanResult();
				}
				/* De-limiter between scan an interface's scan results */
				else if (line.equals("--")) {
					insertResult();
					currentResult = new ScanResult();
				}
				else {
					currentResult.appendDescription(line);
				}
			}
			if (currentResult != null)
				insertResult();
			reader.close();
			
		} catch (Exception e) {
			System.err.println("Failed to read from " + scanFile);
			System.err.println(e);
			System.exit(1);
		}
	}
	
	public void writeOutput() {
		/* Loop through each interface */
		for (String interfaceId : interfaceResultMap.keySet()) {
			/* Create file for this interface's scan results */
			File outFile = null;
			try {
				outFile = new File(outDir, interfaceId);
				outFile.createNewFile();
			} catch (IOException e) {
				System.err.println("Failed to create interface output file " + outFile);
				System.err.println(e);
				System.exit(1);
			}
			
			try {
				PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));
				
				/* Write file heading */
				writer.write(scanFile + " - " + interfaceId + "\n----------\n");
				
				/* Write each scan result to the output file */
				for (ScanResult result : interfaceResultMap.get(interfaceId)) {
					writer.write(result.getDescription());
					writer.println();
				}
				writer.close();
			} catch (IOException e) {
				System.err.println("Failed to write interface output file " + outFile);
				System.err.println(e);
				System.exit(1);
			}
			
		}
	}
	
	public static void main(String[] args) {
		try {
			new BinderScanParser(args[0], args[1]);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Required arguments: <scan_file> <output_dir>");
			System.exit(1);
		}
	}
	
}
