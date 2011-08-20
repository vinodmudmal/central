package edu.yu.einstein.wasp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import edu.yu.einstein.wasp.model.MetaBase;
import edu.yu.einstein.wasp.model.MetaAttribute;

/*
 * implements validation of MetaBase children based on messages_en.properties file
 * @Author Sasha Levchuk
 */

public class MetaValidator {
	 
	public enum Constraint {
		NotEmpty,
		Regexp
	}
	
	private Map<String, String> map=new HashMap<String, String>(); 
		
	public MetaValidator(String... pairs)  {
		if (pairs.length % 2!=0) throw new IllegalStateException("Number of params must be even");
			
		for(int i=0;i<pairs.length;i+=2) {
			map.put(pairs[i],pairs[i+1]);
		}
	
	}
	
	public void validate(List<? extends MetaBase> list, BindingResult result, MetaAttribute.Area area) {
	
		Errors errors=new BindException(result.getTarget(), area.name()); 
	
		for(int i=0;i<list.size();i++) {
			MetaBase meta=list.get(i);
			String constraint=map.get(meta.getK());
			if (constraint==null) continue;

			if (constraint.startsWith(Constraint.Regexp.name()+":")) {
				//TODO: optimize for speed (pre-compile) if used outside of admin pages
				String regexp=constraint.substring(Constraint.Regexp.name().length()+1); 
				Pattern p = Pattern.compile(regexp);
				Matcher m = p.matcher(meta.getV());
				boolean b = m.matches();
				if (!b) {
					errors.rejectValue(area+"Meta["+i+"].k", area+"."+meta.getK().replace(area+".", "")+".error");  
				}

			} else if (constraint.equals(Constraint.NotEmpty.name())){
				if (meta.getV()==null || meta.getV().isEmpty()) {
					String fieldName=area+"Meta["+i+"].k";
					String errorMessageKey=area+"."+meta.getK().replace(area+".", "")+".error";
					errors.rejectValue(fieldName,errorMessageKey );
				}
					
			} else {
				throw new IllegalStateException("Unknown constraint "+constraint+"|"+meta);
			}
		}
		result.addAllErrors(errors);
	}
}
