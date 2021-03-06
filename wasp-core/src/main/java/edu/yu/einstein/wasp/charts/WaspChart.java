package edu.yu.einstein.wasp.charts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import edu.yu.einstein.wasp.service.MessageService;

/**
 * Base class to generalize all charts
 * @author asmclellan
 *
 */
public class WaspChart {
	
	protected String title;
	
	protected String description;
	
	protected List<DataSeries> dataSeries;
	
	protected Map<String, Object> properties;

	public WaspChart() {
		dataSeries = new ArrayList<DataSeries>();
		properties = new HashMap<String, Object>();
		title = "";
		description = "";
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getLocalizedTitle(MessageService messageService) {
		return messageService.getMessage(title);
	}
	
	public String getLocalizedTitle(MessageService messageService, Locale locale) {
		return messageService.getMessage(title, locale);
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getLocalizedDescription(MessageService messageService) {
		return messageService.getMessage(description);
	}
	
	public String getLocalizedDescription(MessageService messageService, Locale locale) {
		return messageService.getMessage(description, locale);
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<DataSeries> getDataSeries() {
		return dataSeries;
	}

	public void setDataSeries(List<DataSeries> dataSeries) {
		this.dataSeries = dataSeries;
	}
	
	/**
	 * Add a data series
	 * @param dataSeries
	 */
	@JsonIgnore
	public void addDataSeries(DataSeries dataSeries){
		this.dataSeries.add(dataSeries);
	}
	
	/**
	 * Get a generic map of properties which may be associated with this plot or an empty Map if none set.
	 * @param properties
	 */
	public Map<String, Object> getProperties() {
		return properties;
	}

	/**
	 * Set a generic map of properties which may be associated with this plot
	 * @param properties
	 */
	@SuppressWarnings("unchecked")
	public void setProperties(Map<String, ?> properties) {
		this.properties = (Map<String,Object>) properties;
	}
	
	/**
	 * Add a property
	 * @param key
	 * @param value
	 */
	@JsonIgnore
	public void addProperty(String key, Object value){
		properties.put(key, value);
	}
	
	@JsonIgnore
	public DataSeries getDataSeries(String name){
		if (dataSeries == null)
			return null;
		for (DataSeries currentSeries : dataSeries)
			if (currentSeries.getName().equals(name))
				return currentSeries;
		return null;
	}
	
	@JsonIgnore
	protected DataSeries getDataSeriesOrCreateNew(String name){
		DataSeries data = this.getDataSeries(name);
		if (data == null){
			data = new DataSeries(name);
			if (this.dataSeries == null)
				this.dataSeries = new ArrayList<DataSeries>();
			this.dataSeries.add(data);
		}
		return data;
	}
	
	/**
	 * sets parameters based on JSON input
	 * @param <T>
	 * @param json
	 * @return
	 * @throws JSONException
	 */
	@JsonIgnore
	public static <T extends WaspChart> T getChart(JSONObject json, Class<T> clazz) throws JSONException{
		ObjectMapper mapper = new ObjectMapper();
		try{
			return mapper.readValue(json.toString(), clazz);
		} catch(Exception e){
			throw new JSONException("Cannot create object of type " + clazz.getName() + " from JSON. Caught exception of type " + 
					e.getClass().getName() + " : " + e.getLocalizedMessage());
		}
	}
	
	@JsonIgnore
	public JSONObject getAsJSON() throws JSONException {
		ObjectMapper mapper = new ObjectMapper();
		try {
			// use jackson object mapper to create json as text then wrap in JSONObject (Jackson understands @JsonIgnore)
			return new JSONObject(mapper.writeValueAsString(this));
		} catch (Exception e) {
			throw new JSONException("Cannot convert object to JSON. Caught exception of type " + e.getClass().getName() + " : " +e.getLocalizedMessage());
		}
	}

}
