package edu.yu.einstein.wasp.tiles;

/**
 * Apache Tiles interceptor to make tile definition name available via request attributes
 * 
 *  @author Sasha Levchuk
 */

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WaspTilesView extends org.springframework.web.servlet.view.tiles2.TilesView {
	
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		request.setAttribute("waspViewName", getUrl());
		super.renderMergedOutputModel(model, request, response);
	}

}
