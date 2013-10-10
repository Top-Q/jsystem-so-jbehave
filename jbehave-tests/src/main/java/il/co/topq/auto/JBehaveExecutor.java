package il.co.topq.auto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jsystem.framework.TestProperties;
import jsystem.framework.report.Reporter;
import junit.framework.SystemTestCase4;

import org.jbehave.core.embedder.Embedder;
import org.junit.Test;

public class JBehaveExecutor extends SystemTestCase4 {

	private String[] stepsPackages = {"il.co.topq.auto.steps"};
	private String[] storyPaths = {"my.story"};

	
	/**
	 * 
	 */
	@Test
	@TestProperties(name = "Run jbehave stories", paramsInclude = { "stepsPackages", "storyPaths" })
	public void runStories() throws ClassNotFoundException, IOException {
		// Checking inputs
		if (null == stepsPackages || stepsPackages.length == 0) {
			report.report("JBehave steps were not specified - skipping execution", Reporter.WARNING);
		}
		if (null == storyPaths || storyPaths.length == 0) {
			report.report("No stories were specified - skipping execution", Reporter.WARNING);
		}

		Embedder embedder = new JSystemEmbedder(GetAllClassNames());
		embedder.runStoriesAsPaths(Arrays.asList(storyPaths));

	}
	


	private List<Class<?>> GetAllClassNames() throws ClassNotFoundException, IOException {
		List<Class<?>> classList = new ArrayList<Class<?>>();
		for (String packageName : stepsPackages){
			for (Class<?> clazz : ClassLoaderUtils.getClasses(packageName)){
				classList.add(clazz);
			}
		}
		return classList;
	}


	public String[] getStepsPackages() {
		return stepsPackages;
	}

	public void setStepsPackages(String[] stepsPackages) {
		this.stepsPackages = stepsPackages;
	}

	public String[] getStoryPaths() {
		return storyPaths;
	}

	public void setStoryPaths(String[] storyPaths) {
		this.storyPaths = storyPaths;
	}

	

}
