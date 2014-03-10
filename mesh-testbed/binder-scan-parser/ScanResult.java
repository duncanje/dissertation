/*
 * Represents a single scan result
 */

public class ScanResult {
	
	private String description = "";
	
	/*
	 * Retrieves the signal level from scan description
	 */
	public int getSignalStrength() {
		try {
			String[] splitDescription = description.split("Signal level=");
			int i = 0;
			
			/* Find the end position of the signal */
			while (splitDescription[1].charAt(i) != ' ')
				i++;

			return Integer.parseInt(splitDescription[1].substring(0, i));
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Failed to retrieve signal strength for:\n" + description);
			return -1;
		}
	}
	
	/*
	 * Append to stored description
	 */
	public void appendDescription(String toAppend) {
		/* Remove any leading whitespace */
		toAppend = toAppend.replaceFirst("^\\s*", "");
		description += toAppend + "\n";
	}
	
	/*
	 * Return this result's description
	 */
	public String getDescription() {
		return description;
	}
	
}
