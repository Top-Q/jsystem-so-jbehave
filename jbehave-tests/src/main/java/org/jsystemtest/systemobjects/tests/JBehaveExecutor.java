package org.jsystemtest.systemobjects.tests;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jsystem.framework.ParameterProperties;
import jsystem.framework.TestProperties;
import jsystem.framework.report.Reporter;
import junit.framework.SystemTestCase4;

import org.jbehave.core.embedder.Embedder;
import org.jsystemtest.systemobjects.jbehave.JSystemEmbedder;
import org.jsystemtest.systemobjects.utils.BeanUtils;
import org.junit.Test;

public class JBehaveExecutor extends SystemTestCase4 {

	private String[] stepsPackages = { "org.jsystemtest.systemobjects.steps" };
	private String[] storyPaths = { "my.story" };
	private File storyFile;

	/**
	 * Run all the specified stories using the step classes that are in the
	 * specified package.
	 */
	@Test
	@TestProperties(name = "Run multiple JBehave stories", paramsInclude = { "stepsPackages", "storyPaths" })
	public void runStories() {
		// Checking inputs
		if (null == stepsPackages || stepsPackages.length == 0) {
			report.report("JBehave steps were not specified - skipping execution", Reporter.WARNING);
			return;
		}
		if (null == storyPaths || storyPaths.length == 0) {
			report.report("No stories were specified - skipping execution", Reporter.WARNING);
			return;
		}

		report.report("About to run stories");
		Embedder embedder = new JSystemEmbedder(BeanUtils.findClassesInPackages(stepsPackages));
		embedder.runStoriesAsPaths(Arrays.asList(storyPaths));

	}

	/**
	 * Run a single JBehave story using the step classes that are in the
	 * specified package.
	 */
	@Test
	@TestProperties(name = "Run a single JBehave story", paramsInclude = { "stepsPackages", "storyFile" })
	public void runStory() throws IOException {
		// Checking inputs
		if (null == stepsPackages || stepsPackages.length == 0) {
			report.report("JBehave steps were not specified - skipping execution", Reporter.WARNING);
			return;
		}
		if (null == storyFile || !storyFile.exists()) {
			report.report("Story was not found - skipping execution",
					Reporter.WARNING);
			return;
		}

		report.report("About to run story " + storyFile.getName());
		Embedder embedder = new JSystemEmbedder(BeanUtils.findClassesInPackages(stepsPackages));
		List<String> storyList = new ArrayList<String>();
		storyList.add(storyFile.getCanonicalPath());
		embedder.runStoriesAsPaths(storyList);

	}

	public String[] getStepsPackages() {
		return stepsPackages;
	}

	@ParameterProperties(description = "The packages that holds the step classes")
	public void setStepsPackages(String[] stepsPackages) {
		this.stepsPackages = stepsPackages;
	}

	public String[] getStoryPaths() {
		return storyPaths;
	}

	@ParameterProperties(description = "The location of the story files")
	public void setStoryPaths(String[] storyPaths) {
		this.storyPaths = storyPaths;
	}

	public File getStoryFile() {
		return storyFile;
	}

	@ParameterProperties(description = "JBehave story file")
	public void setStoryFile(File storyFile) {
		this.storyFile = storyFile;
	}

}
