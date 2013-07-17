package edu.yu.einstein.wasp.web.panel;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents a tab display component which acts as a container for displaying a group of Panel objects
 * @author asmclellan
 *
 */
public class PanelTab {
	
	private Set<Panel> panels = new LinkedHashSet<>();
	
	private String name = "";
	
	private int numberOfColumns = 2; //default
	
	public PanelTab(Set<Panel> panels, String name, int numberOfColumns) {
		this.panels = panels;
		this.name = name;
		this.numberOfColumns = numberOfColumns;
	}

	public Set<Panel> getPanels() {
		return panels;
	}

	public void setPanels(Set<Panel> panels) {
		this.panels = panels;
	}
	
	public void addPanel(Panel panel) {
		this.panels.add(panel);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumberOfColumns() {
		return numberOfColumns;
	}

	public void setNumberOfColumns(int numberOfColumns) {
		this.numberOfColumns = numberOfColumns;
	}


}
