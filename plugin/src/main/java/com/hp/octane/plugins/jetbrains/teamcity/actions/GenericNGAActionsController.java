package com.hp.octane.plugins.jetbrains.teamcity.actions;

import com.hp.nga.integrations.SDKManager;
import com.hp.nga.integrations.dto.DTOFactory;
import com.hp.nga.integrations.dto.connectivity.NGAHttpMethod;
import com.hp.nga.integrations.dto.connectivity.NGAResultAbridged;
import com.hp.nga.integrations.dto.connectivity.NGATaskAbridged;
import com.hp.nga.integrations.api.TasksProcessor;
import jetbrains.buildServer.serverSide.SBuildServer;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by lazara on 07/02/2016.
 */

public class GenericNGAActionsController implements Controller {
	private static final DTOFactory dtoFactory = DTOFactory.getInstance();

	private GenericNGAActionsController(@NotNull SBuildServer buildServer) {
	}

	@Override
	public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {

		NGAHttpMethod method = null;
		if ("post".equals(req.getMethod().toLowerCase())) {
			method = NGAHttpMethod.POST;
		} else if ("get".equals(req.getMethod().toLowerCase())) {
			method = NGAHttpMethod.GET;
		} else if ("put".equals(req.getMethod().toLowerCase())) {
			method = NGAHttpMethod.PUT;
		} else if ("delete".equals(req.getMethod().toLowerCase())) {
			method = NGAHttpMethod.DELETE;
		}
		if (method != null) {
			NGATaskAbridged ngaTaskAbridged = dtoFactory.newDTO(NGATaskAbridged.class)
					.setId(UUID.randomUUID().toString())
					.setMethod(method)
					.setUrl(req.getRequestURI())
					.setBody("");
			TasksProcessor taskProcessor = SDKManager.getService(TasksProcessor.class);
			NGAResultAbridged result = taskProcessor.execute(ngaTaskAbridged);
			res.setStatus(result.getStatus());
			try {
				res.getWriter().write(result.getBody());
			} catch (IOException e) {
				res.setStatus(501);
				e.printStackTrace();
			}
		} else {
			res.setStatus(501);
		}
		return null;
	}
}